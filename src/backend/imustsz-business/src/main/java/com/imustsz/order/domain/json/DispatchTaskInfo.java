package com.imustsz.order.domain.json;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.imustsz.craft.domain.json.Step;
import lombok.Data;

import java.util.List;

@Data
public class DispatchTaskInfo {
    private String operationNo;
    private String operationName;
    private String dispatchQuantity;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String plannedStartTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String plannedEndTime;
    private String workerCode;
    private String workerName;
    private List<Step> stepList;
}

