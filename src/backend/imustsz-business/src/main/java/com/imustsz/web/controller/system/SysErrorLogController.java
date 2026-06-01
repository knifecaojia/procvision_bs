package com.imustsz.web.controller.system;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.imustsz.common.core.domain.entity.SysErrorLog;
import com.imustsz.system.service.ISysErrorLogService;
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
import com.imustsz.common.utils.poi.ExcelUtil;
import com.imustsz.common.core.page.TableDataInfo;

/**
 * 系统异常日志Controller
 * 
 * @author ruoyi
 * @date 2026-05-23
 */
@RestController
@RequestMapping("/system/errlog")
public class SysErrorLogController extends BaseController
{
    @Autowired
    private ISysErrorLogService sysErrorLogService;

    /**
     * 查询系统异常日志列表
     */
    @PreAuthorize("@ss.hasPermi('system:errlog:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysErrorLog sysErrorLog)
    {
        startPage();
        List<SysErrorLog> list = sysErrorLogService.selectSysErrorLogList(sysErrorLog);
        return getDataTable(list);
    }

    /**
     * 导出系统异常日志列表
     */
    @PreAuthorize("@ss.hasPermi('system:errlog:export')")
    @Log(title = "系统异常日志", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysErrorLog sysErrorLog)
    {
        List<SysErrorLog> list = sysErrorLogService.selectSysErrorLogList(sysErrorLog);
        ExcelUtil<SysErrorLog> util = new ExcelUtil<SysErrorLog>(SysErrorLog.class);
        util.exportExcel(response, list, "系统异常日志数据");
    }

    /**
     * 获取系统异常日志详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:errlog:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(sysErrorLogService.selectSysErrorLogById(id));
    }

    /**
     * 新增系统异常日志
     */
    @PreAuthorize("@ss.hasPermi('system:errlog:add')")
    @Log(title = "系统异常日志", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysErrorLog sysErrorLog)
    {
        return toAjax(sysErrorLogService.insertSysErrorLog(sysErrorLog));
    }

    /**
     * 修改系统异常日志
     */
    @PreAuthorize("@ss.hasPermi('system:errlog:edit')")
    @Log(title = "系统异常日志", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysErrorLog sysErrorLog)
    {
        return toAjax(sysErrorLogService.updateSysErrorLog(sysErrorLog));
    }

    /**
     * 删除系统异常日志
     */
    @PreAuthorize("@ss.hasPermi('system:errlog:remove')")
    @Log(title = "系统异常日志", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(sysErrorLogService.deleteSysErrorLogByIds(ids));
    }

    @PreAuthorize("@ss.hasPermi('system:errlog:remove')")
    @Log(title = "系统异常日志", businessType = BusinessType.CLEAN)
    @DeleteMapping("/clean")
    public AjaxResult cleanErrLog()
    {
        sysErrorLogService.cleanSysErrorLog();
        return success();
    }
}
