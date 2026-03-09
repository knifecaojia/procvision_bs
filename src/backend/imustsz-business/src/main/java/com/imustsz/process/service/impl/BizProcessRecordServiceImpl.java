package com.imustsz.process.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.imustsz.cilent.domain.dto.ProcessDTO;
import com.imustsz.cilent.domain.vo.ProcessRecordVO;
import com.imustsz.cilent.domain.vo.StepRecordVO;
import com.imustsz.common.utils.DateUtils;
import com.imustsz.common.utils.bean.MinioUtils;
import com.imustsz.craft.domain.BizStep;
import com.imustsz.craft.domain.Craft;
import com.imustsz.craft.domain.Process;
import com.imustsz.craft.mapper.BizStepMapper;
import com.imustsz.craft.mapper.CraftMapper;
import com.imustsz.craft.mapper.ProcessMapper;
import com.imustsz.order.domain.BizWorkOrder;
import com.imustsz.order.mapper.BizWorkOrderMapper;
import com.imustsz.process.domain.UniqueRecordParams;
import org.jetbrains.annotations.NotNull;
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
public class BizProcessRecordServiceImpl implements IBizProcessRecordService {
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
    public BizProcessRecord selectBizProcessRecordById(Long id) {
        return bizProcessRecordMapper.selectBizProcessRecordById(id);
    }

