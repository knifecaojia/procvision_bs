package com.imustsz.process.controller;

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
import com.imustsz.process.domain.BizProcessRecord;
import com.imustsz.process.service.IBizProcessRecordService;
import com.imustsz.common.utils.poi.ExcelUtil;
import com.imustsz.common.core.page.TableDataInfo;

/**
 * 过程记录Controller
 * 
 * @author imustsz
 * @date 2025-12-22
 */
@RestController
@RequestMapping("/process/record")
public class BizProcessRecordController extends BaseController
{
    @Autowired
    private IBizProcessRecordService bizProcessRecordService;

    /**
     * 查询过程记录列表
     */
    @PreAuthorize("@ss.hasPermi('process:record:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizProcessRecord bizProcessRecord) throws Exception {
        startPage();
        List<BizProcessRecord> list = bizProcessRecordService.selectBizProcessRecordList(bizProcessRecord);
        return getDataTable(list);
    }

    /**
     * 导出过程记录列表
     */
    @PreAuthorize("@ss.hasPermi('process:record:export')")
    @Log(title = "过程记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BizProcessRecord bizProcessRecord) throws Exception {
        List<BizProcessRecord> list = bizProcessRecordService.selectBizProcessRecordList(bizProcessRecord);
        ExcelUtil<BizProcessRecord> util = new ExcelUtil<BizProcessRecord>(BizProcessRecord.class);
        util.exportExcel(response, list, "过程记录数据");
    }

    /**
     * 获取过程记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('process:record:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(bizProcessRecordService.selectBizProcessRecordById(id));
    }

    /**
     * 新增过程记录
     */
    @PreAuthorize("@ss.hasPermi('process:record:add')")
    @Log(title = "过程记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BizProcessRecord bizProcessRecord)
    {
        return toAjax(bizProcessRecordService.insertBizProcessRecord(bizProcessRecord));
    }

    /**
     * 修改过程记录
     */
    @PreAuthorize("@ss.hasPermi('process:record:edit')")
    @Log(title = "过程记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BizProcessRecord bizProcessRecord)
    {
        return toAjax(bizProcessRecordService.updateBizProcessRecord(bizProcessRecord));
    }

    /**
     * 删除过程记录
     */
    @PreAuthorize("@ss.hasPermi('process:record:remove')")
    @Log(title = "过程记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(bizProcessRecordService.deleteBizProcessRecordByIds(ids));
    }
}
