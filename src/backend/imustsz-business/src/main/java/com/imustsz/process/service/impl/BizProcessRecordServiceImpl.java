package com.imustsz.process.service.impl;

import java.util.List;

import com.imustsz.cilent.domain.dto.ProcessDTO;
import com.imustsz.common.utils.DateUtils;
import com.imustsz.common.utils.bean.MinioUtils;
import com.imustsz.craft.domain.BizStep;
import com.imustsz.craft.mapper.BizStepMapper;
import com.imustsz.craft.mapper.CraftMapper;
import com.imustsz.craft.mapper.ProcessMapper;
import com.imustsz.order.domain.BizWorkOrder;
import com.imustsz.order.mapper.BizWorkOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.imustsz.process.mapper.BizProcessRecordMapper;
import com.imustsz.process.domain.BizProcessRecord;
import com.imustsz.process.service.IBizProcessRecordService;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private BizStepMapper bizStepMapper;

    @Autowired
    private BizWorkOrderMapper bizWorkOrderMapper;

    @Autowired
    private CraftMapper craftMapper;

    @Autowired
    private ProcessMapper processMapper;

    @Autowired
    private MinioUtils minioUtils;

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
    public List<BizProcessRecord> selectBizProcessRecordList(BizProcessRecord bizProcessRecord) throws Exception {
        List<BizProcessRecord> bizProcessRecords = bizProcessRecordMapper.selectBizProcessRecordList(bizProcessRecord);
        for (BizProcessRecord record : bizProcessRecords){
            record.setImagePath(minioUtils.getPresignedUrl(record.getImagePath()));
        }
        return bizProcessRecords;
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

    @Override
    @Transactional
    public int insertBizProcessRecordByUpload(ProcessDTO processDTO) {
        BizWorkOrder bizWorkOrder = bizWorkOrderMapper.selectBizWorkOrderByCode((processDTO.getWork_order_code()));
        Long craftId = craftMapper.selectCraftIdByCodeAndVersion(bizWorkOrder.getCraftCode(), bizWorkOrder.getCraftVersion());
        Long processId = processMapper.selectProcessIdByCodeAndCraftId(bizWorkOrder.getProcessCode(), craftId);
        BizStep bizStep = bizStepMapper.selectBizStepByStepCodeAndProcessId(processDTO.getStep_code(), processId);

        BizProcessRecord bizProcessRecord = new BizProcessRecord();
        bizProcessRecord.setWorkOrderCode(processDTO.getWork_order_code());
        bizProcessRecord.setStepName(bizStep.getName());
        bizProcessRecord.setStepStatus(processDTO.getStep_status());
        bizProcessRecord.setImagePath(processDTO.getObject_name());
        bizProcessRecord.setData(processDTO.getData());
        bizProcessRecord.setSubmitTime(DateUtils.getNowDate());

        return bizProcessRecordMapper.insertBizProcessRecord(bizProcessRecord);
    }
}
