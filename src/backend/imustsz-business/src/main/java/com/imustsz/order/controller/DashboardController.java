package com.imustsz.order.controller;

import com.imustsz.common.annotation.Log;
import com.imustsz.common.core.controller.BaseController;
import com.imustsz.common.core.domain.AjaxResult;
import com.imustsz.common.enums.BusinessType;
import com.imustsz.common.utils.bean.MinioUtils;
import com.imustsz.order.domain.dto.ResultQuery;
import com.imustsz.order.service.IDashboardService;
import com.imustsz.order.service.impl.ReportImageProcessor;
import com.imustsz.process.domain.BizProcessRecord;
import com.imustsz.process.mapper.BizProcessRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;

@RestController
@RequestMapping("/dashboard")
public class DashboardController extends BaseController {
    @Autowired
    private IDashboardService dashboardService;
    @Autowired
    private BizProcessRecordMapper recordMapper;
    @Autowired
    private MinioUtils minioUtils;
    @Autowired
    private ReportImageProcessor reportImageProcessor;
    @Value("${minio.bucketName}")
    private String bucketName;

    @GetMapping("/overview")
    public AjaxResult overview(ResultQuery query) {
        return success(dashboardService.getOverview(query));
    }

    @GetMapping("/kpiStats")
    public AjaxResult kpiStats(ResultQuery query) {
        return success(dashboardService.getOverview(query).get("statistics"));
    }

    @GetMapping("/chartAnalysis")
    public AjaxResult chartAnalysis(ResultQuery query) {
        return success(dashboardService.getOverview(query).get("charts"));
    }

    @GetMapping("/recentResults")
    public AjaxResult recentResults(ResultQuery query) {
        return success(dashboardService.getRecords(query));
    }

    @Log(title = "装配多媒体PDF报告", businessType = BusinessType.EXPORT)
    @GetMapping("/reportData")
    public AjaxResult reportData(ResultQuery query) {
        return success(dashboardService.getReport(query));
    }

    /**
     * Same authenticated backend as the report: no browser-to-MinIO CORS dependency.
     * Resolve the object key from a record, never accept an arbitrary URL or filesystem path.
     */
    @GetMapping("/records/{id}/image")
    public void image(@PathVariable Long id, @RequestParam(defaultValue = "false") boolean report, HttpServletResponse response) throws Exception {
        BizProcessRecord record = recordMapper.selectBizProcessRecordById(id);
        if (record == null || record.getImagePath() == null || record.getImagePath().trim().isEmpty()) {
            response.sendError(404, "图片不存在");
            return;
        }
        String path = record.getImagePath().trim();
        io.minio.StatObjectResponse stat = minioUtils.getObjectStat(path);
        if (stat.size() > reportImageProcessor.getMaxSourceBytes()) {
            response.sendError(413, "图片超过配置上限" + reportImageProcessor.getMaxSourceBytes() / 1024 / 1024 + "MB");
            return;
        }
        response.setContentType(report ? "image/jpeg" : "application/octet-stream");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("X-Content-Type-Options", "nosniff");
        try (InputStream input = minioUtils.getFileInputStream(bucketName, path)) {
            if (report) {
                reportImageProcessor.writeJpeg(input, response.getOutputStream());
            } else {
                reportImageProcessor.copyBounded(input, response.getOutputStream());
            }
        }
    }
}
