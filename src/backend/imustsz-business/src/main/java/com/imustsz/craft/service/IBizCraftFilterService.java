package com.imustsz.craft.service;


import com.imustsz.craft.domain.BizCraftFilter;

import java.util.List;


/**
 * 工序步骤工艺过滤规则 Service 接口
 *
 * @author ruoyi
 */
public interface IBizCraftFilterService {
    /**
     * 查询规则详情
     */
    BizCraftFilter selectBizCraftFilterById(Integer id);

    /**
     * 查询规则列表
     */
    List<BizCraftFilter> selectBizCraftFilterList(BizCraftFilter bizCraftFilter);

    /**
     * 新增规则
     */
    int insertBizCraftFilter(BizCraftFilter bizCraftFilter);

    /**
     * 修改规则
     */
    int updateBizCraftFilter(BizCraftFilter bizCraftFilter);

    /**
     * 批量删除规则
     */
    int deleteBizCraftFilterByIds(Integer[] ids);

    /**
     * 删除规则信息
     */
    int deleteBizCraftFilterById(Integer id);

    /**
     * 修改启用状态
     */
    int updateStatus(Integer id, Integer isEnabled, String updateBy);

    /**
     * 重载/刷新全部过滤规则缓存
     */
    void refreshCraftFilterCache();

    /**
     * 根据产品类型和工序名称从缓存获取生效的规则列表
     */
    List<BizCraftFilter> getRulesFromCache(Integer productType, String processName);

    /**
     * 工艺步骤是否命中过滤判定
     * @param productType 产品类型
     * @param processName 工序名
     * @param stepContent 步骤/工步内容文本
     * @return true: 需要过滤掉; false: 保留
     */
    boolean isStepFiltered(Integer productType, String processName, String stepContent);
}
