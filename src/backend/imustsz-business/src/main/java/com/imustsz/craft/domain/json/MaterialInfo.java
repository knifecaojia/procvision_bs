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
}
