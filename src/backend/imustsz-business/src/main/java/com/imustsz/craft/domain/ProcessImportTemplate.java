package com.imustsz.craft.domain;

import com.imustsz.common.annotation.Excel;
import lombok.Data;

/**
 * 订单工艺导入模板对象
 */
@Data
public class ProcessImportTemplate {

    @Excel(name = "生产订单号", prompt = "必填项", readConverterExp = "")
    private String productionOrderNo;

    @Excel(name = "工艺编号", prompt = "必填项", readConverterExp = "")
    private String processNo;

    @Excel(name = "工艺版本", prompt = "必填项", readConverterExp = "")
    private String processVersion;

    @Excel(name = "工艺名称", readConverterExp = "")
    private String processName;

    @Excel(name = "工艺描述", readConverterExp = "")
    private String processDesc;

    @Excel(name = "工序号", prompt = "必填项", readConverterExp = "")
    private String operationNo;

    @Excel(name = "工序名称", readConverterExp = "")
    private String operationName;

    @Excel(name = "工序描述", readConverterExp = "")
    private String operationDesc;

    @Excel(name = "工步号", prompt = "必填项", readConverterExp = "")
    private String stepNo;

    @Excel(name = "工步名称", readConverterExp = "")
    private String stepName;

    @Excel(name = "工步内容", readConverterExp = "")
    private String stepContent;

    @Excel(name = "物料编码", readConverterExp = "")
    private String materialNo;

    @Excel(name = "物料名称", readConverterExp = "")
    private String materialName;

    @Excel(name = "物料数量", readConverterExp = "")
    private Integer materialQuantity;

    @Excel(name = "单位", readConverterExp = "")
    private String materialUnit;

    @Excel(name = "防错标识", readConverterExp = "")
    private String errorPreventionMark;
}