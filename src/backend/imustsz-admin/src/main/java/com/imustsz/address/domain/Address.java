package com.imustsz.address.domain;

import java.math.BigDecimal;

import com.imustsz.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.imustsz.common.annotation.Excel;

/**
 * 地址信息对象 address
 * 
 * @author ruoyi
 * @date 2025-09-30
 */
public class Address extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 地址id */
    private Long id;

    /** 名称 */
    @Excel(name = "名称")
    private String name;

    /** 省份 */
    @Excel(name = "省份")
    private String province;

    /** 城市 */
    @Excel(name = "城市")
    private String city;

    /** 县区 */
    @Excel(name = "县区")
    private String county;

    /** 乡镇街道 */
    @Excel(name = "乡镇街道")
    private String village;

    /** 村社区 */
    @Excel(name = "村社区")
    private String hamlet;

    /** 详细地址 */
    @Excel(name = "详细地址")
    private String address;

    /** 面积（平方米） */
    @Excel(name = "面积", readConverterExp = "平=方米")
    private BigDecimal area;

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

    public void setProvince(String province) 
    {
        this.province = province;
    }

    public String getProvince() 
    {
        return province;
    }

    public void setCity(String city) 
    {
        this.city = city;
    }

    public String getCity() 
    {
        return city;
    }

    public void setCounty(String county) 
    {
        this.county = county;
    }

    public String getCounty() 
    {
        return county;
    }

    public void setVillage(String village) 
    {
        this.village = village;
    }

    public String getVillage() 
    {
        return village;
    }

    public void setHamlet(String hamlet) 
    {
        this.hamlet = hamlet;
    }

    public String getHamlet() 
    {
        return hamlet;
    }

    public void setAddress(String address) 
    {
        this.address = address;
    }

    public String getAddress() 
    {
        return address;
    }

    public void setArea(BigDecimal area) 
    {
        this.area = area;
    }

    public BigDecimal getArea() 
    {
        return area;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .append("province", getProvince())
            .append("city", getCity())
            .append("county", getCounty())
            .append("village", getVillage())
            .append("hamlet", getHamlet())
            .append("address", getAddress())
            .append("area", getArea())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
