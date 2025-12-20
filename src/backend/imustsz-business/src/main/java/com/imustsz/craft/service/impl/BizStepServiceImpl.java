package com.imustsz.craft.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.imustsz.craft.mapper.BizStepMapper;
import com.imustsz.craft.domain.BizStep;
import com.imustsz.craft.service.IBizStepService;

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

    /**
     * 查询工步信息
     * 
     * @param id 工步信息主键
     * @return 工步信息
     */
    @Override
    public BizStep selectBizStepById(Long id)
    {
        return bizStepMapper.selectBizStepById(id);
    }

    /**
     * 查询工步信息列表
     * 
     * @param bizStep 工步信息
     * @return 工步信息
     */
    @Override
    public List<BizStep> selectBizStepList(BizStep bizStep)
    {
        return bizStepMapper.selectBizStepList(bizStep);
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
    public int updateBizStep(BizStep bizStep)
    {
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
}
