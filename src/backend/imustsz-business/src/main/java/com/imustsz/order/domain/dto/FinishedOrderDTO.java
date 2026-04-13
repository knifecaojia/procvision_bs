package com.imustsz.order.domain.dto;

import lombok.Data;

@Data
public class FinishedOrderDTO {
    private String workOrderCode;
    private String workerName;
    private String workerCode;
    private String processCode;
    private String processName;
    private String stepNo;
    private String stepName;
    private String objectName;
}
