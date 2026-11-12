package com.imustsz.craft.service.impl;


import java.util.*;
import java.util.regex.Pattern;
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
        if (bizCraftFilter.getProductType() == null) {
            bizCraftFilter.setProductType(0);
        }
        int rows = bizCraftFilterMapper.insertBizCraftFilter(bizCraftFilter);
        if (rows > 0) {
            refreshCraftFilterCache();
        }
        return rows;
    }

    @Override
    public int updateBizCraftFilter(BizCraftFilter bizCraftFilter) {
        validateRule(bizCraftFilter);
        int rows = bizCraftFilterMapper.updateBizCraftFilter(bizCraftFilter);
        if (rows > 0) {
            refreshCraftFilterCache();
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
        int rows = bizCraftFilterMapper.deleteBizCraftFilterById(id);
        if (rows > 0) {
            refreshCraftFilterCache();
        }
        return rows;
    }

    @Override
    public int updateStatus(Integer id, Integer isEnabled, String updateBy) {
        int rows = bizCraftFilterMapper.updateStatus(id, isEnabled, updateBy);
        if (rows > 0) {
            refreshCraftFilterCache();
        }
        return rows;
    }

    @Override
    public void refreshCraftFilterCache() {
        BizCraftFilter query = new BizCraftFilter();
        query.setIsEnabled(1);
        List<BizCraftFilter> activeRules = bizCraftFilterMapper.selectBizCraftFilterList(query);

        redisCache.deleteObject(CACHE_KEY);
        if (activeRules != null && !activeRules.isEmpty()) {
            redisCache.setCacheObject(CACHE_KEY, activeRules);
        }
        log.info("【工艺过滤规则】Redis 缓存已全量刷新，工序关键词数: {}", activeRules == null ? 0 : activeRules.size());
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<BizCraftFilter> getRulesFromCache() {
        List<BizCraftFilter> rules = redisCache.getCacheObject(CACHE_KEY);
        if (rules == null) {
            BizCraftFilter query = new BizCraftFilter();
            query.setIsEnabled(1);
            rules = bizCraftFilterMapper.selectBizCraftFilterList(query);
            if (rules != null && !rules.isEmpty()) {
                redisCache.setCacheObject(CACHE_KEY, rules);
            }
        }
        return rules != null ? rules : Collections.emptyList();
    }

    @Override
    public boolean isProcessFiltered(String processName) {
        if (processName == null || processName.trim().isEmpty()) {
            return false;
        }
        for (BizCraftFilter rule : getRulesFromCache()) {
            String keyword = rule.getKeyword();
            Integer mode = rule.getMatchMode();
            if (!Integer.valueOf(1).equals(rule.getIsEnabled())
                    || Integer.valueOf(1).equals(rule.getIsDeleted())
                    || keyword == null || keyword.trim().isEmpty()) {
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
        if (rule.getKeyword() == null || rule.getKeyword().trim().isEmpty()) {
            throw new ServiceException("工序关键词不能为空");
        }
        rule.setKeyword(rule.getKeyword().trim());
        BizCraftFilter existing = bizCraftFilterMapper.selectBizCraftFilterByKeyword(rule.getKeyword());
        if (existing != null && !existing.getId().equals(rule.getId())) {
            throw new ServiceException("工序关键词已存在，请勿重复新增");
        }
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

}
