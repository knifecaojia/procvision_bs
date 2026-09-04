package com.imustsz.craft.domain;

import com.imustsz.common.annotation.Excel;
import com.imustsz.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 工序步骤工艺过滤规则对象 biz_craft_filter
 *
 * @author ruoyi
 */
public class BizCraftFilter extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 规则ID */
    private Integer id;

    /** 产品类型（0:天线, 1:板级, 2:模块） */
    @Excel(name = "产品类型", readConverterExp = "0=天线,1=板级,2=模块")
    private Integer productType;

    /** 独立绑定的工序名称 */
    @Excel(name = "工序名称")
    private String processName;

    /** 步骤关键词 */
    @Excel(name = "过滤关键词")
    private String keyword;

    /** 匹配模式（1:精确, 2:模糊包含, 3:正则） */
    @Excel(name = "匹配模式", readConverterExp = "1=精确,2=模糊包含,3=正则")
    private Integer matchMode;

    /** 是否启用（1:启用, 0:停用） */
    @Excel(name = "是否启用", readConverterExp = "1=启用,0=停用")
    private Integer isEnabled;

    /** 逻辑删除（0:正常, 1:已删除） */
    private Integer isDeleted;

    public void setId(Integer id) { this.id = id; }
    public Integer getId() { return id; }

    public void setProductType(Integer productType) { this.productType = productType; }
    public Integer getProductType() { return productType; }

    public void setProcessName(String processName) { this.processName = processName; }
    public String getProcessName() { return processName; }

    public void setKeyword(String keyword) { this.keyword = keyword; }
    public String getKeyword() { return keyword; }

    public void setMatchMode(Integer matchMode) { this.matchMode = matchMode; }
    public Integer getMatchMode() { return matchMode; }

    public void setIsEnabled(Integer isEnabled) { this.isEnabled = isEnabled; }
    public Integer getIsEnabled() { return isEnabled; }

    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }
    public Integer getIsDeleted() { return isDeleted; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("productType", getProductType())
                .append("processName", getProcessName())
                .append("keyword", getKeyword())
                .append("matchMode", getMatchMode())
                .append("isEnabled", getIsEnabled())
                .append("remark", getRemark())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("isDeleted", getIsDeleted())
                .toString();
    }
}