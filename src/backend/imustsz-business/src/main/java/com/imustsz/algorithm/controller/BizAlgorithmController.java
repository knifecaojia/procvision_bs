package com.imustsz.algorithm.controller;

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
import com.imustsz.algorithm.domain.BizAlgorithm;
import com.imustsz.algorithm.service.IBizAlgorithmService;
import com.imustsz.common.utils.poi.ExcelUtil;
import com.imustsz.common.core.page.TableDataInfo;

/**
 * 算法Controller
 * 
 * @author imustsz
 * @date 2025-12-22
 */
@RestController
@RequestMapping("/algorithm")
public class BizAlgorithmController extends BaseController
{
    @Autowired
    private IBizAlgorithmService bizAlgorithmService;

    /**
     * 查询算法列表
     */
    @PreAuthorize("@ss.hasPermi('algorithm:algorithm:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizAlgorithm bizAlgorithm)
    {
        startPage();
        List<BizAlgorithm> list = bizAlgorithmService.selectBizAlgorithmList(bizAlgorithm);
        return getDataTable(list);
    }

    /**
     * 导出算法列表
     */
    @PreAuthorize("@ss.hasPermi('algorithm:algorithm:export')")
    @Log(title = "算法", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BizAlgorithm bizAlgorithm)
    {
        List<BizAlgorithm> list = bizAlgorithmService.selectBizAlgorithmList(bizAlgorithm);
        ExcelUtil<BizAlgorithm> util = new ExcelUtil<BizAlgorithm>(BizAlgorithm.class);
        util.exportExcel(response, list, "算法数据");
    }

    /**
     * 获取算法详细信息
     */
    @PreAuthorize("@ss.hasPermi('algorithm:algorithm:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(bizAlgorithmService.selectBizAlgorithmById(id));
    }

    /**
     * 新增算法
     */
    @PreAuthorize("@ss.hasPermi('algorithm:algorithm:add')")
    @Log(title = "算法", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BizAlgorithm bizAlgorithm)
    {
        return toAjax(bizAlgorithmService.insertBizAlgorithm(bizAlgorithm));
    }

    /**
     * 修改算法
     */
    @PreAuthorize("@ss.hasPermi('algorithm:algorithm:edit')")
    @Log(title = "算法", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BizAlgorithm bizAlgorithm)
    {
        return toAjax(bizAlgorithmService.updateBizAlgorithm(bizAlgorithm));
    }

    /**
     * 删除算法
     */
    @PreAuthorize("@ss.hasPermi('algorithm:algorithm:remove')")
    @Log(title = "算法", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(bizAlgorithmService.deleteBizAlgorithmByIds(ids));
    }
}
