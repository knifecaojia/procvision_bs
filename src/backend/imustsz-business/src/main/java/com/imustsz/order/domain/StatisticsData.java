package com.imustsz.order.domain;

import lombok.Data;

@Data
public class StatisticsData {
    private Integer total;
    private Integer completed;
    private Integer inProgress;
    private Integer abnormal;
}
