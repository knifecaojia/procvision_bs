package com.imustsz.craft.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.imustsz.common.annotation.Log;
import com.imustsz.common.core.controller.BaseController;
import com.imustsz.common.core.domain.AjaxResult;
import com.imustsz.common.core.page.TableDataInfo;
import com.imustsz.common.enums.BusinessType;
import com.imustsz.common.utils.StringUtils;
import com.imustsz.common.utils.poi.ExcelUtil;
import com.imustsz.craft.domain.Craft;
import com.imustsz.craft.domain.ProcessImportTemplate;
import com.imustsz.craft.domain.json.*;
import com.imustsz.craft.service.ICraftService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * 工艺信息Controller
 * 
 * @author imustsz
 * @date 2025-12-18
 */
@Api(tags = "工艺信息")
@RestController
@RequestMapping("/craft/info")
public class CraftController extends BaseController
{
    @Autowired
    private ICraftService craftService;


    /**
     * 查询工艺信息列表
     */
//    @PreAuthorize("@ss.hasPermi('craft:craft:list')")
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
//    @PreAuthorize("@ss.hasPermi('craft:craft:export')")
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
//    @PreAuthorize("@ss.hasPermi('craft:craft:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(craftService.selectCraftById(id));
    }

    /**
     * 新增工艺信息
     */
//    @PreAuthorize("@ss.hasPermi('craft:craft:add')")
    @Log(title = "工艺信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Craft craft)
    {
        return toAjax(craftService.insertCraft(craft));
    }

//    @PreAuthorize("@ss.hasPermi('craft:craft:add')")
    @Log(title = "工艺信息", businessType = BusinessType.INSERT)
    @PostMapping("/hand")
    public AjaxResult addByHand(@RequestBody Craft craft)
    {
        return toAjax(craftService.insertCraftByHand(craft));
    }

    /**
     * 修改工艺信息
     */
//    @PreAuthorize("@ss.hasPermi('craft:craft:edit')")
    @Log(title = "工艺信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Craft craft)
    {
        return toAjax(craftService.updateCraft(craft));
    }

    /**
     * 删除工艺信息
     */
//    @PreAuthorize("@ss.hasPermi('craft:craft:remove')")
    @Log(title = "工艺信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(craftService.deleteCraftByIds(ids));
    }


    /**
     * 下载工艺导入模板
     */
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) {
        ExcelUtil<ProcessImportTemplate> util = new ExcelUtil<>(ProcessImportTemplate.class);
        util.importTemplateExcel(response, "工艺数据导入模板");
    }

    /**
     * 从MMO获取工艺信息
     */
    @ApiOperation("从MMO获取工艺信息")
    @PostMapping("/getCraftFromMMO")
    public AjaxResult getCraftFromMMO(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty())
            return error("文件为空");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        OrderProcessData CrackProcess =objectMapper.readValue(file.getInputStream(), OrderProcessData.class);
        craftService.importCraftFromMOM(CrackProcess);
        return success();
    }

    @GetMapping("/checkStatus/{id}")
    public AjaxResult changeCraftStatus(@PathVariable Long id) {
        craftService.checkStatus(id);
        return success();
    }

    @GetMapping("/options")
    public AjaxResult getOptions() {
        return success(craftService.getSelectorOptions());
    }

    @GetMapping("/craftSelector")
    public AjaxResult getSelectorInfo() {
        return success(craftService.getCraftSelector());
    }

    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file) throws Exception {
        ExcelUtil<ProcessImportTemplate> util = new ExcelUtil<>(ProcessImportTemplate.class);
        List<ProcessImportTemplate> flatList = util.importExcel(file.getInputStream());

        if (flatList == null || flatList.isEmpty()) {
            throw new RuntimeException("导入的Excel数据为空");
        }

        OrderProcessData orderProcessData = new OrderProcessData();

        ProcessImportTemplate firstRow = flatList.get(0);
        ProcessInfo processInfo = new ProcessInfo();

        if (firstRow.getProcessNo() == null)
            throw new RuntimeException("工艺编号不能为空");

        processInfo.setProductionOrderNo(firstRow.getProductionOrderNo());
        processInfo.setProcessNo(firstRow.getProcessNo());
        processInfo.setProcessVersion(firstRow.getProcessVersion());
        processInfo.setProcessName(firstRow.getProcessName());
        processInfo.setProcessDesc(firstRow.getProcessDesc());
        orderProcessData.setProcessInfo(processInfo);

        Map<String, Operation> operationMap = new LinkedHashMap<>();
        // 记录每个工序下的去重 Map：防错处理 (Key是工序号，Value是一个由 stepNo 或 materialNo 组成的去重Map)
        Map<String, Map<String, Step>> stepGroupMap = new HashMap<>();
        Map<String, Map<String, MaterialInfo>> materialGroupMap = new HashMap<>();

        for (ProcessImportTemplate row : flatList) {
            String opNo = row.getOperationNo();
            if (StringUtils.isBlank(opNo)) {
                throw new RuntimeException("工序号不能为空");
            }

            operationMap.computeIfAbsent(opNo, k -> {
                Operation dto = new Operation();
                OperationInfo opInfo = new OperationInfo();
                opInfo.setOperationNo(row.getOperationNo());
                opInfo.setOperationName(row.getOperationName());
                opInfo.setOperationDesc(row.getOperationDesc());
                dto.setOperationInfo(opInfo);

                stepGroupMap.put(opNo, new LinkedHashMap<>());
                materialGroupMap.put(opNo, new LinkedHashMap<>());
                return dto;
            });

            if (StringUtils.isNotBlank(row.getStepNo())) {
                stepGroupMap.get(opNo).computeIfAbsent(row.getStepNo(), k -> {
                    Step step = new Step();
                    step.setStepNo(row.getStepNo());
                    step.setStepName(row.getStepName());
                    step.setStepContent(row.getStepContent());
                    return step;
                });
            }else
                throw new RuntimeException("工步序不能为空");

            if (StringUtils.isNotBlank(row.getMaterialNo())) {
                materialGroupMap.get(opNo).computeIfAbsent(row.getMaterialNo(), k -> {
                    MaterialInfo material = new MaterialInfo();
                    material.setMaterialNo(row.getMaterialNo());
                    material.setMaterialName(row.getMaterialName());
                    material.setMaterialQuantity(row.getMaterialQuantity());
                    material.setMaterialUnit(row.getMaterialUnit());
                    material.setErrorPreventionMark(row.getErrorPreventionMark());
                    return material;
                });
            }
        }

        for (String opNo : operationMap.keySet()) {
            Operation opDTO = operationMap.get(opNo);

            List<Step> stepList = new ArrayList<>(stepGroupMap.get(opNo).values());
            opDTO.setStepList(stepList);

            List<MaterialInfo> materialList = new ArrayList<>(materialGroupMap.get(opNo).values());
            opDTO.setOperationMaterialInfo(materialList);
        }

        orderProcessData.setOperationList(new ArrayList<>(operationMap.values()));

        craftService.importCraftFromMOM(orderProcessData);

        return success(orderProcessData);
    }
}
