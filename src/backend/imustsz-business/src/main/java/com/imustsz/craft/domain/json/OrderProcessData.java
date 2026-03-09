package com.imustsz.craft.domain.json;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class OrderProcessData {
    @JsonProperty("process_info")
    private ProcessInfo processInfo;
    @JsonProperty("operation_list")
    private List<Operation> operationList;
}

