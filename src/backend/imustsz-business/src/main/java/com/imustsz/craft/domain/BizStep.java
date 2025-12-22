package com.imustsz.craft.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.imustsz.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.imustsz.common.annotation.Excel;

/**
 * 工步信息对象 biz_step
 * 
 * @author imustsz
 * @date 2025-12-19
 */
public class BizStep extends BaseEntity
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

    /** 内容 */
    @Excel(name = "内容")
    private String content;

    /** 引导图url */
    @Excel(name = "引导图url")
    private String guideMapUrl;

    /** 工序id */
    @Excel(name = "工序id")
    private Long processId;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getProcessId() {
        return processId;
    }

    public void setProcessId(Long processId) {
        this.processId = processId;
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

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getContent()
    {
        return content;
    }

    public void setGuideMapUrl(String guideMapUrl)
    {
        this.guideMapUrl = guideMapUrl;
    }

    public String getGuideMapUrl()
    {
        return guideMapUrl;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("code", getCode())
            .append("name", getName())
            .append("content", getContent())
            .append("guideMapUrl", getGuideMapUrl())
            .append("processId", getProcessId())
            .toString();
    }
}
