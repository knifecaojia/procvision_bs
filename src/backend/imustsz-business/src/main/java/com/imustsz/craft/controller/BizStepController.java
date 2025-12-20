package com.imustsz.craft.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.imustsz.common.annotation.Log;
import com.imustsz.common.core.controller.BaseController;
import com.imustsz.common.core.domain.AjaxResult;
import com.imustsz.common.enums.BusinessType;
import com.imustsz.craft.domain.BizStep;
import com.imustsz.craft.service.IBizStepService;
import com.imustsz.common.utils.poi.ExcelUtil;
import com.imustsz.common.core.page.TableDataInfo;

/**
 * 工步信息Controller
 * 
 * @author imustsz
 * @date 2025-12-19
 */
@RestController
@RequestMapping("/craft/step")
public class BizStepController extends BaseController
{
    @Autowired
    private IBizStepService bizStepService;

    /**
     * 查询工步信息列表
     */
    @PreAuthorize("@ss.hasPermi('craft:step:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizStep bizStep)
    {
        startPage();
        List<BizStep> list = bizStepService.selectBizStepList(bizStep);
        return getDataTable(list);
    }

    /**
     * 导出工步信息列表
     */
    @PreAuthorize("@ss.hasPermi('craft:step:export')")
    @Log(title = "工步信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BizStep bizStep)
    {
        List<BizStep> list = bizStepService.selectBizStepList(bizStep);
        ExcelUtil<BizStep> util = new ExcelUtil<BizStep>(BizStep.class);
        util.exportExcel(response, list, "工步信息数据");
    }

    /**
     * 获取工步信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('craft:step:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(bizStepService.selectBizStepById(id));
    }

    /**
     * 新增工步信息
     */
    @PreAuthorize("@ss.hasPermi('craft:step:add')")
    @Log(title = "工步信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BizStep bizStep)
    {
        return toAjax(bizStepService.insertBizStep(bizStep));
    }

    /**
     * 修改工步信息
     */
    @PreAuthorize("@ss.hasPermi('craft:step:edit')")
    @Log(title = "工步信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BizStep bizStep)
    {
        return toAjax(bizStepService.updateBizStep(bizStep));
    }

    /**
     * 删除工步信息
     */
    @PreAuthorize("@ss.hasPermi('craft:step:remove')")
    @Log(title = "工步信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(bizStepService.deleteBizStepByIds(ids));
    }
}
