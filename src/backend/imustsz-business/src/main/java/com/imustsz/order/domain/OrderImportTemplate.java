package com.imustsz.order.domain;

import com.imustsz.common.annotation.Excel;
import lombok.Data;

import java.util.Date;

@Data
public class OrderImportTemplate {
    @Excel(name = "生产订单号", prompt = "必填项", readConverterExp = "")
    private String productionOrderNo;

    @Excel(name = "工单号", prompt = "必填项", readConverterExp = "")
    private String workOrderNo;

    @Excel(name = "工序编号", readConverterExp = "")
    private String operationNo;

    @Excel(name = "工序名称", readConverterExp = "")
    private String operationName;

    @Excel(name = "派工数量", readConverterExp = "")
    private Integer dispatchQuantity;

    @Excel(name = "计划开始时间", readConverterExp = "", prompt = "年/月/日 时:分:秒", dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date plannedStartTime;

    @Excel(name = "计划结束时间", readConverterExp = "", prompt = "年/月/日 时:分:秒", dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date plannedEndTime;

    @Excel(name = "班组", readConverterExp = "")
    private String prodGroup;

    @Excel(name = "人员工号", readConverterExp = "")
    private String workerCode;

    @Excel(name = "人员姓名", readConverterExp = "")
    private String workerName;
}
