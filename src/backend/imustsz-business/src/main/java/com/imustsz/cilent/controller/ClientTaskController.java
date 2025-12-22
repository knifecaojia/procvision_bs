package com.imustsz.cilent.controller;

import com.imustsz.cilent.domain.vo.PendingTaskVO;
import com.imustsz.cilent.service.IClientTaskService;
import com.imustsz.common.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/client/task")
public class ClientTaskController {

    @Autowired
    private IClientTaskService clientTaskService;

    @GetMapping("/list")
    private AjaxResult list() {
        List<PendingTaskVO> pendingTaskList = clientTaskService.getPendingTaskList();
        return AjaxResult.success(pendingTaskList);
    }
}
