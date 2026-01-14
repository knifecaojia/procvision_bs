package com.imustsz.order.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.imustsz.common.core.domain.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.imustsz.common.annotation.Excel;

/**
 * 工单对象 biz_work_order
 * 
 * @author imustsz
 * @date 2025-12-22
 */
@Data
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

    private String projectNo;

    private String prodOrderNo;

    private String prodBatchNo;

    private String materialNo;

    private String materialName;

}
