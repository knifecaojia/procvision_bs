package com.imustsz.cilent.controller;

import com.imustsz.algorithm.service.IBizAlgorithmService;
import com.imustsz.cilent.domain.dto.WorkOrderProperties;
import com.imustsz.cilent.domain.vo.AlgorithmVO;
import com.imustsz.cilent.domain.vo.PendingTaskVO;
import com.imustsz.cilent.domain.vo.WorkOrderVO;
import com.imustsz.cilent.service.IClientTaskService;
import com.imustsz.common.core.controller.BaseController;
import com.imustsz.common.core.domain.AjaxResult;
import com.imustsz.common.core.page.TableDataInfo;
import com.imustsz.order.service.IBizWorkOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/client")
public class ClientController extends BaseController {

    @Autowired
    private IClientTaskService clientTaskService;

    @Autowired
    private IBizWorkOrderService bizWorkOrderService;

    @Autowired
    private IBizAlgorithmService bizAlgorithmService;

    @GetMapping("/task/list")
    private AjaxResult pendingList() {
        List<PendingTaskVO> pendingTaskList = clientTaskService.getPendingTaskList();
        return AjaxResult.success(pendingTaskList);
    }

    @GetMapping("/workorder/list")
    private TableDataInfo workOrderList(WorkOrderProperties workOrderProperties) {
        startPage();
        List<WorkOrderVO> workOrderVOList = bizWorkOrderService.getWorkOrderVOList(workOrderProperties);
        return getDataTable(workOrderVOList);
    }

    @GetMapping("/algorithm/list")
    private TableDataInfo algorithmList() {
        startPage();
        List<AlgorithmVO> algorithmVOList = bizAlgorithmService.getAlgorithmVOList();
        return getDataTable(algorithmVOList);
    }
}
