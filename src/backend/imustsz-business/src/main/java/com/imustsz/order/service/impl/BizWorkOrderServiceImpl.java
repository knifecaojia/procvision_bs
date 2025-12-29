package com.imustsz.order.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import com.imustsz.cilent.domain.dto.ProcessDTO;
import com.imustsz.cilent.domain.dto.ResultDTO;
import com.imustsz.cilent.domain.dto.WorkOrderProperties;
import com.imustsz.cilent.domain.vo.StepVO;
import com.imustsz.cilent.domain.vo.WorkOrderVO;
import com.imustsz.craft.domain.BizStep;
import com.imustsz.craft.domain.Process;
import com.imustsz.craft.mapper.BizStepMapper;
import com.imustsz.craft.mapper.CraftMapper;
import com.imustsz.craft.mapper.ProcessMapper;
import com.imustsz.order.domain.json.DispatchTaskInfo;
import com.imustsz.order.domain.json.ProcessTaskSync;
import com.imustsz.order.domain.json.WorkOrder;
import com.imustsz.order.domain.json.WorkOrderInfo;
import com.imustsz.process.domain.BizProcessRecord;
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
    public List<BizWorkOrder> selectBizWorkOrderList(BizWorkOrder bizWorkOrder)
    {
        return bizWorkOrderMapper.selectBizWorkOrderList(bizWorkOrder);
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
    public void importOrderFromMMo(ProcessTaskSync processTaskSync) {
        for (WorkOrder workOrder : processTaskSync.getWorkOrderList()) {
            WorkOrderInfo workOrderInfo = workOrder.getWorkOrderInfo();
            DispatchTaskInfo dispatchTaskInfo = workOrder.getDispatchTaskInfo();

            BizWorkOrder bizWorkOrder = new BizWorkOrder();
            bizWorkOrder.setWorkOrderCode(workOrderInfo.getWorkOrderNo());
            bizWorkOrder.setWorkOrderQuantity(Long.parseLong(workOrderInfo.getWorkOrderQuantity()));
            bizWorkOrder.setCraftCode(workOrderInfo.getProcessNo());
            bizWorkOrder.setCraftVersion(workOrderInfo.getProcessVersion());
            bizWorkOrder.setStatus(1);

            bizWorkOrder.setDispatchQuantity(Long.parseLong(dispatchTaskInfo.getDispatchQuantity()));
            bizWorkOrder.setProcessCode(dispatchTaskInfo.getOperationNo());
            bizWorkOrder.setProcessName(dispatchTaskInfo.getOperationName());

            LocalDateTime localDateTime1 = LocalDateTime.parse(dispatchTaskInfo.getPlannedStartTime(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            LocalDateTime localDateTime2 = LocalDateTime.parse(dispatchTaskInfo.getPlannedEndTime(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            Date date1 = Date.from(localDateTime1.atZone(ZoneId.systemDefault()).toInstant());
            Date date2 = Date.from(localDateTime2.atZone(ZoneId.systemDefault()).toInstant());
            bizWorkOrder.setStartTime(date1);
            bizWorkOrder.setEndTime(date2);

            bizWorkOrder.setWorkerCode(dispatchTaskInfo.getWorkerCode());
            bizWorkOrder.setWorkerName(dispatchTaskInfo.getWorkerName());
            bizWorkOrderMapper.insertBizWorkOrder(bizWorkOrder);
        }

    }

    public List<WorkOrderVO> getWorkOrderVOList(WorkOrderProperties workOrderProperties) {
        List<WorkOrderVO> workOrderVOList = bizWorkOrderMapper.getWorkOrderVOList(workOrderProperties.getStatus());
        for (WorkOrderVO workOrderVO : workOrderVOList) {
            Process process = processMapper.selectProcessByCode(workOrderVO.getProcess_code());
            List<StepVO> bizSteps = bizStepMapper.selectStepByProcessId(process.getId());
            workOrderVO.setStep_infos(bizSteps);
        }
        return workOrderVOList;
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
        bizWorkOrder.setWorkOrderCode(resultDTO.getWork_order_code());
        return bizWorkOrderMapper.updateBizWorkOrderByCode(bizWorkOrder);
    }

    @Override
    public StepVO getStepByWorkOrderCode(String workOrderCode, String stepCode) {
        BizWorkOrder bizWorkOrder = bizWorkOrderMapper.selectBizWorkOrderByCode(workOrderCode);
        Long craftId = craftMapper.selectCraftIdByCodeAndVersion(bizWorkOrder.getCraftCode(), bizWorkOrder.getCraftVersion());
        Long processId = processMapper.selectProcessIdByCodeAndCraftId(bizWorkOrder.getProcessCode(), craftId);
        BizStep bizStep = bizStepMapper.selectBizStepByStepCodeAndProcessId(stepCode, processId);

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
