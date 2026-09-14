package com.imustsz.order.service;
import com.imustsz.order.domain.dto.ResultQuery;
import java.util.Map;

public interface IDashboardService {
    Map<String, Object> getOverview(ResultQuery query);
    Map<String, Object> getRecords(ResultQuery query);
    Map<String, Object> getReport(ResultQuery query);
}
