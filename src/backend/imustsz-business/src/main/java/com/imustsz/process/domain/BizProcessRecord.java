package com.imustsz.process.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.imustsz.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.imustsz.common.annotation.Excel;

/**
 * 过程记录对象 biz_process_record
 * 
 * @author imustsz
 * @date 2025-12-22
 */
public class BizProcessRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 工单编码 */
    @Excel(name = "工单编码")
    private String workOrderCode;

    /** 工步id */
    @Excel(name = "工步id")
    private Long stepId;

    /** 工步名称 */
    @Excel(name = "工步名称")
    private String stepName;

    /** 工步状态 */
    @Excel(name = "工步状态")
    private String stepStatus;

    /** Minio Key */
    @Excel(name = "Minio Key")
    private String imagePath;

    /** 拓展数据 */
    @Excel(name = "拓展数据")
    private String data;

    /** 提交时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "提交时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date submitTime;

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

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

    public void setStepId(Long stepId) 
    {
        this.stepId = stepId;
    }

    public Long getStepId() 
    {
        return stepId;
    }

    public void setStepStatus(String stepStatus) 
    {
        this.stepStatus = stepStatus;
    }

    public String getStepStatus() 
    {
        return stepStatus;
    }

    public void setImagePath(String imagePath) 
    {
        this.imagePath = imagePath;
    }

    public String getImagePath() 
    {
        return imagePath;
    }

    public void setData(String data) 
    {
        this.data = data;
    }

    public String getData() 
    {
        return data;
    }

    public void setSubmitTime(Date submitTime) 
    {
        this.submitTime = submitTime;
    }

    public Date getSubmitTime() 
    {
        return submitTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("workOrderCode", getWorkOrderCode())
            .append("stepId", getStepId())
            .append("stepName", getStepName())
            .append("stepStatus", getStepStatus())
            .append("imagePath", getImagePath())
            .append("data", getData())
            .append("submitTime", getSubmitTime())
            .toString();
    }
}
