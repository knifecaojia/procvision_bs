package com.imustsz.craft.domain.dto;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
public class LabelTrialRequest {
    private Long id;
    private String version;
    private String originalKey;
    private String originalToken;
    private int width;
    private int height;
    private boolean packageMode;
    private JsonNode coordsInfo;
}
