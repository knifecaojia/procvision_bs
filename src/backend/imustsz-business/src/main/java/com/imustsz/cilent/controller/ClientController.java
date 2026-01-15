package com.imustsz.cilent.controller;

import com.imustsz.algorithm.service.IBizAlgorithmService;
import com.imustsz.cilent.domain.dto.ProcessDTO;
import com.imustsz.cilent.domain.dto.ResultDTO;
import com.imustsz.cilent.domain.dto.WorkOrderProperties;
import com.imustsz.cilent.domain.vo.AlgorithmVO;
import com.imustsz.cilent.domain.vo.WorkOrderVO;
import com.imustsz.cilent.service.IClientTaskService;
import com.imustsz.common.core.controller.BaseController;
import com.imustsz.common.core.domain.AjaxResult;
import com.imustsz.common.core.page.TableDataInfo;
import com.imustsz.common.utils.DateUtils;
import com.imustsz.common.utils.bean.MinioUtils;
import com.imustsz.order.service.IBizWorkOrderService;
import com.imustsz.process.service.IBizProcessRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Api("客户端接口")
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
    @ApiOperation("获取装配任务列表")
    private TableDataInfo workOrderList(WorkOrderProperties workOrderProperties) {
        startPage();
        List<WorkOrderVO> workOrderVOList = bizWorkOrderService.getWorkOrderVOList(workOrderProperties);
        return getDataTable(workOrderVOList);
    }

    @GetMapping("/algorithm/list")
    @ApiOperation("获取算法列表")
    private TableDataInfo algorithmList() throws Exception {
        startPage();
        List<AlgorithmVO> algorithmVOList = bizAlgorithmService.getAlgorithmVOList();
        return getDataTable(algorithmVOList);
    }

    @GetMapping("/task/status/{taskNo}/{statusCode}")
    @ApiOperation("修改任务状态")
    private AjaxResult changeWorkOrderStatus(@PathVariable String taskNo,@PathVariable String statusCode) {
        return toAjax(bizWorkOrderService.changeWorkOrderStatusByCode(taskNo, statusCode));
    }

    @PostMapping("/process")
    @ApiOperation("步骤上传")
    private AjaxResult upLoadProcess(@RequestBody ProcessDTO processDTO) {
        return toAjax(bizProcessRecordService.insertBizProcessRecordByUpload(processDTO));
    }

    @PostMapping("/result")
    @ApiOperation("结果上传")
    private AjaxResult upLoadResult(@RequestBody ResultDTO resultDTO) {
        return toAjax(bizWorkOrderService.updateBizWorkOrderResultByUpload(resultDTO));
    }

    @GetMapping("/getUrl")
    @ApiOperation("获取上传URL")
    public AjaxResult getUrl() throws Exception {
        String objectName = DateUtils.getDate() + UUID.randomUUID().toString();
        Map<String, Object> map = new HashMap<>();
        map.put("url", minioUtils.generatePresignedUploadUrl(objectName));
        map.put("objectName", objectName);
        return success(map);
    }
}
