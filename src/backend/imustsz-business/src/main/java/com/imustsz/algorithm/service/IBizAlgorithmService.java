package com.imustsz.algorithm.service;

import java.util.List;
import com.imustsz.algorithm.domain.BizAlgorithm;
import com.imustsz.cilent.domain.vo.AlgorithmVO;

/**
 * 算法Service接口
 * 
 * @author imustsz
 * @date 2025-12-22
 */
public interface IBizAlgorithmService 
{
    /**
     * 查询算法
     * 
     * @param id 算法主键
     * @return 算法
     */
    public BizAlgorithm selectBizAlgorithmById(Long id);

    /**
     * 查询算法列表
     * 
     * @param bizAlgorithm 算法
     * @return 算法集合
     */
    public List<BizAlgorithm> selectBizAlgorithmList(BizAlgorithm bizAlgorithm) throws Exception;

    /**
     * 新增算法
     * 
     * @param bizAlgorithm 算法
     * @return 结果
     */
    public int insertBizAlgorithm(BizAlgorithm bizAlgorithm);

    /**
     * 修改算法
     * 
     * @param bizAlgorithm 算法
     * @return 结果
     */
    public int updateBizAlgorithm(BizAlgorithm bizAlgorithm);

    /**
     * 批量删除算法
     * 
     * @param ids 需要删除的算法主键集合
     * @return 结果
     */
    public int deleteBizAlgorithmByIds(Long[] ids);

    /**
     * 删除算法信息
     * 
     * @param id 算法主键
     * @return 结果
     */
    public int deleteBizAlgorithmById(Long id);

    List<AlgorithmVO> getAlgorithmVOList() throws Exception;
}
