package com.imustsz.order.service.impl;

import java.util.List;

import com.imustsz.common.utils.DateUtils;
import com.imustsz.common.utils.SecurityUtils;
import com.imustsz.craft.domain.BizStep;
import com.imustsz.craft.domain.json.Step;
import com.imustsz.craft.mapper.BizStepMapper;
import com.imustsz.order.domain.BizDispatchOrder;
import com.imustsz.order.domain.BizWorkOrder;
import com.imustsz.order.domain.json.*;
import com.imustsz.order.mapper.BizDispatchOrderMapper;
import com.imustsz.order.mapper.BizWorkOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.imustsz.order.mapper.BizOrderMapper;
import com.imustsz.order.domain.BizOrder;
import com.imustsz.order.service.IBizOrderService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 订单Service业务层处理
 * 
 * @author imustsz
 * @date 2025-12-19
 */
@Service
public class BizOrderServiceImpl implements IBizOrderService 
{
    @Autowired
    private BizOrderMapper bizOrderMapper;

    @Autowired
    private BizWorkOrderMapper bizWorkOrderMapper;

    @Autowired
    private BizDispatchOrderMapper bizDispatchOrderMapper;

    @Autowired
    private BizStepMapper bizStepMapper;

    /**
     * 查询订单
     * 
     * @param id 订单主键
     * @return 订单
     */
    @Override
    public BizOrder selectBizOrderById(Long id)
    {
        return bizOrderMapper.selectBizOrderById(id);
    }

    /**
     * 查询订单列表
     * 
     * @param bizOrder 订单
     * @return 订单
     */
    @Override
    public List<BizOrder> selectBizOrderList(BizOrder bizOrder)
    {
        return bizOrderMapper.selectBizOrderList(bizOrder);
    }

    /**
     * 新增订单
     * 
     * @param bizOrder 订单
     * @return 结果
     */
    @Override
    public int insertBizOrder(BizOrder bizOrder)
    {
        return bizOrderMapper.insertBizOrder(bizOrder);
    }

    /**
     * 修改订单
     * 
     * @param bizOrder 订单
     * @return 结果
     */
    @Override
    public int updateBizOrder(BizOrder bizOrder)
    {
        return bizOrderMapper.updateBizOrder(bizOrder);
    }

    /**
     * 批量删除订单
     * 
     * @param ids 需要删除的订单主键
     * @return 结果
     */
    @Override
    public int deleteBizOrderByIds(Long[] ids)
    {
        return bizOrderMapper.deleteBizOrderByIds(ids);
    }

    /**
     * 删除订单信息
     * 
     * @param id 订单主键
     * @return 结果
     */
    @Override
    public int deleteBizOrderById(Long id)
    {
        return bizOrderMapper.deleteBizOrderById(id);
    }

    /**
     * 从MMO获取订单信息
     */
    @Override
    @Transactional
    public void importOrderFromMMo(ProcessTaskSync processTaskSync) {
        OrderInfo orderInfo = processTaskSync.getOrderInfo();
        List<WorkOrder> workOrderInfoList = processTaskSync.getWorkOrderList();

        BizOrder order = new BizOrder();
        order.setOrderCode(orderInfo.getProductionOrderNo());
        order.setProductBatch(orderInfo.getProductBatch());
        order.setMaterialCode(orderInfo.getMaterialNo());
        order.setCreatedTime(DateUtils.getNowDate());
        order.setCreatedBy(SecurityUtils.getUsername());
        bizOrderMapper.insertBizOrder(order);

        for (WorkOrder workOrder : workOrderInfoList) {
            WorkOrderInfo workOrderInfo = workOrder.getWorkOrderInfo();
            DispatchTaskInfo dispatchTaskInfo = workOrder.getDispatchTaskInfo();

            BizWorkOrder wo = new BizWorkOrder();
            wo.setCraftCode(workOrderInfo.getProcessNo());
            wo.setCraftVersion(workOrderInfo.getProcessVersion());
            wo.setWorkOrderQuantity(Long.valueOf(workOrderInfo.getWorkOrderQuantity()));
            wo.setWorkOrderCode(workOrderInfo.getWorkOrderNo());
            wo.setOrderId(order.getId());
            wo.setCreatedTime(DateUtils.getNowDate());
            wo.setCreatedBy(SecurityUtils.getUsername());
            bizWorkOrderMapper.insertBizWorkOrder(wo);

            BizDispatchOrder bdo = new BizDispatchOrder();
            bdo.setProcessCode(dispatchTaskInfo.getOperationNo());
            bdo.setProcessName(dispatchTaskInfo.getOperationName());
            bdo.setStartTime(DateUtils.parseDate(dispatchTaskInfo.getPlannedStartTime()));
            bdo.setEndTime(DateUtils.parseDate(dispatchTaskInfo.getPlannedEndTime()));
            bdo.setDispatchQuantity(Long.valueOf(dispatchTaskInfo.getDispatchQuantity()));
            bdo.setWorkerCode(dispatchTaskInfo.getWorkerCode());
            bdo.setWorkerName(dispatchTaskInfo.getWorkerName());
            bdo.setOrderId(order.getId());
            bdo.setCreatedTime(DateUtils.getNowDate());
            bdo.setCreatedBy(SecurityUtils.getUsername());
            bizDispatchOrderMapper.insertBizDispatchOrder(bdo);
            List<Step> stepList = dispatchTaskInfo.getStepList();
            for (Step step : stepList) {
                BizStep stepInfo = new BizStep();
                stepInfo.setCode(step.getStepNo());
                stepInfo.setName(step.getStepName());
                stepInfo.setContent(step.getStepContent());
                stepInfo.setType(2);
                stepInfo.setTypeId(bdo.getId());
                stepInfo.setCreatedTime(DateUtils.getNowDate());
                stepInfo.setCreatedBy(SecurityUtils.getUsername());
                bizStepMapper.insertBizStep(stepInfo);
            }
        }

    }
}
