package com.imustsz.order.domain.json;

import lombok.Data;

// OrderInfo.java
@Data
public class OrderInfo {
    private String productionOrderNo;
    private String productBatch;
    private String materialNo;
}

