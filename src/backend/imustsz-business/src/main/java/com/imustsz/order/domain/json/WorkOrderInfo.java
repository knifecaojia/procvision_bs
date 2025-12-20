package com.imustsz.order.domain.json;

import lombok.Data;

// WorkOrderInfo.java
@Data
public class WorkOrderInfo {
    private String workOrderNo;
    private String workOrderQuantity;
    private String processNo;
    private String processVersion;
}
