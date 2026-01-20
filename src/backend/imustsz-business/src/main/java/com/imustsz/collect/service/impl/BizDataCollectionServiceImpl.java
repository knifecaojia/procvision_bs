package com.imustsz.collect.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.imustsz.collect.mapper.BizDataCollectionMapper;
import com.imustsz.collect.domain.BizDataCollection;
import com.imustsz.collect.service.IBizDataCollectionService;

/**
 * 数据采集Service业务层处理
 * 
 * @author imustsz
 * @date 2026-01-20
 */
@Service
public class BizDataCollectionServiceImpl implements IBizDataCollectionService 
{
    @Autowired
    private BizDataCollectionMapper bizDataCollectionMapper;

    /**
     * 查询数据采集
     * 
     * @param id 数据采集主键
     * @return 数据采集
     */
    @Override
    public BizDataCollection selectBizDataCollectionById(Long id)
    {
        return bizDataCollectionMapper.selectBizDataCollectionById(id);
    }

    /**
     * 查询数据采集列表
     * 
     * @param bizDataCollection 数据采集
     * @return 数据采集
     */
    @Override
    public List<BizDataCollection> selectBizDataCollectionList(BizDataCollection bizDataCollection)
    {
        return bizDataCollectionMapper.selectBizDataCollectionList(bizDataCollection);
    }

    /**
     * 新增数据采集
     * 
     * @param bizDataCollection 数据采集
     * @return 结果
     */
    @Override
    public int insertBizDataCollection(BizDataCollection bizDataCollection)
    {
        return bizDataCollectionMapper.insertBizDataCollection(bizDataCollection);
    }

    /**
     * 修改数据采集
     * 
     * @param bizDataCollection 数据采集
     * @return 结果
     */
    @Override
    public int updateBizDataCollection(BizDataCollection bizDataCollection)
    {
        return bizDataCollectionMapper.updateBizDataCollection(bizDataCollection);
    }

    /**
     * 批量删除数据采集
     * 
     * @param ids 需要删除的数据采集主键
     * @return 结果
     */
    @Override
    public int deleteBizDataCollectionByIds(Long[] ids)
    {
        return bizDataCollectionMapper.deleteBizDataCollectionByIds(ids);
    }

    /**
     * 删除数据采集信息
     * 
     * @param id 数据采集主键
     * @return 结果
     */
    @Override
    public int deleteBizDataCollectionById(Long id)
    {
        return bizDataCollectionMapper.deleteBizDataCollectionById(id);
    }
}
