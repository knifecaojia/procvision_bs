package com.imustsz.craft.service.impl;

import java.util.List;

import com.imustsz.common.utils.bean.MinioUtils;
import com.imustsz.craft.domain.dto.GuideInfoDTO;
import com.imustsz.framework.aspectj.AutoFill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.imustsz.craft.mapper.BizStepMapper;
import com.imustsz.craft.domain.BizStep;
import com.imustsz.craft.service.IBizStepService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 工步信息Service业务层处理
 * 
 * @author imustsz
 * @date 2025-12-19
 */
@Service
public class BizStepServiceImpl implements IBizStepService 
{
    @Autowired
    private BizStepMapper bizStepMapper;

    @Autowired
    private MinioUtils minioUtils;

    /**
     * 查询工步信息
     * 
     * @param id 工步信息主键
     * @return 工步信息
     */
    @Override
    public BizStep selectBizStepById(Long id) throws Exception {
        BizStep step = bizStepMapper.selectBizStepById(id);
        if (step.getGuideMapUrl() != null) {
            step.setGuideMapUrl(getLabeledUrl(step.getGuideMapUrl(), 0));
        }
        return step;
    }

    /**
     * 查询工步信息列表
     * 
     * @param bizStep 工步信息
     * @return 工步信息
     */
    @Override
    public List<BizStep> selectBizStepList(BizStep bizStep) throws Exception {
        List<BizStep> bizSteps = bizStepMapper.selectBizStepList(bizStep);
        for (BizStep step : bizSteps) {
            if (step.getGuideMapUrl() != null) {
                step.setGuideMapUrl(getLabeledUrl(step.getGuideMapUrl(), 1));
            }
        }

        return bizSteps;
    }

    private String getLabeledUrl(String urls, Integer index) throws Exception {
        String[] split = urls.substring(1, urls.length() - 1).replace(" ", "").replace("\"", "").split(",");
        return minioUtils.getPresignedUrl(split[index]);
    }

    /**
     * 新增工步信息
     * 
     * @param bizStep 工步信息
     * @return 结果
     */
    @Override
    public int insertBizStep(BizStep bizStep)
    {
        return bizStepMapper.insertBizStep(bizStep);
    }

    /**
     * 修改工步信息
     * 
     * @param bizStep 工步信息
     * @return 结果
     */
    @Override
    @Transactional
    public int updateBizStep(BizStep bizStep) throws Exception {
        BizStep step = bizStepMapper.getStepById(bizStep.getId());
        if (step.getGuideMapUrl() != null && bizStep.getGuideMapUrl() != null){
            String[] objectName = step.getGuideMapUrl().substring(1, step.getGuideMapUrl().length() - 1).replace(" ", "").replace("\"", "").split(",");
            for (String s : objectName)
                minioUtils.deleteFile(s);
        }else if (step.getGuideMapUrl() != null && bizStep.getGuideMapUrl() == null)
            bizStep.setGuideMapUrl(step.getGuideMapUrl());
        return bizStepMapper.updateBizStep(bizStep);
    }

    /**
     * 批量删除工步信息
     * 
     * @param ids 需要删除的工步信息主键
     * @return 结果
     */
    @Override
    public int deleteBizStepByIds(Long[] ids)
    {
        return bizStepMapper.deleteBizStepByIds(ids);
    }

    /**
     * 删除工步信息信息
     * 
     * @param id 工步信息主键
     * @return 结果
     */
    @Override
    public int deleteBizStepById(Long id)
    {
        return bizStepMapper.deleteBizStepById(id);
    }

    @Override
    @Transactional
    public int bindImgAndInfo(GuideInfoDTO guideInfoDTO) {
        BizStep step = new BizStep();
        step.setId(guideInfoDTO.getId());
        step.setCoordsInfo(guideInfoDTO.getCoordsInfo());
        step.setGuideMapUrl(guideInfoDTO.getObjectName());
        return bizStepMapper.updateBizStep(step);
    }

    @Override
    public int deleteStepByCodeAndProcessId(String code, Long processId) {
        return bizStepMapper.deleteStepByCodeAndProcessId(code, processId);
    }

    @Override
    public String getObjectNameById(Long id) {
        BizStep step = bizStepMapper.selectBizStepById(id);
        return step.getGuideMapUrl();
    }
}
