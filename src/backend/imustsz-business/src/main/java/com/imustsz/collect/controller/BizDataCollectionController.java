package com.imustsz.collect.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.imustsz.collect.service.IOpenCvProcessService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.imustsz.common.annotation.Log;
import com.imustsz.common.core.controller.BaseController;
import com.imustsz.common.core.domain.AjaxResult;
import com.imustsz.common.enums.BusinessType;
import com.imustsz.collect.domain.BizDataCollection;
import com.imustsz.collect.service.IBizDataCollectionService;
import com.imustsz.common.utils.poi.ExcelUtil;
import com.imustsz.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 数据采集Controller
 * 
 * @author imustsz
 * @date 2026-01-20
 */
@RestController
@RequestMapping("/collection/data")
public class BizDataCollectionController extends BaseController
{
    @Autowired
    private IBizDataCollectionService bizDataCollectionService;

    @Autowired
    private IOpenCvProcessService processService;

    /**
     * 查询数据采集列表
     */
    @PreAuthorize("@ss.hasPermi('collection:data:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizDataCollection bizDataCollection)
    {
        startPage();
        List<BizDataCollection> list = bizDataCollectionService.selectBizDataCollectionList(bizDataCollection);
        return getDataTable(list);
    }

    /**
     * 导出数据采集列表
     */
    @PreAuthorize("@ss.hasPermi('collection:data:export')")
    @Log(title = "数据采集", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BizDataCollection bizDataCollection)
    {
        List<BizDataCollection> list = bizDataCollectionService.selectBizDataCollectionList(bizDataCollection);
        ExcelUtil<BizDataCollection> util = new ExcelUtil<BizDataCollection>(BizDataCollection.class);
        util.exportExcel(response, list, "数据采集数据");
    }

    /**
     * 获取数据采集详细信息
     */
    @PreAuthorize("@ss.hasPermi('collection:data:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(bizDataCollectionService.selectBizDataCollectionById(id));
    }

    /**
     * 新增数据采集
     */
    @PreAuthorize("@ss.hasPermi('collection:data:add')")
    @Log(title = "数据采集", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BizDataCollection bizDataCollection)
    {
        return toAjax(bizDataCollectionService.insertBizDataCollection(bizDataCollection));
    }

    /**
     * 修改数据采集
     */
    @PreAuthorize("@ss.hasPermi('collection:data:edit')")
    @Log(title = "数据采集", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BizDataCollection bizDataCollection)
    {
        BizDataCollection collection = bizDataCollectionService.selectBizDataCollectionById(bizDataCollection.getId());
        bizDataCollection.setId(collection.getId());
        return toAjax(bizDataCollectionService.updateBizDataCollection(bizDataCollection));
    }

    /**
     * 删除数据采集
     */
    @PreAuthorize("@ss.hasPermi('collection:data:remove')")
    @Log(title = "数据采集", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(bizDataCollectionService.deleteBizDataCollectionByIds(ids));
    }

    @GetMapping("/check")
    public AjaxResult check(@RequestParam("productionInfo") String productionInfo) {
        AjaxResult result = new AjaxResult();
        result.put("isExist", bizDataCollectionService.checkProduction(productionInfo) > 0);
        return result;
    }

    @PostMapping("/process")
    public AjaxResult processImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") String type) {
        try {
            // 获取处理后的 Base64 图片
            String processedBase64 = processService.processAndReturnBase64(file.getBytes(), type);
            AjaxResult a = new AjaxResult();
            a.put("data", processedBase64);
            return a;
        } catch (Exception e) {
            return AjaxResult.error("处理失败: " + e.getMessage());
        }
    }
}
