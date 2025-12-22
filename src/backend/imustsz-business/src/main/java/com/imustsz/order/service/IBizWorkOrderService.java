package com.imustsz.order.service;

import java.util.List;
import com.imustsz.order.domain.BizWorkOrder;
import com.imustsz.order.domain.json.ProcessTaskSync;

/**
 * 工单Service接口
 * 
 * @author imustsz
 * @date 2025-12-22
 */
public interface IBizWorkOrderService 
{
    /**
     * 查询工单
     * 
     * @param id 工单主键
     * @return 工单
     */
    public BizWorkOrder selectBizWorkOrderById(Long id);

    /**
     * 查询工单列表
     * 
     * @param bizWorkOrder 工单
     * @return 工单集合
     */
    public List<BizWorkOrder> selectBizWorkOrderList(BizWorkOrder bizWorkOrder);

    /**
     * 新增工单
     * 
     * @param bizWorkOrder 工单
     * @return 结果
     */
    public int insertBizWorkOrder(BizWorkOrder bizWorkOrder);

    /**
     * 修改工单
     * 
     * @param bizWorkOrder 工单
     * @return 结果
     */
    public int updateBizWorkOrder(BizWorkOrder bizWorkOrder);

    /**
     * 批量删除工单
     * 
     * @param ids 需要删除的工单主键集合
     * @return 结果
     */
    public int deleteBizWorkOrderByIds(Long[] ids);

    /**
     * 删除工单信息
     * 
     * @param id 工单主键
     * @return 结果
     */
    public int deleteBizWorkOrderById(Long id);

    void importOrderFromMMo(ProcessTaskSync processTaskSync);
}
