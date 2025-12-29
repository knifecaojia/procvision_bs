package com.imustsz.cilent.controller;

import com.imustsz.algorithm.service.IBizAlgorithmService;
import com.imustsz.cilent.domain.dto.ProcessDTO;
import com.imustsz.cilent.domain.dto.ResultDTO;
import com.imustsz.cilent.domain.dto.WorkOrderProperties;
import com.imustsz.cilent.domain.vo.AlgorithmVO;
import com.imustsz.cilent.domain.vo.PendingTaskVO;
import com.imustsz.cilent.domain.vo.StepVO;
import com.imustsz.cilent.domain.vo.WorkOrderVO;
import com.imustsz.cilent.service.IClientTaskService;
import com.imustsz.common.core.controller.BaseController;
import com.imustsz.common.core.domain.AjaxResult;
import com.imustsz.common.core.page.TableDataInfo;
import com.imustsz.common.utils.DateUtils;
import com.imustsz.common.utils.bean.MinioUtils;
import com.imustsz.craft.domain.BizStep;
import com.imustsz.order.domain.BizWorkOrder;
import com.imustsz.order.service.IBizWorkOrderService;
import com.imustsz.process.service.IBizProcessRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/client")
public class ClientController extends BaseController {

    @Autowired
    private IClientTaskService clientTaskService;

    @Autowired
    private IBizWorkOrderService bizWorkOrderService;

    @Autowired
    private IBizAlgorithmService bizAlgorithmService;

    @Autowired
    private IBizProcessRecordService bizProcessRecordService;

    @Autowired
    private MinioUtils minioUtils;

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
    private TableDataInfo algorithmList() throws Exception {
        startPage();
        List<AlgorithmVO> algorithmVOList = bizAlgorithmService.getAlgorithmVOList();
        return getDataTable(algorithmVOList);
    }

    @GetMapping("/workorder/status/{workOrderCode}/{statusCode}")
    private AjaxResult changeWorkOrderStatus(@PathVariable String workOrderCode,@PathVariable String statusCode) {
        return toAjax(bizWorkOrderService.changeWorkOrderStatusByCode(workOrderCode, statusCode));
    }

    @PostMapping("/process")
    private AjaxResult upLoadProcess(@RequestBody ProcessDTO processDTO) {
        return toAjax(bizProcessRecordService.insertBizProcessRecordByUpload(processDTO));
    }

    @PostMapping("/result")
    private AjaxResult upLoadResult(@RequestBody ResultDTO resultDTO) {
        return toAjax(bizWorkOrderService.updateBizWorkOrderResultByUpload(resultDTO));
    }

    @GetMapping("/getUrl")
    public AjaxResult getUrl(String workOrderCode, String stepCode) throws Exception {
        String objectName = DateUtils.getDate() + UUID.randomUUID().toString();
        Map<String, Object> map = new HashMap<>();
        map.put("url", minioUtils.generatePresignedUploadUrl(objectName));
        map.put("objectName", objectName);
        if (workOrderCode == null && stepCode == null){
            return success(map);
        }else if(workOrderCode != null && stepCode != null){
            StepVO step = bizWorkOrderService.getStepByWorkOrderCode(workOrderCode, stepCode);
            map.put("stepInfo", step);
        } else if (workOrderCode != null) {
            BizWorkOrder bizWorkOrder = bizWorkOrderService.selectBizWorkOrderByCode(workOrderCode);
            map.put("workOrderInfo", bizWorkOrder);
        }
        return success(map);
    }
}
