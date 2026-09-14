package com.imustsz.craft.domain.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 工步易错物料信息。
 */
@Data
public class StepMaterialInfo {
    @JsonProperty("material_no")
    private String materialNo;

    @JsonProperty("material_name")
    private String materialName;

    @JsonProperty("error_prevention_mark")
    private String errorPreventionMark;
}