    /**
     * 查询过程记录列表
     *
     * @param record 过程记录
     * @return 过程记录
     */
    @Override
    public List<BizProcessRecord> selectBizProcessRecordList(BizProcessRecord record){
        List<BizProcessRecord> bizProcessRecords = bizProcessRecordMapper.selectBizProcessRecordList(record);

        return bizProcessRecords.stream().peek(processRecord -> {
            try {
                processRecord.setImagePath(minioUtils.getPresignedUrl(processRecord.getImagePath()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }

    /**
     * 新增过程记录
     *
     * @param bizProcessRecord 过程记录
     * @return 结果
     */
    @Override
    public int insertBizProcessRecord(BizProcessRecord bizProcessRecord) {
        BizProcessRecord record = bizProcessRecordMapper.selectBizProcessRecordByTaskNoAndStepCode(bizProcessRecord.getWorkOrderCode(), bizProcessRecord.getStepCode());
        if (record != null) {
            record.setData(bizProcessRecord.getData());
            record.setImagePath(bizProcessRecord.getImagePath());
            record.setStepStatus(bizProcessRecord.getStepStatus());
            record.setSubmitTime(DateUtils.getNowDate());
            return bizProcessRecordMapper.updateBizProcessRecord(record);
        }
        return bizProcessRecordMapper.insertBizProcessRecord(bizProcessRecord);
    }

    /**
     * 修改过程记录
     *
     * @param bizProcessRecord 过程记录
     * @return 结果
     */
    @Override
    public int updateBizProcessRecord(BizProcessRecord bizProcessRecord) {
        return bizProcessRecordMapper.updateBizProcessRecord(bizProcessRecord);
    }

    /**
     * 批量删除过程记录
     *
     * @param ids 需要删除的过程记录主键
     * @return 结果
     */
    @Override
    public int deleteBizProcessRecordByIds(Long[] ids) {
        return bizProcessRecordMapper.deleteBizProcessRecordByIds(ids);
    }

    /**
     * 删除过程记录信息
     *
     * @param id 过程记录主键
     * @return 结果
     */
    @Override
    public int deleteBizProcessRecordById(Long id) {
        return bizProcessRecordMapper.deleteBizProcessRecordById(id);
    }

    @Override
    @Transactional
    public int insertBizProcessRecordByUpload(ProcessDTO processDTO) {

        BizProcessRecord record = bizProcessRecordMapper.selectRecordByTaskNoAndProcessCodeAndStepCode(processDTO.getTask_no(), processDTO.getProcess_code(), processDTO.getStep_code());

        BizWorkOrder bizWorkOrder = bizWorkOrderMapper.selectBizWorkOrderByCodeAndProcessCode(processDTO.getTask_no(), processDTO.getProcess_code());

        Craft craft = craftMapper.selectCraftByCodeAndVersion(bizWorkOrder.getCraftCode(), bizWorkOrder.getCraftVersion());

        Process process = processMapper.selectProcessIdByCodeAndCraftId(bizWorkOrder.getProcessCode(), craft.getId());

        BizStep bizStep = bizStepMapper.selectBizStepByStepCodeAndProcessId(processDTO.getStep_code(), process.getId());

        BizProcessRecord bizProcessRecord = getBizProcessRecord(processDTO, bizStep);

        if (record == null)
            return bizProcessRecordMapper.insertBizProcessRecord(bizProcessRecord);
        else {
            bizProcessRecord.setId(record.getId());
            return bizProcessRecordMapper.updateBizProcessRecord(bizProcessRecord);
        }
    }

    @Override
    public List<ProcessRecordVO> getProcessRecordList(Integer status, String taskNo) {

        List<UniqueRecordParams> paramList = bizProcessRecordMapper.selectBizProcessRecords(taskNo);

        return paramList.stream().map(param -> {
            ProcessRecordVO processRecordVO = new ProcessRecordVO();
            processRecordVO.setTaskNo(param.getWorkOrderCode());
            processRecordVO.setProcessNo(param.getProcessCode());
            BizWorkOrder bizWorkOrder = bizWorkOrderMapper.selectBizWorkOrderByCodeAndProcessCode(param.getWorkOrderCode(), param.getProcessCode());
            processRecordVO.setTaskStatus(bizWorkOrder.getStatus());
            List<BizProcessRecord> records = bizProcessRecordMapper.selectBizProcessRecordByOrderAndProcessCode(param.getWorkOrderCode(), param.getProcessCode());
            processRecordVO.setProcessName(bizWorkOrder.getProcessName());
            processRecordVO.setStepInfo(records.stream().map(record -> {
                StepRecordVO stepRecordVO = new StepRecordVO();
                stepRecordVO.setStepNo(record.getStepCode());
                stepRecordVO.setStepName(record.getStepName());
                stepRecordVO.setStepStatus(record.getStepStatus());
                try {
                    stepRecordVO.setImgUrl(minioUtils.getPresignedUrl(record.getImagePath()));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                stepRecordVO.setAlgResult(record.getData());
                return stepRecordVO;
            }).collect(Collectors.toList()));
            return processRecordVO;
        }).filter(processRecordVO -> {
            if (status == null)
                return processRecordVO.getTaskStatus() == 2 || processRecordVO.getTaskStatus() == 3;
            else if (status == 1)
                return processRecordVO.getTaskStatus() == 2;
            else if (status == 2)
                return processRecordVO.getTaskStatus() == 3;
            else
                throw new RuntimeException("参数错误");
        }).collect(Collectors.toList());
    }

    @Override
    public ProcessRecordVO getRecordByTaskNoAndProcessCode(String taskNo, String processCode) {

        BizWorkOrder bizWorkOrder = bizWorkOrderMapper.selectBizWorkOrderByCodeAndProcessCode(taskNo, processCode);
        ProcessRecordVO processRecordVO = new ProcessRecordVO();
        processRecordVO.setTaskNo(taskNo);
        processRecordVO.setTaskStatus(bizWorkOrder.getStatus());
        processRecordVO.setProcessNo(bizWorkOrder.getProcessCode());
        processRecordVO.setProcessName(bizWorkOrder.getProcessName());
        List<BizProcessRecord> bizProcessRecords = bizProcessRecordMapper.selectBizProcessRecordByOrderAndProcessCode(taskNo, processCode);
        List<StepRecordVO> stepRecordVOS = new ArrayList<>();
        bizProcessRecords.forEach(bizProcessRecord -> {
            StepRecordVO stepRecordVO = new StepRecordVO();
            stepRecordVO.setStepNo(bizProcessRecord.getStepCode());
            stepRecordVO.setStepName(bizProcessRecord.getStepName());
            stepRecordVO.setStepStatus(bizProcessRecord.getStepStatus());
            stepRecordVO.setAlgResult(bizProcessRecord.getData());
            try {
                stepRecordVO.setImgUrl(minioUtils.getPresignedUrl(bizProcessRecord.getImagePath()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            stepRecordVOS.add(stepRecordVO);
        });
        processRecordVO.setStepInfo(stepRecordVOS);

        return processRecordVO;
    }

    @NotNull
    private static BizProcessRecord getBizProcessRecord(ProcessDTO processDTO, BizStep bizStep) {
        BizProcessRecord bizProcessRecord = new BizProcessRecord();
        bizProcessRecord.setWorkOrderCode(processDTO.getTask_no());
        bizProcessRecord.setProcessCode(processDTO.getProcess_code());
        bizProcessRecord.setStepName(bizStep.getName());
        bizProcessRecord.setStepCode(bizStep.getCode());
        bizProcessRecord.setStepStatus(processDTO.getStep_status());
        bizProcessRecord.setImagePath(processDTO.getObject_name());
        bizProcessRecord.setData(processDTO.getAlgo_result());
        bizProcessRecord.setSubmitTime(DateUtils.getNowDate());
        return bizProcessRecord;
    }
}
