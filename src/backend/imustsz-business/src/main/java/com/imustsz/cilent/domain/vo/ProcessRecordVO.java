package com.imustsz.cilent.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class ProcessRecordVO {
    private String taskNo;
    private Integer taskStatus;
    private String processNo;
    private String processName;
    private List<StepRecordVO> stepInfo;
}
