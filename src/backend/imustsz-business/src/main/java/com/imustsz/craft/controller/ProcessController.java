package com.imustsz.craft.controller;

import com.imustsz.common.annotation.Log;
import com.imustsz.common.core.controller.BaseController;
import com.imustsz.common.core.domain.AjaxResult;
import com.imustsz.common.core.page.TableDataInfo;
import com.imustsz.common.enums.BusinessType;
import com.imustsz.common.utils.poi.ExcelUtil;
import com.imustsz.craft.domain.Process;
import com.imustsz.craft.service.IProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 工序信息Controller
 * 
 * @author imustsz
 * @date 2025-12-18
 */
@RestController
@RequestMapping("/craft/process")
public class ProcessController extends BaseController
{
    @Autowired
    private IProcessService processService;

    /**
     * 查询工序信息列表
     */
    @PreAuthorize("@ss.hasPermi('process:process:list')")
    @GetMapping("/list")
    public TableDataInfo list(Process process)
    {
        startPage();
        List<Process> list = processService.selectProcessList(process);
        return getDataTable(list);
    }

    /**
     * 导出工序信息列表
     */
    @PreAuthorize("@ss.hasPermi('process:process:export')")
    @Log(title = "工序信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Process process)
    {
        List<Process> list = processService.selectProcessList(process);
        ExcelUtil<Process> util = new ExcelUtil<Process>(Process.class);
        util.exportExcel(response, list, "工序信息数据");
    }

    /**
     * 获取工序信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('process:process:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(processService.selectProcessById(id));
    }

    /**
     * 新增工序信息
     */
    @PreAuthorize("@ss.hasPermi('process:process:add')")
    @Log(title = "工序信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Process process)
    {
        return toAjax(processService.insertProcess(process));
    }

    /**
     * 修改工序信息
     */
    @PreAuthorize("@ss.hasPermi('process:process:edit')")
    @Log(title = "工序信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Process process)
    {
        return toAjax(processService.updateProcess(process));
    }

    /**
     * 删除工序信息
     */
    @PreAuthorize("@ss.hasPermi('process:process:remove')")
    @Log(title = "工序信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(processService.deleteProcessByIds(ids));
    }
}
