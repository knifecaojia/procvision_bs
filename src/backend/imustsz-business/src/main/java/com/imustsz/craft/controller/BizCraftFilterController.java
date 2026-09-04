package com.imustsz.craft.controller;


import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.imustsz.common.annotation.Log;
import com.imustsz.common.core.controller.BaseController;
import com.imustsz.common.core.domain.AjaxResult;
import com.imustsz.common.core.page.TableDataInfo;
import com.imustsz.common.enums.BusinessType;
import com.imustsz.common.utils.poi.ExcelUtil;
import com.imustsz.craft.domain.BizCraftFilter;
import com.imustsz.craft.service.IBizCraftFilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 工序步骤工艺过滤规则 Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/filterRule")
public class BizCraftFilterController extends BaseController {

    @Autowired
    private IBizCraftFilterService bizCraftFilterService;

    /**
     * 查询规则列表（分页）
     */
    @PreAuthorize("@ss.hasPermi('process:craftFilter:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizCraftFilter bizCraftFilter) {
        startPage();
        List<BizCraftFilter> list = bizCraftFilterService.selectBizCraftFilterList(bizCraftFilter);
        return getDataTable(list);
    }

    /**
     * 导出规则数据
     */
    @PreAuthorize("@ss.hasPermi('process:craftFilter:export')")
    @Log(title = "工艺过滤规则", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BizCraftFilter bizCraftFilter) {
        List<BizCraftFilter> list = bizCraftFilterService.selectBizCraftFilterList(bizCraftFilter);
        ExcelUtil<BizCraftFilter> util = new ExcelUtil<BizCraftFilter>(BizCraftFilter.class);
        util.exportExcel(response, list, "工艺过滤规则数据");
    }

    /**
     * 获取规则详细信息
     */
    @PreAuthorize("@ss.hasPermi('process:craftFilter:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Integer id) {
        return success(bizCraftFilterService.selectBizCraftFilterById(id));
    }

    /**
     * 新增过滤规则
     */
    @PreAuthorize("@ss.hasPermi('process:craftFilter:add')")
    @Log(title = "工艺过滤规则", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BizCraftFilter bizCraftFilter) {
        bizCraftFilter.setCreateBy(getUsername());
        return toAjax(bizCraftFilterService.insertBizCraftFilter(bizCraftFilter));
    }

    /**
     * 修改过滤规则
     */
    @PreAuthorize("@ss.hasPermi('process:craftFilter:edit')")
    @Log(title = "工艺过滤规则", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BizCraftFilter bizCraftFilter) {
        bizCraftFilter.setUpdateBy(getUsername());
        return toAjax(bizCraftFilterService.updateBizCraftFilter(bizCraftFilter));
    }

    /**
     * 批量/单条删除
     */
    @PreAuthorize("@ss.hasPermi('process:craftFilter:remove')")
    @Log(title = "工艺过滤规则", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Integer[] ids) {
        return toAjax(bizCraftFilterService.deleteBizCraftFilterByIds(ids));
    }

    /**
     * 修改启用/停用状态
     */
    @PreAuthorize("@ss.hasPermi('process:craftFilter:edit')")
    @Log(title = "修改规则状态", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody BizCraftFilter bizCraftFilter) {
        return toAjax(bizCraftFilterService.updateStatus(
                bizCraftFilter.getId(),
                bizCraftFilter.getIsEnabled(),
                getUsername()
        ));
    }

    /**
     * 刷新工艺过滤规则缓存
     */
    @PreAuthorize("@ss.hasPermi('process:craftFilter:edit')")
    @Log(title = "工艺过滤规则", businessType = BusinessType.CLEAN)
    @DeleteMapping("/refreshCache")
    public AjaxResult refreshCache() {
        bizCraftFilterService.refreshCraftFilterCache();
        return success();
    }
}
