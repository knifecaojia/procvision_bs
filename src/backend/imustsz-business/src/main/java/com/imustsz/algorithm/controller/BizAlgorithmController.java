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
    @PostMapping
    public AjaxResult add(@RequestBody BizAlgorithm bizAlgorithm) throws MinioException, NoSuchAlgorithmException, IOException, InvalidKeyException {
        String objectName = DateUtils.getDate() + UUID.randomUUID().toString();
        bizAlgorithm.setObjectName(objectName);
        bizAlgorithmService.insertBizAlgorithm(bizAlgorithm);
        return AjaxResult.success("新增成功",minioUtils.generatePresignedUploadUrl(objectName));
    }

    /**
     * 修改算法
     */
    @PreAuthorize("@ss.hasPermi('algorithm:algorithm:edit')")
    @Log(title = "算法", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(BizAlgorithm bizAlgorithm) throws Exception {
        BizAlgorithm bizAlg = bizAlgorithmService.selectBizAlgorithmById(bizAlgorithm.getId());
        String objectName = bizAlg.getObjectName();
        if (bizAlgorithm.getFileName() != null) {
            minioUtils.deleteFile(objectName);
            return AjaxResult.success("修改成功", minioUtils.generatePresignedUploadUrl(objectName));
        }
        return toAjax(bizAlgorithmService.updateBizAlgorithm(bizAlgorithm));
    }

    /**
     * 删除算法
     */
    @PreAuthorize("@ss.hasPermi('algorithm:algorithm:remove')")
    @Log(title = "算法", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) throws Exception {
        for (Long id : ids) {
            BizAlgorithm bizAlgorithm = bizAlgorithmService.selectBizAlgorithmById(id);
            minioUtils.deleteFile(bizAlgorithm.getObjectName());
        }
        return toAjax(bizAlgorithmService.deleteBizAlgorithmByIds(ids));
    }
}
