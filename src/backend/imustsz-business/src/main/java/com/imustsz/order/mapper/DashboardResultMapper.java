package com.imustsz.order.mapper;

import com.imustsz.order.domain.dto.ResultQuery;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

public interface DashboardResultMapper {
    // Keep pageNum/pageSize nested under q: supportMethodsArguments=true must not
    // auto-page aggregates or append a second LIMIT to these explicitly bounded queries.
    Map<String, Object> selectRecordStats(@Param("q") ResultQuery query);
    List<Map<String, Object>> selectTaskStats(@Param("q") ResultQuery query);
    List<Map<String, Object>> selectTrend(@Param("q") ResultQuery query);
    List<Map<String, Object>> selectAlgStats(@Param("q") ResultQuery query);
    List<Map<String, Object>> selectNgSteps(@Param("q") ResultQuery query);
    List<Map<String, Object>> selectRecords(@Param("q") ResultQuery query, @Param("offset") int offset, @Param("limit") int limit);
}
