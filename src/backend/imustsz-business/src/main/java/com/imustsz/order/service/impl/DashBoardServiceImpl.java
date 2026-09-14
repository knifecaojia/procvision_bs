package com.imustsz.order.service.impl;

import com.imustsz.common.exception.ServiceException;
import com.imustsz.common.utils.bean.MinioUtils;
import com.imustsz.order.domain.dto.ResultQuery;
import com.imustsz.order.mapper.DashboardResultMapper;
import com.imustsz.order.service.IDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
public class DashBoardServiceImpl implements IDashboardService {
    private static final int MAX_REPORT_RECORDS = 500;
    @Autowired private DashboardResultMapper mapper;
    @Autowired private MinioUtils minioUtils;

    @Override
    public Map<String, Object> getOverview(ResultQuery query) {
        query.validated();
        Map<String, Object> result = summary(query);
        Map<String, Object> stats = castMap(result.get("statistics"));
        result.put("rows", records(query, query.getOffset(), query.getPageSize(), true));
        result.put("total", stats.get("recordCount"));
        return result;
    }

    @Override
    public Map<String, Object> getRecords(ResultQuery query) {
        query.validated();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("total", mapper.selectRecordStats(query).get("recordCount"));
        result.put("rows", records(query, query.getOffset(), query.getPageSize(), true));
        return result;
    }

    @Override
    public Map<String, Object> getReport(ResultQuery query) {
        query.validated();
        // Count before loading details. Never silently export only the visible page.
        long count = number(mapper.selectRecordStats(query), "recordCount");
        if (count > MAX_REPORT_RECORDS) {
            throw new ServiceException("本次匹配" + count + "条记录，单份PDF最多500条，请缩小日期范围或增加筛选条件后导出");
        }
        Map<String, Object> result = summary(query);
        result.put("rows", records(query, 0, MAX_REPORT_RECORDS, false));
        result.put("total", count);
        result.put("generatedAt", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return result;
    }

    private Map<String, Object> summary(ResultQuery query) {
        Map<String, Object> stats = new LinkedHashMap<>(mapper.selectRecordStats(query));
        long total = 0, completed = 0, inProgress = 0, abnormal = 0, pending = 0;
        List<Map<String, Object>> statusData = new ArrayList<>();
        for (Map<String, Object> row : mapper.selectTaskStats(query)) {
            int status = row.get("status") == null ? Integer.MIN_VALUE : ((Number) row.get("status")).intValue();
            long count = number(row, "totalCount");
            total += count;
            if (status == 3 || status == 4) completed += count;
            if (status == 2) inProgress += count;
            if (status == -1 || status == -2) abnormal += count;
            if (status == 1) pending += count;
            statusData.add(pie(statusName(status), count));
        }
        stats.put("total", total); stats.put("completed", completed);
        stats.put("inProgress", inProgress); stats.put("abnormal", abnormal); stats.put("pending", pending);
        List<String> dates = new ArrayList<>();
        List<Long> recordCounts = new ArrayList<>(), imageCounts = new ArrayList<>();
        Map<String, Map<String, Object>> byDate = new HashMap<>();
        for (Map<String, Object> row : mapper.selectTrend(query)) byDate.put(row.get("dateStr").toString(), row);
        LocalDate end = LocalDate.parse(query.getEndDate());
        for (LocalDate date = LocalDate.parse(query.getStartDate()); !date.isAfter(end); date = date.plusDays(1)) {
            String key = date.toString();
            dates.add(key);
            Map<String, Object> row = byDate.getOrDefault(key, Collections.emptyMap());
            recordCounts.add(number(row, "recordCount")); imageCounts.add(number(row, "imageCount"));
        }
        Map<String, Object> trend = new LinkedHashMap<>();
        trend.put("dates", dates); trend.put("records", recordCounts); trend.put("images", imageCounts);
        Map<Integer, Long> algCounts = new HashMap<>();
        for (Map<String, Object> row : mapper.selectAlgStats(query)) {
            algCounts.put(((Number) row.get("algResult")).intValue(), number(row, "totalCount"));
        }
        List<Map<String, Object>> algData = Arrays.asList(pie("OK（合格）", algCounts.getOrDefault(0, 0L)),
            pie("NG（不合格）", algCounts.getOrDefault(1, 0L)), pie("执行失败", algCounts.getOrDefault(-1, 0L)),
            pie("未检测/未知", algCounts.getOrDefault(2, 0L)));
        List<String> categories = new ArrayList<>();
        List<Long> counts = new ArrayList<>();
        for (Map<String, Object> row : mapper.selectNgSteps(query)) {
            categories.add(Objects.toString(row.get("stepName"), "未知工步") + " ["
                + Objects.toString(row.get("processCode"), "-") + "/" + Objects.toString(row.get("stepCode"), "-") + "]");
            counts.add(number(row, "totalCount"));
        }
        Map<String, Object> ng = new LinkedHashMap<>(); ng.put("categories", categories); ng.put("counts", counts);
        Map<String, Object> charts = new LinkedHashMap<>();
        charts.put("trendData", trend); charts.put("statusData", statusData);
        charts.put("algResultData", algData); charts.put("ngStepStats", ng);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("statistics", stats); result.put("charts", charts); result.put("query", query);
        return result;
    }

    private List<Map<String, Object>> records(ResultQuery query, int offset, int limit, boolean preview) {
        List<Map<String, Object>> rows = mapper.selectRecords(query, offset, limit);
        for (Map<String, Object> row : rows) {
            String path = Objects.toString(row.get("imagePath"), "").trim();
            row.put("hasImage", !path.isEmpty());
            row.put("imgUrl", "");
            if (preview && !path.isEmpty()) {
                try { row.put("imgUrl", minioUtils.getPresignedUrl(path)); }
                catch (Exception ex) { row.put("imageError", "图片暂不可用"); }
            }
        }
        return rows;
    }
    private long number(Map<String, Object> row, String key) {
        Object value = row.get(key); return value instanceof Number ? ((Number) value).longValue() : 0;
    }
    @SuppressWarnings("unchecked")
    private Map<String, Object> castMap(Object value) { return (Map<String, Object>) value; }
    private Map<String, Object> pie(String name, long value) {
        Map<String, Object> result = new LinkedHashMap<>(); result.put("name", name); result.put("value", value); return result;
    }
    private String statusName(int status) {
        switch (status) {
            case 3: return "正常完成"; case 4: return "手工通过"; case 2: return "进行中";
            case 1: return "待派单"; case -1: return "引导资源未就绪"; case -2: return "检测资源未就绪";
            default: return "未知状态";
        }
    }
}
