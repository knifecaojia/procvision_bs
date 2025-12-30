package com.imustsz.order.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.imustsz.order.domain.json.ProcessTaskSync;
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
import com.imustsz.order.domain.BizWorkOrder;
import com.imustsz.order.service.IBizWorkOrderService;
import com.imustsz.common.utils.poi.ExcelUtil;
import com.imustsz.common.core.page.TableDataInfo;

/**
 * 工单Controller
 * 
 * @author imustsz
 * @date 2025-12-22
 */
@RestController
@RequestMapping("/workOrder")
public class BizWorkOrderController extends BaseController
{
    @Autowired
    private IBizWorkOrderService bizWorkOrderService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 查询工单列表
     */
    @PreAuthorize("@ss.hasPermi('wo:workOrder:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizWorkOrder bizWorkOrder) throws Exception {
        startPage();
        List<BizWorkOrder> list = bizWorkOrderService.selectBizWorkOrderList(bizWorkOrder);
        return getDataTable(list);
    }

    /**
     * 导出工单列表
     */
    @PreAuthorize("@ss.hasPermi('wo:workOrder:export')")
    @Log(title = "工单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BizWorkOrder bizWorkOrder) throws Exception {
        List<BizWorkOrder> list = bizWorkOrderService.selectBizWorkOrderList(bizWorkOrder);
        ExcelUtil<BizWorkOrder> util = new ExcelUtil<BizWorkOrder>(BizWorkOrder.class);
        util.exportExcel(response, list, "工单数据");
    }

    /**
     * 获取工单详细信息
     */
    @PreAuthorize("@ss.hasPermi('wo:workOrder:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(bizWorkOrderService.selectBizWorkOrderById(id));
    }

    /**
     * 新增工单
     */
    @PreAuthorize("@ss.hasPermi('wo:workOrder:add')")
    @Log(title = "工单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BizWorkOrder bizWorkOrder)
    {
        return toAjax(bizWorkOrderService.insertBizWorkOrder(bizWorkOrder));
    }

    /**
     * 修改工单
     */
    @PreAuthorize("@ss.hasPermi('wo:workOrder:edit')")
    @Log(title = "工单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BizWorkOrder bizWorkOrder)
    {
        return toAjax(bizWorkOrderService.updateBizWorkOrder(bizWorkOrder));
    }

    /**
     * 删除工单
     */
    @PreAuthorize("@ss.hasPermi('wo:workOrder:remove')")
    @Log(title = "工单", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(bizWorkOrderService.deleteBizWorkOrderByIds(ids));
    }

    /**
     * 从MMO获取订单信息
     */
    @GetMapping("/getOrderFromMMO")
    public AjaxResult getOrderFromMMO() throws IOException {
        File file = new File("MOM/工序任务同步.json");
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        ProcessTaskSync processTaskSync = objectMapper.readValue(file, ProcessTaskSync.class);
        bizWorkOrderService.importOrderFromMMo(processTaskSync);
        return success();
    }
}
