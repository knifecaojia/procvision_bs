package com.imustsz.craft.domain.json;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

/**
 * 工艺信息
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CrackInfo {
    private String crackNo;
    private String crackVersion;
    private String crackName;
    private String crackDesc;
}
