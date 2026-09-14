package com.imustsz.collect.domain;
import com.imustsz.common.exception.ServiceException;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
@Data
public class CollectionManageQuery {
    private String data;
    private Integer datasetId;
    private String productModel;
    private String productBatch;
    private String processNum;
    private String beginProductTime;
    private String endProductTime;
    public void validate() {
        try {
            if (beginProductTime != null && beginProductTime.isEmpty()) beginProductTime = null;
            if (endProductTime != null && endProductTime.isEmpty()) endProductTime = null;
            DateTimeFormatter f = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss").withResolverStyle(ResolverStyle.STRICT);
            LocalDateTime begin = beginProductTime == null ? null : LocalDateTime.parse(beginProductTime,f);
            LocalDateTime end = endProductTime == null ? null : LocalDateTime.parse(endProductTime,f);
            if (begin != null && end != null && begin.isAfter(end)) throw new ServiceException("生产时间开始值不能晚于结束值");
        } catch (java.time.DateTimeException ex) { throw new ServiceException("生产时间格式应为yyyy-MM-dd HH:mm:ss"); }
    }
}
