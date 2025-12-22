package com.imustsz.process.mapper;

import java.util.List;
import com.imustsz.process.domain.BizProcessRecord;

/**
 * 过程记录Mapper接口
 * 
 * @author imustsz
 * @date 2025-12-22
 */
public interface BizProcessRecordMapper 
{
    /**
     * 查询过程记录
     * 
     * @param id 过程记录主键
     * @return 过程记录
     */
    public BizProcessRecord selectBizProcessRecordById(Long id);

    /**
     * 查询过程记录列表
     * 
     * @param bizProcessRecord 过程记录
     * @return 过程记录集合
     */
    public List<BizProcessRecord> selectBizProcessRecordList(BizProcessRecord bizProcessRecord);

    /**
     * 新增过程记录
     * 
     * @param bizProcessRecord 过程记录
     * @return 结果
     */
    public int insertBizProcessRecord(BizProcessRecord bizProcessRecord);

    /**
     * 修改过程记录
     * 
     * @param bizProcessRecord 过程记录
     * @return 结果
     */
    public int updateBizProcessRecord(BizProcessRecord bizProcessRecord);

    /**
     * 删除过程记录
     * 
     * @param id 过程记录主键
     * @return 结果
     */
    public int deleteBizProcessRecordById(Long id);

    /**
     * 批量删除过程记录
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBizProcessRecordByIds(Long[] ids);
}
