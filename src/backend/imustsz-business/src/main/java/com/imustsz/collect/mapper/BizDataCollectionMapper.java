package com.imustsz.collect.mapper;

import java.util.List;
import com.imustsz.collect.domain.BizDataCollection;
import com.imustsz.framework.aspectj.AutoFill;
import org.apache.ibatis.annotations.Param;

/**
 * 数据采集Mapper接口
 * 
 * @author imustsz
 * @date 2026-01-20
 */
public interface BizDataCollectionMapper 
{
    /**
     * 查询数据采集
     * 
     * @param id 数据采集主键
     * @return 数据采集
     */
    public BizDataCollection selectBizDataCollectionById(Long id);

    /**
     * 查询数据采集列表
     * 
     * @param bizDataCollection 数据采集
     * @return 数据采集集合
     */
    public List<BizDataCollection> selectBizDataCollectionList(BizDataCollection bizDataCollection);

    /**
     * 新增数据采集
     * 
     * @param bizDataCollection 数据采集
     * @return 结果
     */
    @AutoFill("insert")
    public int insertBizDataCollection(BizDataCollection bizDataCollection);

    /**
     * 修改数据采集
     * 
     * @param bizDataCollection 数据采集
     * @return 结果
     */
    @AutoFill("update")
    public int updateBizDataCollection(BizDataCollection bizDataCollection);

    /**
     * 删除数据采集
     * 
     * @param id 数据采集主键
     * @return 结果
     */
    public int deleteBizDataCollectionById(Long id);

    /**
     * 批量删除数据采集
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBizDataCollectionByIds(Long[] ids);

    BizDataCollection checkData(String data);

    List<BizDataCollection> selectUploadData();

    List<BizDataCollection> selectDeleteData(@Param("limit") Integer limit);

    int deleteData();
}
