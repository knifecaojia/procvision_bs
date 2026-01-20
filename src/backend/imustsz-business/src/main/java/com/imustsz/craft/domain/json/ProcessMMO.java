package com.imustsz.craft.domain.json;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ProcessMMO {
    private ProcessInfo processInfo;
    private List<Step> stepList;
    private List<MaterialInfo> processMaterialInfo;
}
