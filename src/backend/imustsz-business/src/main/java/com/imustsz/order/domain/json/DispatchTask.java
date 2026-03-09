package com.imustsz.order.domain.json;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DispatchTask {
    private String operationNo;
    private String operationName;
    private Integer dispatchQuantity;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime plannedStartTime;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime plannedEndTime;
    private String prodGroup;
    private String workerCode;
    private String workerName;
}

