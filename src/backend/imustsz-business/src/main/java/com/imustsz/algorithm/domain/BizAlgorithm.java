package com.imustsz.algorithm.domain;

import com.imustsz.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.imustsz.common.annotation.Excel;
import org.springframework.web.multipart.MultipartFile;

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

    /** 算法编码 */
    @Excel(name = "算法编码")
    private String code;

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

    /** 算法url */
    @Excel(name = "算法url")
    private String url;

    private String fileName;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

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
            .append("code", getCode())
            .append("version", getVersion())
            .append("desc", getDesc())
            .append("objectName", getObjectName())
            .append("url", getUrl())
            .toString();
    }
}
