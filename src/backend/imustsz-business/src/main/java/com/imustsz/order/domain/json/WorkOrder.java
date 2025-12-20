package com.imustsz.order.domain.json;

import lombok.Data;

// WorkOrder.java
@Data
public class WorkOrder {
    private WorkOrderInfo workOrderInfo;
    private DispatchTaskInfo dispatchTaskInfo;
}
