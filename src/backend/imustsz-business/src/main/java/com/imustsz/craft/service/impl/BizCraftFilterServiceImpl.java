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
import com.imustsz.craft.domain.BizCraftFilter;
import com.imustsz.craft.mapper.BizCraftFilterMapper;
import com.imustsz.craft.service.IBizCraftFilterService;

@Service
public class BizCraftFilterServiceImpl implements IBizCraftFilterService {

    private static final Logger log = LoggerFactory.getLogger(BizCraftFilterServiceImpl.class);

    private static final String CACHE_KEY = "craft:filter:rule";

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
        int rows = bizCraftFilterMapper.insertBizCraftFilter(bizCraftFilter);
        if (rows > 0) {
            refreshSubCache(bizCraftFilter.getProductType(), bizCraftFilter.getProcessName());
        }
        return rows;
    }

    @Override
    public int updateBizCraftFilter(BizCraftFilter bizCraftFilter) {
        BizCraftFilter old = bizCraftFilterMapper.selectBizCraftFilterById(bizCraftFilter.getId());
        int rows = bizCraftFilterMapper.updateBizCraftFilter(bizCraftFilter);
        if (rows > 0) {
            if (old != null) {
                refreshSubCache(old.getProductType(), old.getProcessName());
            }
            refreshSubCache(bizCraftFilter.getProductType(), bizCraftFilter.getProcessName());
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
            refreshSubCache(rule.getProductType(), rule.getProcessName());
        }
        return rows;
    }

    @Override
    public int updateStatus(Integer id, Integer isEnabled, String updateBy) {
        BizCraftFilter rule = bizCraftFilterMapper.selectBizCraftFilterById(id);
        int rows = bizCraftFilterMapper.updateStatus(id, isEnabled, updateBy);
        if (rows > 0 && rule != null) {
            refreshSubCache(rule.getProductType(), rule.getProcessName());
        }
        return rows;
    }

    @Override
    public void refreshCraftFilterCache() {
        BizCraftFilter query = new BizCraftFilter();
        query.setIsEnabled(1);
        List<BizCraftFilter> allRules = bizCraftFilterMapper.selectBizCraftFilterList(query);

        Map<String, List<BizCraftFilter>> map = allRules.stream()
                .collect(Collectors.groupingBy(item -> buildHashKey(item.getProductType(), item.getProcessName())));

        redisCache.deleteObject(CACHE_KEY);
        if (!map.isEmpty()) {
            redisCache.setCacheMap(CACHE_KEY, map);
        }
        log.info("【工艺过滤规则】Redis 缓存已全量刷新，工序节点数: {}", map.size());
    }

    private void refreshSubCache(Integer productType, String processName) {
        String hKey = buildHashKey(productType, processName);
        BizCraftFilter query = new BizCraftFilter();
        query.setProductType(productType);
        query.setProcessName(processName);
        query.setIsEnabled(1);
        List<BizCraftFilter> activeRules = bizCraftFilterMapper.selectBizCraftFilterList(query);

        if (activeRules != null && !activeRules.isEmpty()) {
            redisCache.setCacheMapValue(CACHE_KEY, hKey, activeRules);
        } else {
            redisCache.deleteCacheMapValue(CACHE_KEY, hKey);
        }
    }

    @Override
    public List<BizCraftFilter> getRulesFromCache(Integer productType, String processName) {
        String hKey = buildHashKey(productType, processName);
        List<BizCraftFilter> rules = redisCache.getCacheMapValue(CACHE_KEY, hKey);
        if (rules == null) {
            BizCraftFilter query = new BizCraftFilter();
            query.setProductType(productType);
            query.setProcessName(processName);
            query.setIsEnabled(1);
            rules = bizCraftFilterMapper.selectBizCraftFilterList(query);
            if (rules != null && !rules.isEmpty()) {
                redisCache.setCacheMapValue(CACHE_KEY, hKey, rules);
            }
        }
        return rules != null ? rules : Collections.emptyList();
    }

    @Override
    public boolean isStepFiltered(Integer productType, String processName, String stepContent) {
        if (stepContent == null || stepContent.trim().isEmpty()) {
            return false;
        }
        List<BizCraftFilter> rules = getRulesFromCache(productType, processName);
        if (rules.isEmpty()) {
            return false;
        }

        for (BizCraftFilter rule : rules) {
            String kw = rule.getKeyword();
            Integer mode = rule.getMatchMode();

            if (mode == 1 && stepContent.trim().equalsIgnoreCase(kw.trim())) {
                return true;
            } else if (mode == 2 && stepContent.contains(kw)) {
                return true;
            } else if (mode == 3) {
                try {
                    if (Pattern.compile(kw).matcher(stepContent).find()) {
                        return true;
                    }
                } catch (Exception e) {
                    log.error("正则规则解析失败: {}", kw);
                }
            }
        }
        return false;
    }

    private String buildHashKey(Integer productType, String processName) {
        return productType + ":" + processName;
    }
}