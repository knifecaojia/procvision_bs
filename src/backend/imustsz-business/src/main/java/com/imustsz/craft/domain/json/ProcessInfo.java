package com.imustsz.craft.domain.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 工艺信息
 */
@Data
public class ProcessInfo {
    @JsonProperty("production_order_no")
    private String productionOrderNo;
    @JsonProperty("process_no")
    private String processNo;
    @JsonProperty("process_version")
    private String processVersion;
    @JsonProperty("process_name")
    private String processName;
    @JsonProperty("process_desc")
    private String processDesc;
}
