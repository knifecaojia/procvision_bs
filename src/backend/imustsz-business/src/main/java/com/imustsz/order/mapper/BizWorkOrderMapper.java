package com.imustsz.order.mapper;

import java.util.List;

import com.imustsz.cilent.domain.vo.WorkOrderVO;
import com.imustsz.order.domain.BizWorkOrder;
import org.apache.ibatis.annotations.Param;

/**
 * 工单Mapper接口
 * 
 * @author imustsz
 * @date 2025-12-22
 */
public interface BizWorkOrderMapper 
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
     * 删除工单
     * 
     * @param id 工单主键
     * @return 结果
     */
    public int deleteBizWorkOrderById(Long id);

    /**
     * 批量删除工单
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBizWorkOrderByIds(Long[] ids);

    List<WorkOrderVO> getWorkOrderVOList(Integer status);

    int changeWorkOrderStatusByCode(@Param("workOrderCode") String workOrderCode, @Param("statusCode") String statusCode);

    BizWorkOrder selectBizWorkOrderByCode(@Param("workOrderCode") String workOrderCode);

    int updateBizWorkOrderByCode(BizWorkOrder bizWorkOrder);
}
