package com.imustsz.algorithm.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.imustsz.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.imustsz.common.annotation.Excel;

/**
 * 算法对象 biz_algorithm
 * 
 * @author imustsz
 * @date 2025-12-22
 */
public class BizAlgorithm extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 算法名称 */
    @Excel(name = "算法名称")
    private String name;

    /** 算法版本 */
    @Excel(name = "算法版本")
    private String version;

    /** 算法描述 */
    @Excel(name = "算法描述")
    private String desc;

    /** minio上的算法objectName */
    @Excel(name = "minio上的算法objectName")
    private String objectName;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
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

    public void setObjectName(String objectName) 
    {
        this.objectName = objectName;
    }

    public String getObjectName() 
    {
        return objectName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .append("version", getVersion())
            .append("desc", getDesc())
            .append("objectName", getObjectName())
            .toString();
    }
}
