package com.imustsz.craft.service;

import java.util.List;

import com.imustsz.common.core.domain.AjaxResult;
import com.imustsz.craft.domain.BizStep;
import com.imustsz.craft.domain.dto.GuideInfoDTO;

/**
 * 工步信息Service接口
 * 
 * @author imustsz
 * @date 2025-12-19
 */
public interface IBizStepService 
{
    /**
     * 查询工步信息
     * 
     * @param id 工步信息主键
     * @return 工步信息
     */
    public BizStep selectBizStepById(Long id) throws Exception;

    /**
     * 查询工步信息列表
     * 
     * @param bizStep 工步信息
     * @return 工步信息集合
     */
    public List<BizStep> selectBizStepList(BizStep bizStep) throws Exception;

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
    public int updateBizStep(BizStep bizStep) throws Exception;

    /**
     * 批量删除工步信息
     * 
     * @param ids 需要删除的工步信息主键集合
     * @return 结果
     */
    public int deleteBizStepByIds(Long[] ids) throws Exception;

    /**
     * 删除工步信息信息
     * 
     * @param id 工步信息主键
     * @return 结果
     */
    public int deleteBizStepById(Long id);

    int bindImgAndInfo(GuideInfoDTO guideInfoDTO);

    int deleteStepByCodeAndProcessId(String code, Long processId);

    String getObjectNameById(Long id);

    List<BizStep> selectBizStepListOri(BizStep bizStep);

    BizStep selectBizStepOriById(Long id);
}
