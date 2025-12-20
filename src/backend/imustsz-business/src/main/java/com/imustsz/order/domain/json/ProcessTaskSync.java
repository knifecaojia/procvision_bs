package com.imustsz.order.domain.json;


import lombok.Data;

import java.util.List;

@Data
public class ProcessTaskSync {
    private OrderInfo orderInfo;
    private List<WorkOrder> workOrderList;
}

