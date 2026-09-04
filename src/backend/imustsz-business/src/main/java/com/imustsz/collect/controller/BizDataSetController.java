package com.imustsz.collect.controller;

import com.imustsz.collect.domain.BizDataset;
import com.imustsz.collect.service.IBizDatasetService;
import com.imustsz.common.annotation.Log;
import com.imustsz.common.core.controller.BaseController;
import com.imustsz.common.core.domain.AjaxResult;
import com.imustsz.common.core.page.TableDataInfo;
import com.imustsz.common.enums.BusinessType;
import com.imustsz.common.utils.file.FileUtils;
import com.imustsz.common.utils.poi.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/collection/dataset")
public class BizDataSetController extends BaseController {

    @Autowired
    private IBizDatasetService BizDatasetService;


    /**
     * 查询数据集列表
     */
//    @PreAuthorize("@ss.hasPermi('collection:dataset:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizDataset BizDataset) {
        startPage();
        List<BizDataset> list = BizDatasetService.selectBizDatasetList(BizDataset);
        return getDataTable(list);
    }

    /**
     * 导出数据集列表
     */
//    @PreAuthorize("@ss.hasPermi('collection:dataset:export')")
    @Log(title = "导出数据集", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BizDataset BizDataset) {
        List<BizDataset> list = BizDatasetService.selectBizDatasetList(BizDataset);
        ExcelUtil<BizDataset> util = new ExcelUtil<BizDataset>(BizDataset.class);
        util.exportExcel(response, list, "数据集数据");
    }

    /**
     * 获取数据集详细信息
     */
//    @PreAuthorize("@ss.hasPermi('collection:dataset:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(BizDatasetService.selectBizDatasetById(id));
    }

    /**
     * 新增数据集
     */
//    @PreAuthorize("@ss.hasPermi('collection:dataset:add')")
    @Log(title = "数据集", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BizDataset BizDataset) {
        int rows = BizDatasetService.insertBizDataset(BizDataset);
        return rows > 0 ? success(BizDataset) : error();
    }

    /**
     * 修改数据集
     */
//    @PreAuthorize("@ss.hasPermi('collection:dataset:edit')")
    @Log(title = "数据集", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BizDataset BizDataset) {
        return toAjax(BizDatasetService.updateBizDataset(BizDataset));
    }

    /**
     * 删除数据集
     */
//    @PreAuthorize("@ss.hasPermi('collection:dataset:remove')")
    @Log(title = "数据集", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(BizDatasetService.deleteBizDatasetByIds(ids));
    }

    /**
     * 下载数据集压缩包（包含原图、标注图、坐标信息和数据集清单）
     */
    @Log(title = "下载数据集", businessType = BusinessType.EXPORT)
    @RequestMapping(value = "/download/{id}", method = {RequestMethod.GET, RequestMethod.POST})
    public void download(@PathVariable Long id, HttpServletResponse response) throws IOException {
        BizDataset dataset = BizDatasetService.selectBizDatasetById(id);
        if (dataset == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "数据集不存在");
            return;
        }

        response.setContentType("application/zip");
        FileUtils.setAttachmentResponseHeader(response, safeFilename(dataset.getName()) + ".zip");
        BizDatasetService.writeDatasetArchive(id, response.getOutputStream());
    }

    private String safeFilename(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return "dataset";
        }
        return filename.replaceAll("[\\\\/:*?\"<>|]", "_");
    }
}
