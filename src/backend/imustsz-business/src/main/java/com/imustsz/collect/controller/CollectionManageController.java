package com.imustsz.collect.controller;

import com.imustsz.collect.domain.*;
import com.imustsz.collect.service.impl.CollectionManageService;
import com.imustsz.common.annotation.Log;
import com.imustsz.common.core.controller.BaseController;
import com.imustsz.common.core.domain.AjaxResult;
import com.imustsz.common.core.page.TableDataInfo;
import com.imustsz.common.enums.BusinessType;
import org.springframework.web.bind.annotation.*;

/**
 * Dedicated type=1 endpoints. Shared /collection/data upload/edit behavior stays intact.
 */
@RestController
@RequestMapping("/collection/manage")
public class CollectionManageController extends BaseController {
    @org.springframework.beans.factory.annotation.Autowired
    private com.imustsz.collect.service.impl.CollectionImageExportService exportService;
    private final CollectionManageService service;

    public CollectionManageController(CollectionManageService service) {
        this.service = service;
    }

    @GetMapping("/list")
    public TableDataInfo list(CollectionManageQuery query) {
        query.validate();
        startPage();
        return getDataTable(service.list(query));
    }

    @PostMapping("/export-images")
    @Log(title = "采集图像批量导出", businessType = BusinessType.EXPORT)
    public void exportImages(@RequestBody CollectionExportRequest request, javax.servlet.http.HttpServletResponse response) throws Exception {
        java.nio.file.Path archive = exportService.createArchive(request);
        try {
            response.setContentType("application/zip");
            response.setHeader("Content-Disposition", "attachment; filename=collection-images.zip");
            response.setHeader("Cache-Control", "no-store");
            response.setContentLengthLong(java.nio.file.Files.size(archive));
            java.nio.file.Files.copy(archive, response.getOutputStream());
        } finally {
            java.nio.file.Files.deleteIfExists(archive);
        }
    }

    @GetMapping("/{id}/storage")
    public AjaxResult storageInfo(@PathVariable Long id) throws Exception {
        return success(service.storageInfo(id));
    }

    @PutMapping("/storage")
    @Log(title = "采集图像存储转换", businessType = BusinessType.UPDATE)
    public AjaxResult convert(@RequestBody CollectionStorageRequest request) throws Exception {
        return success(service.convert(request, getUsername()));
    }
}
