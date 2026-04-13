package com.imustsz.process.domain;

import lombok.Data;

@Data
public class AlgDataJson {
    private Integer step_index;
    private String result_status;
    private String defect_result;
    private Boolean is_manual_review;
    private String debug;
    private String ng_reason;
}
