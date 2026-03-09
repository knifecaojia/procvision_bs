package com.imustsz.order.domain.json;

import lombok.Data;

import java.util.List;

@Data
public class WorkOrderTaskData {
    private String productionOrderNo;
    private List<WorkOrder> workOrderList;
}
