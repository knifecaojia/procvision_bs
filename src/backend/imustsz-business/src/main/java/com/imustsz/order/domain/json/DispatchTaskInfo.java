package com.imustsz.order.domain.json;


import com.imustsz.craft.domain.json.Step;
import lombok.Data;

import java.util.List;

@Data
public class DispatchTaskInfo {
    private String operationNo;
    private String operationName;
    private String dispatchQuantity;
    private String plannedStartTime;
    private String plannedEndTime;
    private String workerCode;
    private String workerName;
    private List<Step> stepList;
}

