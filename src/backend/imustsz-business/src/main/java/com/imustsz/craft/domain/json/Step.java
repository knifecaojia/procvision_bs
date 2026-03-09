package com.imustsz.craft.domain.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Step {
    @JsonProperty("step_no")
    private String stepNo;
    @JsonProperty("step_name")
    private String stepName;
    @JsonProperty("step_content")
    private String stepContent;
}
