package com.imustsz.address.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import io.minio.errors.*;
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
import com.imustsz.address.domain.Address;
import com.imustsz.address.service.IAddressService;
import com.imustsz.common.utils.poi.ExcelUtil;
import com.imustsz.common.core.page.TableDataInfo;

/**
 * 地址信息Controller
 * 
 * @author ruoyi
 * @date 2025-09-30
 */
@RestController
@RequestMapping("/address/address")
public class AddressController extends BaseController
{
    @Autowired
    private IAddressService addressService;

    /**
     * 查询地址信息列表
     */
    @PreAuthorize("@ss.hasPermi('address:address:list')")
    @GetMapping("/list")
    public TableDataInfo list(Address address)
    {
        startPage();
        List<Address> list = addressService.selectAddressList(address);
        return getDataTable(list);
    }

    /**
     * 导出地址信息列表
     */
    @PreAuthorize("@ss.hasPermi('address:address:export')")
    @Log(title = "地址信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Address address)
    {
        List<Address> list = addressService.selectAddressList(address);
        ExcelUtil<Address> util = new ExcelUtil<Address>(Address.class);
        util.exportExcel(response, list, "地址信息数据");
    }

    /**
     * 获取地址信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('address:address:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(addressService.selectAddressById(id));
    }

    /**
     * 新增地址信息
     */
    @PreAuthorize("@ss.hasPermi('address:address:add')")
    @Log(title = "地址信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Address address)
    {
        return toAjax(addressService.insertAddress(address));
    }

    /**
     * 修改地址信息
     */
    @PreAuthorize("@ss.hasPermi('address:address:edit')")
    @Log(title = "地址信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Address address)
    {
        return toAjax(addressService.updateAddress(address));
    }

    /**
     * 删除地址信息
     */
    @PreAuthorize("@ss.hasPermi('address:address:remove')")
    @Log(title = "地址信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(addressService.deleteAddressByIds(ids));
    }
}
