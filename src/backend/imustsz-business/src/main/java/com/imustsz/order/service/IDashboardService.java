package com.imustsz.order.service;

import com.imustsz.order.domain.StatisticsData;

import java.util.List;
import java.util.Map;

public interface IDashboardService {
    StatisticsData getKpiStats();

    Map<String, Object> getTrendData();

    List<Map<String, Object>> getStatusData(String startDate, String endDate);

    List<Map<String, Object>> getRecentRecords(String startDate, String endDate) throws Exception;

    List<Map<String, Object>> getAlgResultData();

    Map<String, Object> getNgStepStats();
}
