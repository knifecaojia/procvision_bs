package com.imustsz.craft.domain.json;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Operation {
    private OperationInfo operationInfo;
    private List<Step> stepList;
    private List<MaterialInfo> operationMaterialInfo;
}
