package com.imustsz.order.service;

import java.util.List;
import com.imustsz.order.domain.BizDispatchOrder;

/**
 * 派单Service接口
 * 
 * @author imustsz
 * @date 2025-12-19
 */
public interface IBizDispatchOrderService 
{
    /**
     * 查询派单
     * 
     * @param id 派单主键
     * @return 派单
     */
    public BizDispatchOrder selectBizDispatchOrderById(Long id);

    /**
     * 查询派单列表
     * 
     * @param bizDispatchOrder 派单
     * @return 派单集合
     */
    public List<BizDispatchOrder> selectBizDispatchOrderList(BizDispatchOrder bizDispatchOrder);

    /**
     * 新增派单
     * 
     * @param bizDispatchOrder 派单
     * @return 结果
     */
    public int insertBizDispatchOrder(BizDispatchOrder bizDispatchOrder);

    /**
     * 修改派单
     * 
     * @param bizDispatchOrder 派单
     * @return 结果
     */
    public int updateBizDispatchOrder(BizDispatchOrder bizDispatchOrder);

    /**
     * 批量删除派单
     * 
     * @param ids 需要删除的派单主键集合
     * @return 结果
     */
    public int deleteBizDispatchOrderByIds(Long[] ids);

    /**
     * 删除派单信息
     * 
     * @param id 派单主键
     * @return 结果
     */
    public int deleteBizDispatchOrderById(Long id);
}
