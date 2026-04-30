package com.imustsz.collect.service;

import com.imustsz.collect.domain.BizDataset;

import java.util.List;

public interface IBizDatasetService
{
    /**
     * 查询数据采集
     *
     * @param id 数据采集主键
     * @return 数据采集
     */
    public BizDataset selectBizDatasetById(Long id);

    /**
     * 查询数据采集列表
     *
     * @param BizDataset 数据采集
     * @return 数据采集集合
     */
    public List<BizDataset> selectBizDatasetList(BizDataset BizDataset);

    /**
     * 新增数据采集
     *
     * @param BizDataset 数据采集
     * @return 结果
     */
    public int insertBizDataset(BizDataset BizDataset);

    /**
     * 修改数据采集
     *
     * @param BizDataset 数据采集
     * @return 结果
     */
    public int updateBizDataset(BizDataset BizDataset);

    /**
     * 批量删除数据采集
     *
     * @param ids 需要删除的数据采集主键集合
     * @return 结果
     */
    public int deleteBizDatasetByIds(Long[] ids);

    /**
     * 删除数据采集信息
     *
     * @param id 数据采集主键
     * @return 结果
     */
    public int deleteBizDatasetById(Long id);
}
