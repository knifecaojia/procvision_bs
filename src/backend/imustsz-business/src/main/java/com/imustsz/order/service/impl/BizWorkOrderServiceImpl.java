package com.imustsz.order.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.imustsz.cilent.domain.dto.ProcessDTO;
import com.imustsz.cilent.domain.dto.ResultDTO;
import com.imustsz.cilent.domain.dto.WorkOrderProperties;
import com.imustsz.cilent.domain.vo.StepVO;
import com.imustsz.cilent.domain.vo.WorkOrderVO;
import com.imustsz.common.utils.DateUtils;
import com.imustsz.common.utils.bean.MinioUtils;
import com.imustsz.craft.domain.BizStep;
import com.imustsz.craft.domain.Craft;
import com.imustsz.craft.domain.Process;
import com.imustsz.craft.mapper.BizStepMapper;
import com.imustsz.craft.mapper.CraftMapper;
import com.imustsz.craft.mapper.ProcessMapper;
import com.imustsz.order.domain.json.*;
import com.imustsz.order.domain.vo.PageVO;
import com.imustsz.process.domain.BizProcessRecord;
import com.imustsz.process.mapper.BizProcessRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.imustsz.order.mapper.BizWorkOrderMapper;
import com.imustsz.order.domain.BizWorkOrder;
import com.imustsz.order.service.IBizWorkOrderService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 工单Service业务层处理
 * 
 * @author imustsz
 * @date 2025-12-22
 */
@Service
public class BizWorkOrderServiceImpl implements IBizWorkOrderService
{
    @Autowired
    private BizWorkOrderMapper bizWorkOrderMapper;

    @Autowired
    private BizStepMapper bizStepMapper;

    @Autowired
    private ProcessMapper processMapper;

    @Autowired
    private CraftMapper craftMapper;

    @Autowired
    private MinioUtils minioUtils;

    @Autowired
    private BizProcessRecordMapper bizProcessRecordMapper;

    /**
     * 查询工单
     * 
     * @param id 工单主键
     * @return 工单
     */
    @Override
    public BizWorkOrder selectBizWorkOrderById(Long id)
    {
        return bizWorkOrderMapper.selectBizWorkOrderById(id);
    }

    /**
     * 查询工单列表
     * 
     * @param bizWorkOrder 工单
     * @return 工单
     */
    @Override
    public List<BizWorkOrder> selectBizWorkOrderList(BizWorkOrder bizWorkOrder) throws Exception {
        List<BizWorkOrder> bizWorkOrders = bizWorkOrderMapper.selectBizWorkOrderList(bizWorkOrder);
        for (BizWorkOrder order : bizWorkOrders){
            if (order.getGuideMapUrl() != null)
                order.setGuideMapUrl(minioUtils.getPresignedUrl(order.getGuideMapUrl()));
        }
        return bizWorkOrders;
    }

    /**
     * 新增工单
     * 
     * @param bizWorkOrder 工单
     * @return 结果
     */
    @Override
    public int insertBizWorkOrder(BizWorkOrder bizWorkOrder)
    {
        Craft craft = craftMapper.selectCraftByCodeAndVersion(bizWorkOrder.getCraftCode(), bizWorkOrder.getCraftVersion());
        if (craft == null)
            return -1;
        Process process = processMapper.selectProcessByCodeAndNameAndCraftId(bizWorkOrder.getProcessCode(), bizWorkOrder.getProcessName(), craft.getId());
        if (process == null)
            return -2;
        if(craft.getStatus() == 1 || craft.getStatus() == 2)
            bizWorkOrder.setStatus(-2);
        else if (craft.getStatus() == 3)
            bizWorkOrder.setStatus(-1);
        else
            bizWorkOrder.setStatus(1);

        return bizWorkOrderMapper.insertBizWorkOrder(bizWorkOrder);
    }

    /**
     * 修改工单
     * 
     * @param bizWorkOrder 工单
     * @return 结果
     */
    @Override
    public int updateBizWorkOrder(BizWorkOrder bizWorkOrder)
    {
        return bizWorkOrderMapper.updateBizWorkOrder(bizWorkOrder);
    }

