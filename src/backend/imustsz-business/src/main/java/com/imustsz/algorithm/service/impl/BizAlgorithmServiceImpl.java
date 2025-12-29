package com.imustsz.algorithm.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.imustsz.cilent.domain.vo.AlgorithmVO;
import com.imustsz.common.utils.bean.MinioUtils;
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

    @Autowired
    private MinioUtils minioUtils;

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
    public List<BizAlgorithm> selectBizAlgorithmList(BizAlgorithm bizAlgorithm) throws Exception {
        List<BizAlgorithm> bizAlgorithms = bizAlgorithmMapper.selectBizAlgorithmList(bizAlgorithm);
        for (BizAlgorithm bizAlg : bizAlgorithms) {
            try {
                bizAlg.setUrl(minioUtils.getPresignedUrl(bizAlg.getObjectName()));
            } catch (Exception e) {
                throw new Exception("获取预签名URL失败");
            }
        }
        return bizAlgorithms;
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

    @Override
    public List<AlgorithmVO> getAlgorithmVOList() throws Exception {
        List<BizAlgorithm> algorithmVOList = bizAlgorithmMapper.getAlgorithmVOList();
        List<AlgorithmVO> algorithmVOS = new ArrayList<>();
        for (BizAlgorithm algorithmVO : algorithmVOList) {
            try {
                AlgorithmVO algorithm = new AlgorithmVO();
                algorithm.setId(algorithmVO.getId());
                algorithm.setName(algorithmVO.getName());
                algorithm.setVersion(algorithmVO.getVersion());
                algorithm.setUrl(minioUtils.getPresignedUrl(algorithmVO.getObjectName()));
                algorithm.setDesc(algorithmVO.getDesc());
                algorithmVOS.add(algorithm);
            } catch (Exception e) {
                throw new Exception("获取预签名URL失败");
            }
        }
        return algorithmVOS;
    }
}
