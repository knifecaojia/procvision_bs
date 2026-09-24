package com.imustsz.craft.controller;

import com.imustsz.common.core.controller.BaseController;
import com.imustsz.common.core.domain.AjaxResult;
import com.imustsz.common.annotation.Log;
import com.imustsz.common.enums.BusinessType;
import com.imustsz.craft.domain.dto.LabelTrialRequest;
import com.imustsz.craft.service.impl.LabelTrialService;
import org.springframework.web.bind.annotation.*;

/**
 * Trial-only routes; existing step controller and deletion behavior are untouched.
 */
@RestController
@RequestMapping("/craft/label-trial")
public class LabelTrialController extends BaseController {
    private final LabelTrialService service;

    public LabelTrialController(LabelTrialService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public AjaxResult info(@PathVariable Long id) throws Exception {
        return success(service.info(id));
    }

    @PostMapping("/{id}/upload")
    public AjaxResult upload(@PathVariable Long id) throws Exception {
        return success(service.upload(id));
    }

    @Log(title = "工步标注试验版", businessType = BusinessType.UPDATE)
    @PostMapping("/save")
    public AjaxResult save(@RequestBody LabelTrialRequest request) throws Exception {
        return success(service.save(request));
    }
}
