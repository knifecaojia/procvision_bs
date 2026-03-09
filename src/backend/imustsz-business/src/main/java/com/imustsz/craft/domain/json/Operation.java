package com.imustsz.craft.domain.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class Operation {
    @JsonProperty("operation_info")
    private OperationInfo operationInfo;
    @JsonProperty("step_list")
    private List<Step> stepList;
    @JsonProperty("operation_material_info")
    private List<MaterialInfo> operationMaterialInfo;
}
