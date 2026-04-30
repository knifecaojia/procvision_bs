package com.imustsz.collect.mapper;

import com.imustsz.collect.domain.BizDataset;
import com.imustsz.framework.aspectj.AutoFill;

import java.util.List;

/**
 * 数据采集Mapper接口
 * 
 * @author imustsz
 * @date 2026-01-20
 */
public interface BizDatasetMapper 
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
    @AutoFill("insert")
    public int insertBizDataset(BizDataset BizDataset);

    /**
     * 修改数据采集
     * 
     * @param BizDataset 数据采集
     * @return 结果
     */
    @AutoFill("update")
    public int updateBizDataset(BizDataset BizDataset);

    /**
     * 删除数据采集
     * 
     * @param id 数据采集主键
     * @return 结果
     */
    public int deleteBizDatasetById(Long id);

    /**
     * 批量删除数据采集
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBizDatasetByIds(Long[] ids);
}
