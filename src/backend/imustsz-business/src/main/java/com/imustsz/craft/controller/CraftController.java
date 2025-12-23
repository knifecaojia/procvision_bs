package com.imustsz.craft.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.imustsz.common.annotation.Log;
import com.imustsz.common.core.controller.BaseController;
import com.imustsz.common.core.domain.AjaxResult;
import com.imustsz.common.core.page.TableDataInfo;
import com.imustsz.common.enums.BusinessType;
import com.imustsz.common.utils.poi.ExcelUtil;
import com.imustsz.craft.domain.Craft;
import com.imustsz.craft.domain.json.ProductProcess;
import com.imustsz.craft.service.ICraftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 工艺信息Controller
 * 
 * @author imustsz
 * @date 2025-12-18
 */
@RestController
@RequestMapping("/craft/info")
public class CraftController extends BaseController
{
    @Autowired
    private ICraftService craftService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 查询工艺信息列表
     */
    @PreAuthorize("@ss.hasPermi('craft:craft:list')")
    @GetMapping("/list")
    public TableDataInfo list(Craft craft)
    {
        startPage();
        List<Craft> list = craftService.selectCraftList(craft);
        return getDataTable(list);
    }

    /**
     * 导出工艺信息列表
     */
    @PreAuthorize("@ss.hasPermi('craft:craft:export')")
    @Log(title = "工艺信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Craft craft)
    {
        List<Craft> list = craftService.selectCraftList(craft);
        ExcelUtil<Craft> util = new ExcelUtil<Craft>(Craft.class);
        util.exportExcel(response, list, "工艺信息数据");
    }

    /**
     * 获取工艺信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('craft:craft:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(craftService.selectCraftById(id));
    }

    /**
     * 新增工艺信息
     */
    @PreAuthorize("@ss.hasPermi('craft:craft:add')")
    @Log(title = "工艺信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Craft craft)
    {
        return toAjax(craftService.insertCraft(craft));
    }

    /**
     * 修改工艺信息
     */
    @PreAuthorize("@ss.hasPermi('craft:craft:edit')")
    @Log(title = "工艺信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Craft craft)
    {
        return toAjax(craftService.updateCraft(craft));
    }

    /**
     * 删除工艺信息
     */
    @PreAuthorize("@ss.hasPermi('craft:craft:remove')")
    @Log(title = "工艺信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(craftService.deleteCraftByIds(ids));
    }


    /**
     * 从MMO获取工艺信息
     */
    @GetMapping("/getCraftFromMMO")
    public AjaxResult getCraftFromMMO() throws IOException {
        File file = new File("MOM/测试数据.json");
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        ProductProcess productProcess =objectMapper.readValue(file, ProductProcess.class);
        craftService.importCraftFromMMo(productProcess);
        return success(productProcess);
    }
}
