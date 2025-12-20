package com.imustsz.craft.mapper;

import java.util.List;
import com.imustsz.craft.domain.BizStep;

/**
 * 工步信息Mapper接口
 * 
 * @author imustsz
 * @date 2025-12-19
 */
public interface BizStepMapper 
{
    /**
     * 查询工步信息
     * 
     * @param id 工步信息主键
     * @return 工步信息
     */
    public BizStep selectBizStepById(Long id);

    /**
     * 查询工步信息列表
     * 
     * @param bizStep 工步信息
     * @return 工步信息集合
     */
    public List<BizStep> selectBizStepList(BizStep bizStep);

    /**
     * 新增工步信息
     * 
     * @param bizStep 工步信息
     * @return 结果
     */
    public int insertBizStep(BizStep bizStep);

    /**
     * 修改工步信息
     * 
     * @param bizStep 工步信息
     * @return 结果
     */
    public int updateBizStep(BizStep bizStep);

    /**
     * 删除工步信息
     * 
     * @param id 工步信息主键
     * @return 结果
     */
    public int deleteBizStepById(Long id);

    /**
     * 批量删除工步信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBizStepByIds(Long[] ids);
}
