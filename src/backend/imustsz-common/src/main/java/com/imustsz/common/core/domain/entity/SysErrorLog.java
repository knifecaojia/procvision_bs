package com.imustsz.common.core.domain.entity;

import com.imustsz.common.annotation.Excel;
import com.imustsz.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 系统异常日志对象 sys_error_log
 * 
 * @author ruoyi
 * @date 2026-05-23
 */
public class SysErrorLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 请求URI */
    @Excel(name = "请求URI")
    private String requestUri;

    /** 请求方式 */
    @Excel(name = "请求方式")
    private String requestMethod;

    /** 异常名称 */
    @Excel(name = "异常名称")
    private String exceptionName;

    /** 异常信息 */
    @Excel(name = "异常信息")
    private String exceptionMessage;


    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setRequestUri(String requestUri) 
    {
        this.requestUri = requestUri;
    }

    public String getRequestUri() 
    {
        return requestUri;
    }

    public void setRequestMethod(String requestMethod) 
    {
        this.requestMethod = requestMethod;
    }

    public String getRequestMethod() 
    {
        return requestMethod;
    }

    public void setExceptionName(String exceptionName) 
    {
        this.exceptionName = exceptionName;
    }

    public String getExceptionName() 
    {
        return exceptionName;
    }

    public void setExceptionMessage(String exceptionMessage) 
    {
        this.exceptionMessage = exceptionMessage;
    }

    public String getExceptionMessage() 
    {
        return exceptionMessage;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("requestUri", getRequestUri())
            .append("requestMethod", getRequestMethod())
            .append("exceptionName", getExceptionName())
            .append("exceptionMessage", getExceptionMessage())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .toString();
    }
}
