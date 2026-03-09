package com.imustsz.craft.domain.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OperationInfo {
    @JsonProperty("operation_no")
    private String operationNo;
    @JsonProperty("operation_name")
    private String operationName;
    @JsonProperty("operation_desc")
    private String operationDesc;
}
