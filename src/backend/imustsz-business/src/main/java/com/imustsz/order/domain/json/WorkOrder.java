package com.imustsz.order.domain.json;

import lombok.Data;

import java.util.List;

// WorkOrder.java
@Data
public class WorkOrder {
    private String workOrderNo;
    private List<DispatchTask> dispatchTaskInfo;
}
