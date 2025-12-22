package com.imustsz.craft.domain;

import com.imustsz.common.annotation.Excel;
import com.imustsz.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 工艺信息对象 craft
 * 
 * @author imustsz
 * @date 2025-12-18
 */
public class Craft extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 编码 */
    @Excel(name = "编码")
    private String code;

    @Excel(name = "编码")
    private String name;

    /** 版本 */
    @Excel(name = "版本")
    private String version;

    /** 说明 */
    @Excel(name = "说明")
    private String desc;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public void setVersion(String version) 
    {
        this.version = version;
    }

    public String getVersion() 
    {
        return version;
    }

    public void setDesc(String desc)
    {
        this.desc = desc;
    }

    public String getDesc()
    {
        return desc;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("code", getCode())
            .append("name", getName())
            .append("version", getVersion())
            .append("desc", getDesc())
            .toString();
    }
}
