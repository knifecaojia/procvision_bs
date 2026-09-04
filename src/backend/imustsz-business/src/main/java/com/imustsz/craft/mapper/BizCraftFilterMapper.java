package com.imustsz.craft.mapper;


import java.util.List;

import com.imustsz.craft.domain.BizCraftFilter;
import org.apache.ibatis.annotations.Param;

/**
 * 工序步骤工艺过滤规则 Mapper 接口
 *
 * @author ruoyi
 */
public interface BizCraftFilterMapper {
    /**
     * 查询过滤规则详情
     *
     * @param id 规则主键
     * @return 规则详情
     */
    BizCraftFilter selectBizCraftFilterById(Integer id);

    /**
     * 查询过滤规则列表
     *
     * @param bizCraftFilter 查询条件
     * @return 规则集合
     */
    List<BizCraftFilter> selectBizCraftFilterList(BizCraftFilter bizCraftFilter);

    /**
     * 新增过滤规则
     *
     * @param bizCraftFilter 规则实体
     * @return 影响行数
     */
    int insertBizCraftFilter(BizCraftFilter bizCraftFilter);

    /**
     * 修改过滤规则
     *
     * @param bizCraftFilter 规则实体
     * @return 影响行数
     */
    int updateBizCraftFilter(BizCraftFilter bizCraftFilter);

    /**
     * 单条逻辑删除
     *
     * @param id 规则ID
     * @return 影响行数
     */
    int deleteBizCraftFilterById(Integer id);

    /**
     * 批量逻辑删除
     *
     * @param ids 需要删除的规则ID数组
     * @return 影响行数
     */
    int deleteBizCraftFilterByIds(Integer[] ids);

    /**
     * 修改启停状态
     *
     * @param id 规则ID
     * @param isEnabled 状态值
     * @param updateBy 更新人
     * @return 影响行数
     */
    int updateStatus(@Param("id") Integer id, @Param("isEnabled") Integer isEnabled, @Param("updateBy") String updateBy);
}