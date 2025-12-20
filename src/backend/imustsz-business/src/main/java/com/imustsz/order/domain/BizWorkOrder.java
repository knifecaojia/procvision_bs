package com.imustsz.order.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.imustsz.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.imustsz.common.annotation.Excel;

/**
 * 工单对象 biz_work_order
 * 
 * @author imustsz
 * @date 2025-12-19
 */
public class BizWorkOrder extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 工单编码 */
    @Excel(name = "工单编码")
    private String workOrderCode;

    /** 工单数量 */
    @Excel(name = "工单数量")
    private Long workOrderQuantity;

    /** 工艺编码 */
    @Excel(name = "工艺编码")
    private String craftCode;

    /** 工艺版本 */
    @Excel(name = "工艺版本")
    private String craftVersion;

    /** 工单状态(1为待开始，2为进行中，3为已完成，4为BLOCKED) */
    @Excel(name = "工单状态(1为待开始，2为进行中，3为已完成，4为BLOCKED)")
    private Long status;

    /** 订单id */
    @Excel(name = "订单id")
    private Long orderId;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createdTime;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date updatedTime;

    /** 创建人 */
    @Excel(name = "创建人")
    private String createdBy;

    /** 更新人 */
    @Excel(name = "更新人")
    private String updatedBy;

    /** 备注 */
    @Excel(name = "备注")
    private String remarks;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setWorkOrderCode(String workOrderCode) 
    {
        this.workOrderCode = workOrderCode;
    }

    public String getWorkOrderCode() 
    {
        return workOrderCode;
    }

    public void setWorkOrderQuantity(Long workOrderQuantity) 
    {
        this.workOrderQuantity = workOrderQuantity;
    }

    public Long getWorkOrderQuantity() 
    {
        return workOrderQuantity;
    }

    public void setCraftCode(String craftCode) 
    {
        this.craftCode = craftCode;
    }

    public String getCraftCode() 
    {
        return craftCode;
    }

    public void setCraftVersion(String craftVersion) 
    {
        this.craftVersion = craftVersion;
    }

    public String getCraftVersion() 
    {
        return craftVersion;
    }

    public void setStatus(Long status) 
    {
        this.status = status;
    }

    public Long getStatus() 
    {
        return status;
    }

    public void setOrderId(Long orderId) 
    {
        this.orderId = orderId;
    }

    public Long getOrderId() 
    {
        return orderId;
    }

    public void setCreatedTime(Date createdTime) 
    {
        this.createdTime = createdTime;
    }

    public Date getCreatedTime() 
    {
        return createdTime;
    }

    public void setUpdatedTime(Date updatedTime) 
    {
        this.updatedTime = updatedTime;
    }

    public Date getUpdatedTime() 
    {
        return updatedTime;
    }

    public void setCreatedBy(String createdBy) 
    {
        this.createdBy = createdBy;
    }

    public String getCreatedBy() 
    {
        return createdBy;
    }

    public void setUpdatedBy(String updatedBy) 
    {
        this.updatedBy = updatedBy;
    }

    public String getUpdatedBy() 
    {
        return updatedBy;
    }

    public void setRemarks(String remarks) 
    {
        this.remarks = remarks;
    }

    public String getRemarks() 
    {
        return remarks;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("workOrderCode", getWorkOrderCode())
            .append("workOrderQuantity", getWorkOrderQuantity())
            .append("craftCode", getCraftCode())
            .append("craftVersion", getCraftVersion())
            .append("status", getStatus())
            .append("orderId", getOrderId())
            .append("createdTime", getCreatedTime())
            .append("updatedTime", getUpdatedTime())
            .append("createdBy", getCreatedBy())
            .append("updatedBy", getUpdatedBy())
            .append("remarks", getRemarks())
            .toString();
    }
}
