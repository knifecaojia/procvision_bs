package com.imustsz.order.service.impl;

import com.imustsz.common.utils.bean.MinioUtils;
import com.imustsz.order.domain.GroupByStatus;
import com.imustsz.order.domain.StatisticsData;
import com.imustsz.order.mapper.BizWorkOrderMapper;
import com.imustsz.order.service.IDashboardService;
import com.imustsz.process.domain.BizProcessRecord;
import com.imustsz.process.mapper.BizProcessRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashBoardServiceImpl implements IDashboardService {

    @Autowired
    private BizWorkOrderMapper workOrderMapper;

    @Autowired
    private BizProcessRecordMapper processRecordMapper;

    @Autowired
    private MinioUtils minioUtils;

    @Autowired
    private BizProcessRecordMapper bizProcessRecordMapper;

    @Override
    public StatisticsData getKpiStats() {
        StatisticsData stats = new StatisticsData();

        List<GroupByStatus> data = workOrderMapper.countOrders();
        int total = data.stream().mapToInt(GroupByStatus::getCount).sum();
        stats.setTotal(total);

        int abnormal = data.stream().filter(item -> item.getStatus() == -1 || item.getStatus() == -2 || item.getStatus() == 1).mapToInt(GroupByStatus::getCount).sum();
        stats.setAbnormal(abnormal);

        int completed = data.stream().filter(item -> item.getStatus() == 3 || item.getStatus() == 4).mapToInt(GroupByStatus::getCount).sum();
        stats.setCompleted(completed);

        int inProgress = data.stream().filter(item -> item.getStatus() == 2).mapToInt(GroupByStatus::getCount).sum();
        stats.setInProgress(inProgress);

        return stats;
    }


    @Override
    public Map<String, Object> getTrendData() {
        // 1. 生成最近7天的连续日期列表 (X轴)
        List<String> dates = new ArrayList<>();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (int i = 6; i >= 0; i--) {
            dates.add(today.minusDays(i).format(formatter));
        }

        String startDate = dates.get(0) + " 00:00:00";
        String endDate = dates.get(dates.size() - 1) + " 23:59:59";

        // 2. 查询数据库
        List<Map<String, Object>> plannedList = workOrderMapper.selectPlannedCountByDate(startDate, endDate);
        List<Map<String, Object>> completedList = workOrderMapper.selectCompletedCountByDate(startDate, endDate);

        // 3. 将查询结果转为 Map 便于按日期匹配 (避免嵌套循环)
        // map的格式为: {"2023-10-18": 15, "2023-10-19": 20}
        Map<String, Long> plannedMap = plannedList.stream().collect(
                Collectors.toMap(
                        m -> m.get("dateStr").toString(),
                        m -> ((Number) m.get("totalCount")).longValue()
                )
        );

        Map<String, Long> completedMap = completedList.stream().collect(
                Collectors.toMap(
                        m -> m.get("dateStr").toString(),
                        m -> ((Number) m.get("totalCount")).longValue()
                )
        );

        // 4. 组装最终的 Y轴 数组 (保证每天都有数据，没有就是0)
        List<Long> plannedData = new ArrayList<>();
        List<Long> actualData = new ArrayList<>();

        // 缩略日期格式用于图表展示，例如 "10-18"
        List<String> displayDates = new ArrayList<>();

        for (String dateStr : dates) {
            displayDates.add(dateStr.substring(5)); // 截取 MM-dd
            plannedData.add(plannedMap.getOrDefault(dateStr, 0L));
            actualData.add(completedMap.getOrDefault(dateStr, 0L));
        }

        // 5. 封装为前端 ECharts 需要的格式
        Map<String, Object> trendData = new HashMap<>();
        trendData.put("dates", displayDates);
        trendData.put("planned", plannedData);
        trendData.put("actual", actualData);

        return trendData;
    }

    @Override
    public List<Map<String, Object>> getStatusData(String startDate, String endDate) {
        List<Map<String, Object>> dbResult = workOrderMapper.selectStatusDistribution(startDate, endDate);
        List<Map<String, Object>> statusData = new ArrayList<>();

        // 预定义状态字典与颜色映射
        Map<Integer, String> nameMap = new HashMap<>();
        nameMap.put(3, "正常完成");
        nameMap.put(4, "手工通过");
        nameMap.put(2, "进行中");
        nameMap.put(1, "待派单");
        nameMap.put(-1, "引导资源未就绪");
        nameMap.put(-2, "检测资源未就绪");

        Map<Integer, String> colorMap = new HashMap<>();
        colorMap.put(3, "#67C23A"); // 绿
        colorMap.put(4, "#33CC99"); // 青绿
        colorMap.put(2, "#409EFF"); // 蓝
        colorMap.put(1, "#909399"); // 灰
        colorMap.put(-1, "#F56C6C"); // 红
        colorMap.put(-2, "#E6A23C"); // 橙

        for (Map<String, Object> row : dbResult) {
            Integer status = ((Number) row.get("status")).intValue();
            Long count = ((Number) row.get("totalCount")).longValue();

            Map<String, Object> item = new HashMap<>();
            item.put("name", nameMap.getOrDefault(status, "未知状态(" + status + ")"));
            item.put("value", count);

            Map<String, String> itemStyle = new HashMap<>();
            itemStyle.put("color", colorMap.getOrDefault(status, "#CCCCCC"));
            item.put("itemStyle", itemStyle);

            statusData.add(item);
        }
        return statusData;
    }

    @Override
    public List<Map<String, Object>> getRecentRecords(String startDate, String endDate) throws Exception {
        List<BizProcessRecord> dbResult = processRecordMapper.selectRecordByDate(startDate, endDate);
        List<Map<String, Object>> statusData = new ArrayList<>();

        for (BizProcessRecord record : dbResult) {
            Map<String, Object> item = new HashMap<>();
            item.put("workOrderCode", record.getWorkOrderCode());
            item.put("stepName", record.getStepName());
            item.put("stepStatus", record.getStepStatus());
            item.put("imgUrl", minioUtils.getPresignedUrl(record.getImagePath()));
            statusData.add(item);
        }

        return statusData;
    }

    @Override
    public List<Map<String, Object>> getAlgResultData() {

        // 1. 计算时间范围 (近7天)
        LocalDate today = LocalDate.now();
        String startDate = today.minusDays(6).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " 00:00:00";
        String endDate = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " 23:59:59";

        // 2. 获取并处理【算法执行结果】数据
        List<Map<String, Object>> algResultStats = bizProcessRecordMapper.selectAlgResultData(startDate, endDate);

        List<Map<String, Object>> algResultData = new ArrayList<>();

        // 预定义三个状态，确保即使某天某种状态数量为0，图表图例也能正常显示
        long okCount = 0;
        long ngCount = 0;
        long failCount = 0;

        // 遍历数据库返回的分组统计结果
        for (Map<String, Object> row : algResultStats) {
            Integer algResult = ((Number) row.get("algResult")).intValue();
            Long count = ((Number) row.get("totalCount")).longValue();

            if (algResult == 0) {
                okCount = count;
            } else if (algResult == 1) {
                ngCount = count;
            } else if (algResult == -1) {
                failCount = count;
            }
        }

        // 组装成 ECharts 饼图需要的格式：[{name: '...', value: ...}]
        // 顺序建议固定：OK -> NG -> 失败，以对应前端 color: ['#67C23A', '#F56C6C', '#909399']
        algResultData.add(createPieItem("OK (合格)", okCount));
        algResultData.add(createPieItem("NG (不合格)", ngCount));
        algResultData.add(createPieItem("算法执行失败", failCount));

        return algResultData;
    }

    @Override
    public Map<String, Object> getNgStepStats() {

        LocalDate today = LocalDate.now();
        String startDate = today.minusDays(6).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " 00:00:00";
        String endDate = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " 23:59:59";

        List<Map<String, Object>> ngStepStats = bizProcessRecordMapper.selectNgStepStats(startDate, endDate);

        List<String> ngCategories = new ArrayList<>();
        List<Long> ngCounts = new ArrayList<>();

        for (Map<String, Object> row : ngStepStats) {
            String stepName = (String) row.get("stepName");
            Long count = ((Number) row.get("totalCount")).longValue();

            // 如果工步名称为空，给个默认值防止前端图表显示异常
            ngCategories.add(stepName != null ? stepName : "未知工步");
            ngCounts.add(count);
        }

        Map<String, Object> ngStepData = new HashMap<>();
        ngStepData.put("categories", ngCategories);
        ngStepData.put("counts", ngCounts);

        return ngStepData;
    }

    // 辅助方法：构建饼图数据项
    private Map<String, Object> createPieItem(String name, long value) {
        Map<String, Object> item = new HashMap<>();
        item.put("name", name);
        item.put("value", value);
        return item;
    }
}
