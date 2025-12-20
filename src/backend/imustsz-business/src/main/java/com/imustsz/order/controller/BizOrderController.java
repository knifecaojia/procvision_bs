package com.imustsz.order.controller;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.imustsz.common.utils.bean.MinioUtils;
import com.imustsz.order.domain.json.ProcessTaskSync;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
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
import com.imustsz.order.domain.BizOrder;
import com.imustsz.order.service.IBizOrderService;
import com.imustsz.common.utils.poi.ExcelUtil;
import com.imustsz.common.core.page.TableDataInfo;

/**
 * 订单Controller
 * 
 * @author imustsz
 * @date 2025-12-19
 */
@RestController
@RequestMapping("/order/info")
public class BizOrderController extends BaseController
{
    @Autowired
    private IBizOrderService bizOrderService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 查询订单列表
     */
    @PreAuthorize("@ss.hasPermi('order:order:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizOrder bizOrder)
    {
        startPage();
        List<BizOrder> list = bizOrderService.selectBizOrderList(bizOrder);
        return getDataTable(list);
    }

    /**
     * 导出订单列表
     */
    @PreAuthorize("@ss.hasPermi('order:order:export')")
    @Log(title = "订单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BizOrder bizOrder)
    {
        List<BizOrder> list = bizOrderService.selectBizOrderList(bizOrder);
        ExcelUtil<BizOrder> util = new ExcelUtil<BizOrder>(BizOrder.class);
        util.exportExcel(response, list, "订单数据");
    }

    /**
     * 获取订单详细信息
     */
    @PreAuthorize("@ss.hasPermi('order:order:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(bizOrderService.selectBizOrderById(id));
    }

    /**
     * 新增订单
     */
    @PreAuthorize("@ss.hasPermi('order:order:add')")
    @Log(title = "订单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BizOrder bizOrder)
    {
        return toAjax(bizOrderService.insertBizOrder(bizOrder));
    }

    /**
     * 修改订单
     */
    @PreAuthorize("@ss.hasPermi('order:order:edit')")
    @Log(title = "订单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BizOrder bizOrder)
    {
        return toAjax(bizOrderService.updateBizOrder(bizOrder));
    }

    /**
     * 删除订单
     */
    @PreAuthorize("@ss.hasPermi('order:order:remove')")
    @Log(title = "订单", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(bizOrderService.deleteBizOrderByIds(ids));
    }

    /**
     * 从MMO获取订单信息
     */
    @GetMapping("/getOrderFromMMO")
    public AjaxResult getOrderFromMMO() throws IOException {
        File file = new File("MOM/工序任务同步.json");
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        ProcessTaskSync processTaskSync = objectMapper.readValue(file, ProcessTaskSync.class);
        bizOrderService.importOrderFromMMo(processTaskSync);
        return success();
    }
}
