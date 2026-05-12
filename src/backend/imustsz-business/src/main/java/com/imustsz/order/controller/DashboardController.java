package com.imustsz.order.controller;

import com.imustsz.common.core.controller.BaseController;
import com.imustsz.common.core.domain.AjaxResult;
import com.imustsz.common.utils.DateUtils;
import com.imustsz.order.service.IDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
public class DashboardController extends BaseController {

    @Autowired
    private IDashboardService dashboardService;

    @GetMapping("/kpiStats")
    public AjaxResult kpiStats() {
        return success(dashboardService.getKpiStats());
    }

    @GetMapping("/chartAnalysis")
    public AjaxResult chartAnalysis() {
        Map<String, Object> data = new HashMap<>();

        // 获取趋势图数据
        Map<String, Object> trendData = dashboardService.getTrendData();
        data.put("trendData", trendData);

        // 可以在这里继续放入其他图表的数据，例如之前设计的状态饼图 statusData
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        data.put("statusData", dashboardService.getStatusData(today.minusDays(7).format(formatter), DateUtils.getDate()));

        data.put("algResultData", dashboardService.getAlgResultData());

        data.put("ngStepStats", dashboardService.getNgStepStats());

        return AjaxResult.success(data);
    }

    @GetMapping("/recentResults")
    public AjaxResult recentResults(String startDate, String endDate) throws Exception {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        startDate = startDate == null ? today.minusDays(7).format(formatter) : startDate;
        endDate = endDate == null ? DateUtils.getDate() : endDate;

        return success(dashboardService.getRecentRecords(startDate + " 00:00:00", endDate + " 23:59:59"));
    }

}
