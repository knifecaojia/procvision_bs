package com.imustsz.craft.service.impl;


import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.imustsz.common.core.redis.RedisCache;
import com.imustsz.common.exception.ServiceException;
import com.imustsz.craft.domain.BizCraftFilter;
import com.imustsz.craft.mapper.BizCraftFilterMapper;
import com.imustsz.craft.service.IBizCraftFilterService;

@Service
public class BizCraftFilterServiceImpl implements IBizCraftFilterService {

    private static final Logger log = LoggerFactory.getLogger(BizCraftFilterServiceImpl.class);

    private static final String CACHE_KEY = "craft:filter:process:rule";

    @Autowired
    private BizCraftFilterMapper bizCraftFilterMapper;

    @Autowired
    private RedisCache redisCache;

    @PostConstruct
    public void init() {
        refreshCraftFilterCache();
    }

    @Override
    public BizCraftFilter selectBizCraftFilterById(Integer id) {
        return bizCraftFilterMapper.selectBizCraftFilterById(id);
    }

    @Override
    public List<BizCraftFilter> selectBizCraftFilterList(BizCraftFilter bizCraftFilter) {
        return bizCraftFilterMapper.selectBizCraftFilterList(bizCraftFilter);
    }

    @Override
    public int insertBizCraftFilter(BizCraftFilter bizCraftFilter) {
        validateRule(bizCraftFilter);
        int rows = bizCraftFilterMapper.insertBizCraftFilter(bizCraftFilter);
        if (rows > 0) {
            refreshSubCache(bizCraftFilter.getProductType());
        }
        return rows;
    }

    @Override
    public int updateBizCraftFilter(BizCraftFilter bizCraftFilter) {
        validateRule(bizCraftFilter);
        BizCraftFilter old = bizCraftFilterMapper.selectBizCraftFilterById(bizCraftFilter.getId());
        int rows = bizCraftFilterMapper.updateBizCraftFilter(bizCraftFilter);
        if (rows > 0) {
            if (old != null) {
                refreshSubCache(old.getProductType());
            }
            refreshSubCache(bizCraftFilter.getProductType());
        }
        return rows;
    }

    @Override
    public int deleteBizCraftFilterByIds(Integer[] ids) {
        int rows = bizCraftFilterMapper.deleteBizCraftFilterByIds(ids);
        if (rows > 0) {
            refreshCraftFilterCache();
        }
        return rows;
    }

    @Override
    public int deleteBizCraftFilterById(Integer id) {
        BizCraftFilter rule = bizCraftFilterMapper.selectBizCraftFilterById(id);
        int rows = bizCraftFilterMapper.deleteBizCraftFilterById(id);
        if (rows > 0 && rule != null) {
            refreshSubCache(rule.getProductType());
        }
        return rows;
    }

    @Override
    public int updateStatus(Integer id, Integer isEnabled, String updateBy) {
        BizCraftFilter rule = bizCraftFilterMapper.selectBizCraftFilterById(id);
        int rows = bizCraftFilterMapper.updateStatus(id, isEnabled, updateBy);
        if (rows > 0 && rule != null) {
            refreshSubCache(rule.getProductType());
        }
        return rows;
    }

    @Override
    public void refreshCraftFilterCache() {
        BizCraftFilter query = new BizCraftFilter();
        query.setIsEnabled(1);
        List<BizCraftFilter> allRules = bizCraftFilterMapper.selectBizCraftFilterList(query);

        Map<String, List<BizCraftFilter>> map = allRules.stream()
                .collect(Collectors.groupingBy(item -> buildHashKey(item.getProductType())));

        redisCache.deleteObject(CACHE_KEY);
        if (!map.isEmpty()) {
            redisCache.setCacheMap(CACHE_KEY, map);
        }
        log.info("【工艺过滤规则】Redis 缓存已全量刷新，产品类型数: {}", map.size());
    }

    private void refreshSubCache(Integer productType) {
        String hKey = buildHashKey(productType);
        BizCraftFilter query = new BizCraftFilter();
        query.setProductType(productType);

        query.setIsEnabled(1);
        List<BizCraftFilter> activeRules = bizCraftFilterMapper.selectBizCraftFilterList(query);

        if (activeRules != null && !activeRules.isEmpty()) {
            redisCache.setCacheMapValue(CACHE_KEY, hKey, activeRules);
        } else {
            redisCache.deleteCacheMapValue(CACHE_KEY, hKey);
        }
    }

    @Override
    public List<BizCraftFilter> getRulesFromCache(Integer productType) {
        String hKey = buildHashKey(productType);
        List<BizCraftFilter> rules = redisCache.getCacheMapValue(CACHE_KEY, hKey);
        if (rules == null) {
            BizCraftFilter query = new BizCraftFilter();
            query.setProductType(productType);

            query.setIsEnabled(1);
            rules = bizCraftFilterMapper.selectBizCraftFilterList(query);
            if (rules != null && !rules.isEmpty()) {
                redisCache.setCacheMapValue(CACHE_KEY, hKey, rules);
            }
        }
        return rules != null ? rules : Collections.emptyList();
    }

    @Override
    public boolean isProcessFiltered(Integer productType, String processName) {
        if (productType == null || productType < 0 || productType > 2
                || processName == null || processName.trim().isEmpty()) {
            return false;
        }
        for (BizCraftFilter rule : getRulesFromCache(productType)) {
            String keyword = rule.getKeyword();
            Integer mode = rule.getMatchMode();
            if (!productType.equals(rule.getProductType()) || !Integer.valueOf(1).equals(rule.getIsEnabled())
                    || Integer.valueOf(1).equals(rule.getIsDeleted()) || keyword == null || keyword.trim().isEmpty()) {
                continue;
            }
            keyword = keyword.trim();
            if (Integer.valueOf(1).equals(mode) && processName.trim().equalsIgnoreCase(keyword)) {
                return true;
            } else if (Integer.valueOf(2).equals(mode) && processName.contains(keyword)) {
                return true;
            } else if (Integer.valueOf(3).equals(mode)) {
                try {
                    if (Pattern.compile(keyword).matcher(processName).find()) {
                        return true;
                    }
                } catch (java.util.regex.PatternSyntaxException e) {
                    log.error("正则规则解析失败: {}", keyword);
                }
            }
        }
        return false;
    }

    private void validateRule(BizCraftFilter rule) {
        if (rule.getProductType() == null || rule.getProductType() < 0 || rule.getProductType() > 2) {
            throw new ServiceException("请选择有效的产品类型");
        }
        if (rule.getKeyword() == null || rule.getKeyword().trim().isEmpty()) {
            throw new ServiceException("工序关键词不能为空");
        }
        rule.setKeyword(rule.getKeyword().trim());
        if (rule.getMatchMode() == null || rule.getMatchMode() < 1 || rule.getMatchMode() > 3) {
            throw new ServiceException("请选择有效的匹配模式");
        }
        if (Integer.valueOf(3).equals(rule.getMatchMode())) {
            try {
                Pattern.compile(rule.getKeyword());
            } catch (java.util.regex.PatternSyntaxException e) {
                throw new ServiceException("工序关键词不是有效的正则表达式");
            }
        }
        // 保留数据库兼容字段，不再作为匹配的前置条件。
        rule.setProcessName(rule.getKeyword());
    }

    private String buildHashKey(Integer productType) {
        return String.valueOf(productType);
    }
}
