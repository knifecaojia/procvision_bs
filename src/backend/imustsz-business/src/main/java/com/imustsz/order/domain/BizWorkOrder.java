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
 * @date 2025-12-22
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
    private Integer status;

    /** 工序编码 */
    @Excel(name = "工序编码")
    private String processCode;

    /** 工序名称 */
    @Excel(name = "工序名称")
    private String processName;

    /** 派单数量 */
    @Excel(name = "派单数量")
    private Long dispatchQuantity;

    /** 计划开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "计划开始时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date startTime;

    /** 计划结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "计划结束时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date endTime;

    /** 引导图url */
    @Excel(name = "引导图url")
    private String guideMapUrl;

    /** 装配工人编码 */
    @Excel(name = "装配工人编码")
    private String workerCode;

    /** 装配工人姓名 */
    @Excel(name = "装配工人姓名")
    private String workerName;

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

    public void setStatus(Integer status)
    {
        this.status = status;
    }

    public Integer getStatus()
    {
        return status;
    }

    public void setProcessCode(String processCode) 
    {
        this.processCode = processCode;
    }

    public String getProcessCode() 
    {
        return processCode;
    }

    public void setProcessName(String processName) 
    {
        this.processName = processName;
    }

    public String getProcessName() 
    {
        return processName;
    }

    public void setDispatchQuantity(Long dispatchQuantity) 
    {
        this.dispatchQuantity = dispatchQuantity;
    }

    public Long getDispatchQuantity() 
    {
        return dispatchQuantity;
    }

    public void setStartTime(Date startTime) 
    {
        this.startTime = startTime;
    }

    public Date getStartTime() 
    {
        return startTime;
    }

    public void setEndTime(Date endTime) 
    {
        this.endTime = endTime;
    }

    public Date getEndTime() 
    {
        return endTime;
    }

    public void setGuideMapUrl(String guideMapUrl) 
    {
        this.guideMapUrl = guideMapUrl;
    }

    public String getGuideMapUrl() 
    {
        return guideMapUrl;
    }

    public void setWorkerCode(String workerCode) 
    {
        this.workerCode = workerCode;
    }

    public String getWorkerCode() 
    {
        return workerCode;
    }

    public void setWorkerName(String workerName) 
    {
        this.workerName = workerName;
    }

    public String getWorkerName() 
    {
        return workerName;
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
            .append("processCode", getProcessCode())
            .append("processName", getProcessName())
            .append("dispatchQuantity", getDispatchQuantity())
            .append("startTime", getStartTime())
            .append("endTime", getEndTime())
            .append("guideMapUrl", getGuideMapUrl())
            .append("workerCode", getWorkerCode())
            .append("workerName", getWorkerName())
            .toString();
    }
}
