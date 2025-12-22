package com.imustsz.process.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.imustsz.process.mapper.BizProcessRecordMapper;
import com.imustsz.process.domain.BizProcessRecord;
import com.imustsz.process.service.IBizProcessRecordService;

/**
 * 过程记录Service业务层处理
 * 
 * @author imustsz
 * @date 2025-12-22
 */
@Service
public class BizProcessRecordServiceImpl implements IBizProcessRecordService 
{
    @Autowired
    private BizProcessRecordMapper bizProcessRecordMapper;

    /**
     * 查询过程记录
     * 
     * @param id 过程记录主键
     * @return 过程记录
     */
    @Override
    public BizProcessRecord selectBizProcessRecordById(Long id)
    {
        return bizProcessRecordMapper.selectBizProcessRecordById(id);
    }

    /**
     * 查询过程记录列表
     * 
     * @param bizProcessRecord 过程记录
     * @return 过程记录
     */
    @Override
    public List<BizProcessRecord> selectBizProcessRecordList(BizProcessRecord bizProcessRecord)
    {
        return bizProcessRecordMapper.selectBizProcessRecordList(bizProcessRecord);
    }

    /**
     * 新增过程记录
     * 
     * @param bizProcessRecord 过程记录
     * @return 结果
     */
    @Override
    public int insertBizProcessRecord(BizProcessRecord bizProcessRecord)
    {
        return bizProcessRecordMapper.insertBizProcessRecord(bizProcessRecord);
    }

    /**
     * 修改过程记录
     * 
     * @param bizProcessRecord 过程记录
     * @return 结果
     */
    @Override
    public int updateBizProcessRecord(BizProcessRecord bizProcessRecord)
    {
        return bizProcessRecordMapper.updateBizProcessRecord(bizProcessRecord);
    }

    /**
     * 批量删除过程记录
     * 
     * @param ids 需要删除的过程记录主键
     * @return 结果
     */
    @Override
    public int deleteBizProcessRecordByIds(Long[] ids)
    {
        return bizProcessRecordMapper.deleteBizProcessRecordByIds(ids);
    }

    /**
     * 删除过程记录信息
     * 
     * @param id 过程记录主键
     * @return 结果
     */
    @Override
    public int deleteBizProcessRecordById(Long id)
    {
        return bizProcessRecordMapper.deleteBizProcessRecordById(id);
    }
}
