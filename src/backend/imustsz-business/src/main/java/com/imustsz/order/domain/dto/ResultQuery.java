package com.imustsz.order.domain.dto;

import com.imustsz.common.exception.ServiceException;
import lombok.Data;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

/** All result views use the same record-time interval, inclusive of both selected dates. */
@Data
public class ResultQuery {
    private String startDate;
    private String endDate;
    private String product;
    private String prodBatchNo;
    private String workOrderCode;
    private String process;
    private String step;
    private Integer algResult;
    private Boolean hasImage;
    private Integer pageNum = 1;
    private Integer pageSize = 12;

    public ResultQuery validated() {
        try {
            LocalDate end = endDate == null || endDate.isEmpty() ? LocalDate.now() : LocalDate.parse(endDate);
            LocalDate start = startDate == null || startDate.isEmpty() ? end.minusDays(6) : LocalDate.parse(startDate);
            if (end.isBefore(start) || ChronoUnit.DAYS.between(start, end) > 365) {
                throw new ServiceException("查询开始日期不能晚于结束日期，且范围不能超过366天");
            }
            startDate = start.toString();
            endDate = end.toString();
        } catch (java.time.format.DateTimeParseException ex) {
            throw new ServiceException("日期格式应为 yyyy-MM-dd");
        }
        product = clean(product);
        prodBatchNo = clean(prodBatchNo);
        workOrderCode = clean(workOrderCode);
        process = clean(process);
        step = clean(step);
        if (algResult != null && !Arrays.asList(-1, 0, 1, 2).contains(algResult)) {
            throw new ServiceException("检测结果参数无效");
        }
        if (pageNum == null || pageNum < 1 || pageNum > 1000000 || pageSize == null || pageSize < 1 || pageSize > 100) {
            throw new ServiceException("分页参数无效，每页允许1至100条");
        }
        return this;
    }

    private String clean(String value) {
        if (value == null) return null;
        value = value.trim();
        if (value.length() > 100) throw new ServiceException("查询关键词不能超过100个字符");
        return value.isEmpty() ? null : value;
    }
    public String getStartTime() { return startDate + " 00:00:00"; }
    public String getEndTimeExclusive() { return LocalDate.parse(endDate).plusDays(1) + " 00:00:00"; }
    public int getOffset() { return (pageNum - 1) * pageSize; }
}
