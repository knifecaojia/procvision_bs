package com.imustsz.collect.domain;

import com.imustsz.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.imustsz.common.annotation.Excel;

/**
 * 数据采集对象 biz_data_collection
 * 
 * @author imustsz
 * @date 2026-01-20
 */
public class BizDataCollection extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** Minio Key */
    @Excel(name = "Minio Key")
    private String imagePath;

    /** 数据 */
    @Excel(name = "数据")
    private String data;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("imagePath", getImagePath())
            .append("data", getData())
            .toString();
    }
}
