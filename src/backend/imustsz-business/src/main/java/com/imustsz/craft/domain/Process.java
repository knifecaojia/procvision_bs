package com.imustsz.craft.domain;

import com.imustsz.common.annotation.Excel;
import com.imustsz.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 工序信息对象 process
 * 
 * @author imustsz
 * @date 2025-12-18
 */
public class Process extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 编码 */
    @Excel(name = "编码")
    private String code;

    /** 名称 */
    @Excel(name = "名称")
    private String name;

    /** 检测算法编码 */
    @Excel(name = "检测算法编码")
    private Long algorithmId;

    /** 说明 */
    @Excel(name = "说明")
    private String desc;

    /** 异物检测 */
    @Excel(name = "异物检测")
    private String exceptionCheck;

    /** 工艺id */
    @Excel(name = "工艺id")
    private Long craftId;

    /** 所属工艺编码 */
    @Excel(name = "所属工艺编码")
    private String craftCode;

    /** 终检 */
    @Excel(name = "终检")
    private String finalCheck;

    /** 工序材料信息(JSON形式) */
    @Excel(name = "工序材料信息(JSON形式)")
    private String processMaterialInfo;

    public String getExceptionCheck() {
        return exceptionCheck;
    }

    public void setExceptionCheck(String exceptionCheck) {
        this.exceptionCheck = exceptionCheck;
    }

    public String getFinalCheck() {
        return finalCheck;
    }

    public void setFinalCheck(String finalCheck) {
        this.finalCheck = finalCheck;
    }

    public Long getAlgorithmId() {
        return algorithmId;
    }

    public void setAlgorithmId(Long algorithmId) {
        this.algorithmId = algorithmId;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getCode()
    {
        return code;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void setDesc(String desc)
    {
        this.desc = desc;
    }

    public String getDesc()
    {
        return desc;
    }

    public void setCraftId(Long craftId)
    {
        this.craftId = craftId;
    }

    public Long getCraftId()
    {
        return craftId;
    }

    public void setCraftCode(String craftCode)
    {
        this.craftCode = craftCode;
    }

    public String getCraftCode()
    {
        return craftCode;
    }

    public void setProcessMaterialInfo(String processMaterialInfo)
    {
        this.processMaterialInfo = processMaterialInfo;
    }

    public String getProcessMaterialInfo()
    {
        return processMaterialInfo;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("code", getCode())
            .append("name", getName())
            .append("desc", getDesc())
            .append("algorithmId", getAlgorithmId())
            .append("exceptionCheck", getExceptionCheck())
            .append("craftId", getCraftId())
            .append("craftCode", getCraftCode())
            .append("finalCheck", getFinalCheck())
            .append("processMaterialInfo", getProcessMaterialInfo())
            .toString();
    }
}
