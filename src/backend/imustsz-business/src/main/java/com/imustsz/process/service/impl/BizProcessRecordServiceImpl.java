package com.imustsz.process.service.impl;

import java.util.ArrayList;
import java.util.List;
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
    public List<ProcessRecordVO> selectBizProcessRecordList(BizProcessRecord record){
        List<BizProcessRecord> bizProcessRecords = bizProcessRecordMapper.selectBizProcessRecordList(record);
        List<String> taskNos = bizProcessRecords.stream().map(BizProcessRecord::getWorkOrderCode).distinct().collect(Collectors.toList());

        return taskNos.stream().map(taskNo -> {
            ProcessRecordVO processRecordVO = new ProcessRecordVO();
            processRecordVO.setTaskNo(taskNo);
            BizWorkOrder bizWorkOrder = bizWorkOrderMapper.selectBizWorkOrderByCode(taskNo);
            processRecordVO.setTaskStatus(bizWorkOrder.getStatus());
            processRecordVO.setProcessNo(bizWorkOrder.getProcessCode());
            processRecordVO.setProcessName(bizWorkOrder.getProcessName());
            List<StepRecordVO> stepRecordVOS = new ArrayList<>();
            bizProcessRecords.forEach(bizProcessRecord -> {
                if (bizProcessRecord.getWorkOrderCode().equals(taskNo)) {
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
                }
            });
            processRecordVO.setStepInfo(stepRecordVOS);
            return processRecordVO;
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

        BizProcessRecord record = bizProcessRecordMapper.selectRecordByTaskNoAndStepCode(processDTO.getTask_no(), processDTO.getStep_code());

        BizWorkOrder bizWorkOrder = bizWorkOrderMapper.selectBizWorkOrderByCode((processDTO.getTask_no()));

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
    public List<ProcessRecordVO> getProcessRecordList(Integer status) {
        BizProcessRecord record = new BizProcessRecord();

        List<BizProcessRecord> bizProcessRecords = bizProcessRecordMapper.selectBizProcessRecordList(record);

        List<String> taskNos = bizProcessRecords.stream().map(BizProcessRecord::getWorkOrderCode).filter(workOrderCode -> {
            BizWorkOrder bizWorkOrder = bizWorkOrderMapper.selectBizWorkOrderByCode(workOrderCode);
            if (status == null)
                return bizWorkOrder.getStatus() == 3 || bizWorkOrder.getStatus() == 2 || bizWorkOrder.getStatus() == 4;
            else if (status == 3) {
                return bizWorkOrder.getStatus() == 3;
            } else if (status == 2)
                return bizWorkOrder.getStatus() == 2;
            else
                throw new RuntimeException("status参数错误");
        }).distinct().collect(Collectors.toList());

        return taskNos.stream().map(taskNo -> {
            ProcessRecordVO processRecordVO = new ProcessRecordVO();
            processRecordVO.setTaskNo(taskNo);
            BizWorkOrder bizWorkOrder = bizWorkOrderMapper.selectBizWorkOrderByCode(taskNo);
            processRecordVO.setTaskStatus(bizWorkOrder.getStatus());
            processRecordVO.setProcessNo(bizWorkOrder.getProcessCode());
            processRecordVO.setProcessName(bizWorkOrder.getProcessName());
            List<StepRecordVO> stepRecordVOS = new ArrayList<>();
            bizProcessRecords.forEach(bizProcessRecord -> {
                if (bizProcessRecord.getWorkOrderCode().equals(taskNo)) {
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
                }
            });
            processRecordVO.setStepInfo(stepRecordVOS);
            return processRecordVO;
        }).collect(Collectors.toList());
    }

    @Override
    public ProcessRecordVO getRecordByTaskNo(String taskNo) {

        BizWorkOrder bizWorkOrder = bizWorkOrderMapper.selectBizWorkOrderByCode(taskNo);
        ProcessRecordVO processRecordVO = new ProcessRecordVO();
        processRecordVO.setTaskNo(taskNo);
        processRecordVO.setTaskStatus(bizWorkOrder.getStatus());
        processRecordVO.setProcessNo(bizWorkOrder.getProcessCode());
        processRecordVO.setProcessName(bizWorkOrder.getProcessName());
        List<BizProcessRecord> bizProcessRecords = bizProcessRecordMapper.selectRecordByTaskNo(taskNo);
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
        bizProcessRecord.setStepName(bizStep.getName());
        bizProcessRecord.setStepCode(bizStep.getCode());
        bizProcessRecord.setStepStatus(processDTO.getStep_status());
        bizProcessRecord.setImagePath(processDTO.getObject_name());
        bizProcessRecord.setData(processDTO.getAlgo_result());
        bizProcessRecord.setSubmitTime(DateUtils.getNowDate());
        return bizProcessRecord;
    }
}
