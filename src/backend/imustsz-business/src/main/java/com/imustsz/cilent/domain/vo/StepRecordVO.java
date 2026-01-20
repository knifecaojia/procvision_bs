package com.imustsz.cilent.domain.vo;

import lombok.Data;

@Data
public class StepRecordVO {
    private String stepNo;
    private String stepName;
    private Integer stepStatus;
    private String imgUrl;
    private String algResult;
}
