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

    /** 说明 */
    @Excel(name = "说明")
    private String desc;

    /** 指导粒度(1为工序，2为工步) */
    @Excel(name = "指导粒度(1为工序，2为工步)")
    private Long guideGranularity;

    /** 工艺id */
    @Excel(name = "工艺id")
    private Long craftId;

    /** 所属工艺编码 */
    @Excel(name = "所属工艺编码")
    private String craftCode;

    /** 指导图url */
    @Excel(name = "指导图url")
    private String guideMapUrl;

    /** 工序材料信息(JSON形式) */
    @Excel(name = "工序材料信息(JSON形式)")
    private String processMaterialInfo;

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

    public void setGuideGranularity(Long guideGranularity) 
    {
        this.guideGranularity = guideGranularity;
    }

    public Long getGuideGranularity() 
    {
        return guideGranularity;
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

    public void setGuideMapUrl(String guideMapUrl) 
    {
        this.guideMapUrl = guideMapUrl;
    }

    public String getGuideMapUrl() 
    {
        return guideMapUrl;
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
            .append("guideGranularity", getGuideGranularity())
            .append("craftId", getCraftId())
            .append("craftCode", getCraftCode())
            .append("guideMapUrl", getGuideMapUrl())
            .append("processMaterialInfo", getProcessMaterialInfo())
            .toString();
    }
}
