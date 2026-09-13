package com.imustsz.craft.domain.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MaterialInfo {
    @JsonProperty("material_no")
    private String materialNo;
    @JsonProperty("material_name")
    private String materialName;
    @JsonProperty("material_quantity")
    private Integer materialQuantity;
    @JsonProperty("material_unit")
    private String materialUnit;
    @JsonProperty("error_prevention_mark")
    private String errorPreventionMark;

    //  以下为预留字段
    private String assembly_number; // 装配序号
    private String position_number; // 位号
    private String model_no; // 型号
    private String polarity_direction; // 极性方向
}
