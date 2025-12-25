package com.imustsz.algorithm.controller;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;
import javax.servlet.http.HttpServletResponse;

import com.imustsz.common.utils.DateUtils;
import com.imustsz.common.utils.bean.MinioUtils;
import io.minio.errors.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.imustsz.common.annotation.Log;
import com.imustsz.common.core.controller.BaseController;
import com.imustsz.common.core.domain.AjaxResult;
import com.imustsz.common.enums.BusinessType;
import com.imustsz.algorithm.domain.BizAlgorithm;
import com.imustsz.algorithm.service.IBizAlgorithmService;
import com.imustsz.common.utils.poi.ExcelUtil;
import com.imustsz.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 算法Controller
 *
 * @author imustsz
 * @date 2025-12-22
 */
@RestController
@RequestMapping("/algorithm")
public class BizAlgorithmController extends BaseController {
    @Autowired
    private IBizAlgorithmService bizAlgorithmService;

    @Autowired
    private MinioUtils minioUtils;

    /**
     * 查询算法列表
     */
    @PreAuthorize("@ss.hasPermi('algorithm:algorithm:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizAlgorithm bizAlgorithm) throws Exception {
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
    public void export(HttpServletResponse response, BizAlgorithm bizAlgorithm) throws Exception {
        List<BizAlgorithm> list = bizAlgorithmService.selectBizAlgorithmList(bizAlgorithm);
        ExcelUtil<BizAlgorithm> util = new ExcelUtil<BizAlgorithm>(BizAlgorithm.class);
        util.exportExcel(response, list, "算法数据");
    }

    /**
     * 获取算法详细信息
     */
    @PreAuthorize("@ss.hasPermi('algorithm:algorithm:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(bizAlgorithmService.selectBizAlgorithmById(id));
    }

    /**
     * 新增算法
     */
    @PreAuthorize("@ss.hasPermi('algorithm:algorithm:add')")
    @Log(title = "算法", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestParam("file") MultipartFile file,
                          @RequestParam("code") String code,
                          @RequestParam("name") String name,
                          @RequestParam("version") String version,
                          @RequestParam("desc") String desc) throws Exception {
        BizAlgorithm bizAlgorithm = new BizAlgorithm();
        bizAlgorithm.setCode(code);
        bizAlgorithm.setName(name);
        bizAlgorithm.setVersion(version);
        bizAlgorithm.setDesc(desc);

        String objectName = DateUtils.getDate() + UUID.randomUUID().toString();
        bizAlgorithm.setObjectName(objectName);

        minioUtils.uploadFile(objectName, file.getInputStream(), file.getSize(), file.getContentType());


        return toAjax(bizAlgorithmService.insertBizAlgorithm(bizAlgorithm));
    }

    /**
     * 修改算法
     */
    @PreAuthorize("@ss.hasPermi('algorithm:algorithm:edit')")
    @Log(title = "算法", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestParam("file") MultipartFile file,
                           @RequestParam("code") String code,
                           @RequestParam("name") String name,
                           @RequestParam("version") String version,
                           @RequestParam("desc") String desc,
                           @RequestParam("id") Long id) throws Exception {
        BizAlgorithm bizAlgorithm = bizAlgorithmService.selectBizAlgorithmById(id);
        bizAlgorithm.setCode(code);
        bizAlgorithm.setName(name);
        bizAlgorithm.setVersion(version);
        bizAlgorithm.setDesc(desc);

        if (file == null)
            return toAjax(bizAlgorithmService.updateBizAlgorithm(bizAlgorithm));
        else{
            minioUtils.deleteFile(bizAlgorithm.getObjectName());
            String objectName = DateUtils.getDate() + UUID.randomUUID().toString();
            bizAlgorithm.setObjectName(objectName);
            minioUtils.uploadFile(objectName, file.getInputStream(), file.getSize(), file.getContentType());

            return toAjax(bizAlgorithmService.updateBizAlgorithm(bizAlgorithm));
        }

    }

    /**
     * 删除算法
     */
    @PreAuthorize("@ss.hasPermi('algorithm:algorithm:remove')")
    @Log(title = "算法", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(bizAlgorithmService.deleteBizAlgorithmByIds(ids));
    }
}
