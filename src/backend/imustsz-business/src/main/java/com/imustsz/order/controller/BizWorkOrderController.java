package com.imustsz.order.controller;

import java.io.IOException;
import java.time.ZoneId;
import java.util.*;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.imustsz.common.utils.StringUtils;
import com.imustsz.order.domain.OrderImportTemplate;
import com.imustsz.order.domain.dto.FinishedOrderDTO;
import com.imustsz.order.domain.json.DispatchTask;
import com.imustsz.order.domain.json.WorkOrder;
import com.imustsz.order.domain.json.WorkOrderTaskData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.imustsz.common.annotation.Log;
import com.imustsz.common.core.controller.BaseController;
import com.imustsz.common.core.domain.AjaxResult;
import com.imustsz.common.enums.BusinessType;
import com.imustsz.order.domain.BizWorkOrder;
import com.imustsz.order.service.IBizWorkOrderService;
import com.imustsz.common.utils.poi.ExcelUtil;
import com.imustsz.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 工单Controller
 * 
 * @author imustsz
 * @date 2025-12-22
 */
@Api(tags = "任务管理")
@RestController
@RequestMapping("/workOrder")
public class BizWorkOrderController extends BaseController
{
    @Autowired
    private IBizWorkOrderService bizWorkOrderService;

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
    public AjaxResult add(@RequestBody BizWorkOrder bizWorkOrder) {
        int status = bizWorkOrderService.insertBizWorkOrder(bizWorkOrder);
        if (status == -1) {
            return error("未找到该工艺");
        }else if (status == -2)
            return error("未找到该工序");
        return toAjax(status);
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
    @ApiOperation("从MMO获取任务信息")
    @PostMapping("/getOrderFromMMO")
    public AjaxResult getOrderFromMMO(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty())
            return error("文件为空");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        WorkOrderTaskData taskSync = objectMapper.readValue(file.getInputStream(), WorkOrderTaskData.class);
        return toAjax(bizWorkOrderService.importOrderFromMMo(taskSync));
//        return success(taskSync);
    }

    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) {
        ExcelUtil<OrderImportTemplate> util = new ExcelUtil<>(OrderImportTemplate.class);
        util.importTemplateExcel(response, "任务数据导入模板");
    }

    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file) throws Exception {
        ExcelUtil<OrderImportTemplate> util = new ExcelUtil<>(OrderImportTemplate.class);
        List<OrderImportTemplate> list = util.importExcel(file.getInputStream());

        if (list == null || list.isEmpty()) {
            throw new RuntimeException("导入的Excel数据为空");
        }

        WorkOrderTaskData workOrderTaskData = new WorkOrderTaskData();

        OrderImportTemplate first = list.get(0);
        if (StringUtils.isBlank(first.getProductionOrderNo()))
            throw new RuntimeException("生产订单号不能为空");

        workOrderTaskData.setProductionOrderNo(first.getProductionOrderNo());

        Map<String, WorkOrder> workOrderMap = new HashMap<>();
        Map<String, Map<String, DispatchTask>> dispatchTaskMap = new HashMap<>();

        for (OrderImportTemplate template : list) {
            String workOrderNo = template.getWorkOrderNo();
            if (StringUtils.isBlank(workOrderNo))
                throw new RuntimeException("工单号不能为空");

            workOrderMap.computeIfAbsent(workOrderNo, k ->{
                WorkOrder workOrder = new WorkOrder();
                workOrder.setWorkOrderNo(workOrderNo);

                dispatchTaskMap.put(workOrderNo, new LinkedHashMap<>());
                return workOrder;
            });

            if (StringUtils.isNotBlank(template.getOperationNo())) {
                dispatchTaskMap.get(workOrderNo).computeIfAbsent(template.getOperationNo(), k -> {
                    DispatchTask dispatchTask = new DispatchTask();
                    dispatchTask.setOperationNo(template.getOperationNo());
                    dispatchTask.setOperationName(template.getOperationName());
                    dispatchTask.setDispatchQuantity(template.getDispatchQuantity());
                    dispatchTask.setPlannedStartTime(template.getPlannedStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
                    dispatchTask.setPlannedEndTime(template.getPlannedEndTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
                    dispatchTask.setProdGroup(template.getProdGroup());
                    dispatchTask.setWorkerCode(template.getWorkerCode());
                    dispatchTask.setWorkerName(template.getWorkerName());
                    return dispatchTask;
                });
            }else
                throw new RuntimeException("工序编号不能为空");

            for (String orderNo : workOrderMap.keySet()){
                WorkOrder wo = workOrderMap.get(orderNo);
                wo.setDispatchTaskInfo(new ArrayList<>(dispatchTaskMap.get(orderNo).values()));
            }

        }

        workOrderTaskData.setWorkOrderList(new ArrayList<>(workOrderMap.values()));

        bizWorkOrderService.importOrderFromMMo(workOrderTaskData);

        return success(workOrderTaskData);
    }

    @PostMapping("/uploadToMOM")
    public AjaxResult getOrderByCode(@RequestBody FinishedOrderDTO finishedOrderDTO) throws Exception {
        return toAjax(bizWorkOrderService.uploadToMOM(finishedOrderDTO));
    }
}