    /**
     * 批量删除工单
     * 
     * @param ids 需要删除的工单主键
     * @return 结果
     */
    @Override
    public int deleteBizWorkOrderByIds(Long[] ids)
    {
        for (Long id : ids){
            BizWorkOrder bizWorkOrder = bizWorkOrderMapper.selectBizWorkOrderById(id);
            bizProcessRecordMapper.deleteBizProcessRecordByTaskNo(bizWorkOrder.getWorkOrderCode());
        }
        return bizWorkOrderMapper.deleteBizWorkOrderByIds(ids);
    }

    /**
     * 删除工单信息
     * 
     * @param id 工单主键
     * @return 结果
     */
    @Override
    public int deleteBizWorkOrderById(Long id)
    {
        return bizWorkOrderMapper.deleteBizWorkOrderById(id);
    }

    /**
     * 从MMO获取订单信息
     */
    @Override
    @Transactional
    public int importOrderFromMMo(List<Task> taskSync) {
        int flag = 0;
        int i = 1;
        Date date = new Date();
        for (Task task : taskSync) {
            StringBuilder sb = new StringBuilder();

            BizWorkOrder bizWorkOrder = new BizWorkOrder();
            if (i < 10)
                bizWorkOrder.setWorkOrderCode(sb.append("10000").append(String.valueOf(date.getTime()).substring(String.valueOf(date.getTime()).length()-5)).append("-0").append(i++).toString());
            else
                bizWorkOrder.setWorkOrderCode(sb.append("10000").append(String.valueOf(date.getTime()).substring(String.valueOf(date.getTime()).length()-5)).append("-").append(i++).toString());
            bizWorkOrder.setCraftCode(task.getCraft_no());
            bizWorkOrder.setCraftVersion(task.getCraft_version());

            Craft craft = craftMapper.selectCraftByCodeAndVersion(bizWorkOrder.getCraftCode(), bizWorkOrder.getCraftVersion());
            if (craft == null)
                throw new RuntimeException("工艺不存在，请核对工艺");

            if(craft.getStatus() == 1 || craft.getStatus() == 2)
                bizWorkOrder.setStatus(-2);
            else if (craft.getStatus() == 3)
                bizWorkOrder.setStatus(-1);
            else
                bizWorkOrder.setStatus(1);

            bizWorkOrder.setProcessCode(task.getProceress_no());
            bizWorkOrder.setProcessName(task.getProceress_name());

            LocalDateTime localDateTime1 = LocalDateTime.parse(task.getPlanned_start_time(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            LocalDateTime localDateTime2 = LocalDateTime.parse(task.getPlanned_end_time(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            Date date1 = Date.from(localDateTime1.atZone(ZoneId.systemDefault()).toInstant());
            Date date2 = Date.from(localDateTime2.atZone(ZoneId.systemDefault()).toInstant());
            bizWorkOrder.setStartTime(date1);
            bizWorkOrder.setEndTime(date2);

            bizWorkOrder.setWorkerCode(task.getWorker_code());
            bizWorkOrder.setWorkerName(task.getWorker_name());
            bizWorkOrder.setProjectNo(task.getProject_no());
            bizWorkOrder.setProdOrderNo(task.getProd_order_no());
            bizWorkOrder.setProdBatchNo(task.getProd_batch_no());
            bizWorkOrder.setMaterialNo(task.getMaterial_no());
            bizWorkOrder.setMaterialName(task.getMaterial_name());

            flag += bizWorkOrderMapper.insertBizWorkOrder(bizWorkOrder);
        }

        return flag;

    }
    public PageVO workOrderVOList(WorkOrderProperties workOrderProperties) {
        BizWorkOrder bizWorkOrder = new BizWorkOrder();
        bizWorkOrder.setStatus(workOrderProperties.getStatus());
        PageHelper.startPage(workOrderProperties.getPageNum(), workOrderProperties.getPageSize());
        List<BizWorkOrder> bizWorkOrders = bizWorkOrderMapper.selectBizWorkOrderList(bizWorkOrder);
        PageInfo<BizWorkOrder> pageInfo = new PageInfo<>(bizWorkOrders);

        List<WorkOrderVO> collect = pageInfo.getList().stream().map(workOrder -> {
            WorkOrderVO workOrderVO = new WorkOrderVO();

            workOrderVO.setTask_no(workOrder.getWorkOrderCode());
            workOrderVO.setCraft_no(workOrder.getCraftCode());
            workOrderVO.setCraft_version(workOrder.getCraftVersion());
            Craft craft = craftMapper.selectCraftByCodeAndVersion(workOrder.getCraftCode(), workOrder.getCraftVersion());
            workOrderVO.setCraft_name(craft.getName());
            workOrderVO.setProcess_code(workOrder.getProcessCode());
            workOrderVO.setProcess_name(workOrder.getProcessName());
            workOrderVO.setStart_time(workOrder.getStartTime());
            workOrderVO.setEnd_time(workOrder.getEndTime());
            workOrderVO.setWorker_code(workOrder.getWorkerCode());
            workOrderVO.setWorker_name(workOrder.getWorkerName());
            workOrderVO.setProd_order_no(workOrder.getProdOrderNo());
            workOrderVO.setProd_batch_no(workOrder.getProdBatchNo());
            workOrderVO.setProject_no(workOrder.getProjectNo());

            Process process = processMapper.selectProcessIdByCodeAndCraftId(workOrder.getProcessCode(), craft.getId());
            List<StepVO> stepVOS = bizStepMapper.selectStepByProcessId(process.getId());
            stepVOS.forEach(stepVO -> {
                try {
                    if (stepVO.getGuide_url() != null)
                        stepVO.setGuide_url(minioUtils.getPresignedUrl(stepVO.getGuide_url()));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            workOrderVO.setStep_infos(stepVOS);
            workOrderVO.setAlgorithm_id(process.getAlgorithmId());

            workOrderVO.setStatus(workOrder.getStatus());

            return workOrderVO;

        }).collect(Collectors.toList());
        PageVO pageVO = new PageVO();
        pageVO.setList(collect);
        pageVO.setTotal((int) pageInfo.getTotal());
        return pageVO;
    }

    @Override
    @Transactional
    public int changeWorkOrderStatusByCode(String workOrderCode, String statusCode) {
        return bizWorkOrderMapper.changeWorkOrderStatusByCode(workOrderCode, statusCode);
    }

    @Override
    @Transactional
    public int updateBizWorkOrderResultByUpload(ResultDTO resultDTO) {
        BizWorkOrder bizWorkOrder = new BizWorkOrder();
        bizWorkOrder.setStatus(resultDTO.getResult_status());
        bizWorkOrder.setGuideMapUrl(resultDTO.getObject_name());
        bizWorkOrder.setWorkOrderCode(resultDTO.getTask_no());
        return bizWorkOrderMapper.updateBizWorkOrderByCode(bizWorkOrder);
    }

    @Override
    public StepVO getStepByWorkOrderCode(String workOrderCode, String stepCode) {
        BizWorkOrder bizWorkOrder = bizWorkOrderMapper.selectBizWorkOrderByCode(workOrderCode);
        Craft craft = craftMapper.selectCraftByCodeAndVersion(bizWorkOrder.getCraftCode(), bizWorkOrder.getCraftVersion());
        Process process = processMapper.selectProcessIdByCodeAndCraftId(bizWorkOrder.getProcessCode(), craft.getId());
        BizStep bizStep = bizStepMapper.selectBizStepByStepCodeAndProcessId(stepCode, process.getId());

        StepVO step = new StepVO();
        step.setStep_code(bizStep.getCode());
        step.setStep_name(bizStep.getName());
        step.setStep_content(bizStep.getContent());

        return step;
    }

    @Override
    public BizWorkOrder selectBizWorkOrderByCode(String workOrderCode) {
        return bizWorkOrderMapper.selectBizWorkOrderByCode(workOrderCode);
    }
}
