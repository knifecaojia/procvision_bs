package com.imustsz.craft.domain.json;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

/**
 * 工艺信息
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ProcessInfo {
    private String processNo;
    private String processVersion;
    private String processName;
    private String processDesc;
}
