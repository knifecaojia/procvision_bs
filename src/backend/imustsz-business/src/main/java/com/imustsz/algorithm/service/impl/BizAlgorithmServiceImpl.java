package com.imustsz.algorithm.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.imustsz.algorithm.mapper.BizAlgorithmMapper;
import com.imustsz.algorithm.domain.BizAlgorithm;
import com.imustsz.algorithm.service.IBizAlgorithmService;

/**
 * 算法Service业务层处理
 * 
 * @author imustsz
 * @date 2025-12-22
 */
@Service
public class BizAlgorithmServiceImpl implements IBizAlgorithmService 
{
    @Autowired
    private BizAlgorithmMapper bizAlgorithmMapper;

    /**
     * 查询算法
     * 
     * @param id 算法主键
     * @return 算法
     */
    @Override
    public BizAlgorithm selectBizAlgorithmById(Long id)
    {
        return bizAlgorithmMapper.selectBizAlgorithmById(id);
    }

    /**
     * 查询算法列表
     * 
     * @param bizAlgorithm 算法
     * @return 算法
     */
    @Override
    public List<BizAlgorithm> selectBizAlgorithmList(BizAlgorithm bizAlgorithm)
    {
        return bizAlgorithmMapper.selectBizAlgorithmList(bizAlgorithm);
    }

    /**
     * 新增算法
     * 
     * @param bizAlgorithm 算法
     * @return 结果
     */
    @Override
    public int insertBizAlgorithm(BizAlgorithm bizAlgorithm)
    {
        return bizAlgorithmMapper.insertBizAlgorithm(bizAlgorithm);
    }

    /**
     * 修改算法
     * 
     * @param bizAlgorithm 算法
     * @return 结果
     */
    @Override
    public int updateBizAlgorithm(BizAlgorithm bizAlgorithm)
    {
        return bizAlgorithmMapper.updateBizAlgorithm(bizAlgorithm);
    }

    /**
     * 批量删除算法
     * 
     * @param ids 需要删除的算法主键
     * @return 结果
     */
    @Override
    public int deleteBizAlgorithmByIds(Long[] ids)
    {
        return bizAlgorithmMapper.deleteBizAlgorithmByIds(ids);
    }

    /**
     * 删除算法信息
     * 
     * @param id 算法主键
     * @return 结果
     */
    @Override
    public int deleteBizAlgorithmById(Long id)
    {
        return bizAlgorithmMapper.deleteBizAlgorithmById(id);
    }
}
