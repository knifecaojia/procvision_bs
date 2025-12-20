package com.imustsz.order.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.imustsz.order.mapper.BizDispatchOrderMapper;
import com.imustsz.order.domain.BizDispatchOrder;
import com.imustsz.order.service.IBizDispatchOrderService;

/**
 * 派单Service业务层处理
 * 
 * @author imustsz
 * @date 2025-12-19
 */
@Service
public class BizDispatchOrderServiceImpl implements IBizDispatchOrderService 
{
    @Autowired
    private BizDispatchOrderMapper bizDispatchOrderMapper;

    /**
     * 查询派单
     * 
     * @param id 派单主键
     * @return 派单
     */
    @Override
    public BizDispatchOrder selectBizDispatchOrderById(Long id)
    {
        return bizDispatchOrderMapper.selectBizDispatchOrderById(id);
    }

    /**
     * 查询派单列表
     * 
     * @param bizDispatchOrder 派单
     * @return 派单
     */
    @Override
    public List<BizDispatchOrder> selectBizDispatchOrderList(BizDispatchOrder bizDispatchOrder)
    {
        return bizDispatchOrderMapper.selectBizDispatchOrderList(bizDispatchOrder);
    }

    /**
     * 新增派单
     * 
     * @param bizDispatchOrder 派单
     * @return 结果
     */
    @Override
    public int insertBizDispatchOrder(BizDispatchOrder bizDispatchOrder)
    {
        return bizDispatchOrderMapper.insertBizDispatchOrder(bizDispatchOrder);
    }

    /**
     * 修改派单
     * 
     * @param bizDispatchOrder 派单
     * @return 结果
     */
    @Override
    public int updateBizDispatchOrder(BizDispatchOrder bizDispatchOrder)
    {
        return bizDispatchOrderMapper.updateBizDispatchOrder(bizDispatchOrder);
    }

    /**
     * 批量删除派单
     * 
     * @param ids 需要删除的派单主键
     * @return 结果
     */
    @Override
    public int deleteBizDispatchOrderByIds(Long[] ids)
    {
        return bizDispatchOrderMapper.deleteBizDispatchOrderByIds(ids);
    }

    /**
     * 删除派单信息
     * 
     * @param id 派单主键
     * @return 结果
     */
    @Override
    public int deleteBizDispatchOrderById(Long id)
    {
        return bizDispatchOrderMapper.deleteBizDispatchOrderById(id);
    }
}
