package com.imustsz.order.service.impl;

import java.util.List;

import com.imustsz.common.utils.DateUtils;
import com.imustsz.order.domain.json.DispatchTaskInfo;
import com.imustsz.order.domain.json.ProcessTaskSync;
import com.imustsz.order.domain.json.WorkOrder;
import com.imustsz.order.domain.json.WorkOrderInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.imustsz.order.mapper.BizWorkOrderMapper;
import com.imustsz.order.domain.BizWorkOrder;
import com.imustsz.order.service.IBizWorkOrderService;

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
            bizWorkOrder.setStartTime(DateUtils.parseDate(dispatchTaskInfo.getPlannedStartTime()));
            bizWorkOrder.setEndTime(DateUtils.parseDate(dispatchTaskInfo.getPlannedEndTime()));
            bizWorkOrder.setWorkerCode(dispatchTaskInfo.getWorkerCode());
            bizWorkOrder.setWorkerName(dispatchTaskInfo.getWorkerName());
            bizWorkOrderMapper.insertBizWorkOrder(bizWorkOrder);
        }

    }
}
