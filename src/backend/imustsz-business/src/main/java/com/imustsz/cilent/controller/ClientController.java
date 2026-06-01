package com.imustsz.cilent.controller;

import com.imustsz.algorithm.service.IBizAlgorithmService;
import com.imustsz.cilent.domain.dto.*;
import com.imustsz.cilent.domain.vo.AlgorithmVO;
import com.imustsz.cilent.domain.vo.ProcessRecordVO;
import com.imustsz.cilent.domain.vo.TaskConditionVO;
import com.imustsz.cilent.domain.vo.WorkOrderVO;
import com.imustsz.cilent.service.IClientTaskService;
import com.imustsz.common.annotation.Log;
import com.imustsz.common.annotation.RateLimiter;
import com.imustsz.common.constant.HttpStatus;
import com.imustsz.common.core.controller.BaseController;
import com.imustsz.common.core.domain.AjaxResult;
import com.imustsz.common.core.page.TableDataInfo;
import com.imustsz.common.enums.BusinessType;
import com.imustsz.common.enums.LimitType;
import com.imustsz.common.utils.DateUtils;
import com.imustsz.common.utils.bean.MinioUtils;
import com.imustsz.order.domain.vo.PageVO;
import com.imustsz.order.service.IBizWorkOrderService;
import com.imustsz.process.service.IBizProcessRecordService;
import io.minio.errors.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

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
    public TableDataInfo workOrderList(WorkOrderProperties workOrderProperties) {
        PageVO pageVO = bizWorkOrderService.workOrderVOList(workOrderProperties);
        return getDataTable(pageVO.getList(), pageVO.getTotal());
    }

    @RateLimiter(time = 60, count = 30, limitType = LimitType.USER)
    @PostMapping("/task/condition")
    @ApiOperation("任务条件查询")
    public TaskConditionVO taskConditionSelect(@RequestBody TaskSelectDTO taskSelectDTO) {
        if (taskSelectDTO.getPagination() != null) {
            taskSelectDTO.getPagination().setPage(taskSelectDTO.getPagination().getPage() == null ? 1 : taskSelectDTO.getPagination().getPage());
            taskSelectDTO.getPagination().setPage_size(taskSelectDTO.getPagination().getPage_size() == null ? 20 : taskSelectDTO.getPagination().getPage_size());
        }else{
            taskSelectDTO.setPagination(new PaginationParams(1, 20));
        }

        if ((taskSelectDTO.getTask_no() != null && taskSelectDTO.getTask_no().length() < 4) || (taskSelectDTO.getProd_order_no() != null && taskSelectDTO.getProd_order_no().length() < 4))
            throw new RuntimeException("查询编码过短，请至少输入4位");

        if (taskSelectDTO.getTime_range() != null){
            int parmsCheckFlag = DateUtils.differentDaysByMillisecond(DateUtils.parseDate(taskSelectDTO.getTime_range().getBegin()), DateUtils.parseDate(taskSelectDTO.getTime_range().getEnd()), false);
            if (parmsCheckFlag < 0 || parmsCheckFlag > 90)
                throw new RuntimeException("时间参数有误");
        }

        PageVO pageVO = bizWorkOrderService.selectByCondition(taskSelectDTO);

        TaskConditionVO taskConditionVO = new TaskConditionVO();
        taskConditionVO.setRows(pageVO.getList());
        taskConditionVO.setTotal(pageVO.getTotal());
        taskConditionVO.setCode(HttpStatus.SUCCESS);
        taskConditionVO.setMsg("查询成功");
        taskConditionVO.setPage(taskSelectDTO.getPagination().getPage());
        taskConditionVO.setPage_size(taskSelectDTO.getPagination().getPage_size());
        return taskConditionVO;
    }

    @GetMapping("/algorithm/list")
    @ApiOperation("获取算法列表")
    public AjaxResult algorithmList() throws Exception {
        List<AlgorithmVO> algorithmVOList = bizAlgorithmService.getAlgorithmVOList();
        return success(algorithmVOList);
    }

    @GetMapping("/task/status/{taskNo}/{statusCode}")
    @Log(title = "C端修改任务状态", businessType = BusinessType.UPDATE)
    @ApiOperation("修改任务状态")
    public AjaxResult changeWorkOrderStatus(@PathVariable String taskNo,@PathVariable String statusCode) {
        return toAjax(bizWorkOrderService.changeWorkOrderStatusByCode(taskNo, statusCode));
    }

    @PostMapping("/process")
    @Log(title = "C端步骤上传", businessType = BusinessType.INSERT)
    @ApiOperation("步骤上传")
    public AjaxResult upLoadProcess(@RequestBody ProcessDTO processDTO) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return toAjax(bizProcessRecordService.insertBizProcessRecordByUpload(processDTO));
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

    @GetMapping("/getRecordList")
    @ApiOperation("获取步骤列表")
    public TableDataInfo getProcessList(RecordPageDTO recordPageDTO) {
        int pageNum = recordPageDTO.getPageNum() == null ? 1 : recordPageDTO.getPageNum();
        int pageSize = recordPageDTO.getPageSize() == null ? 10 : recordPageDTO.getPageSize();
        List<ProcessRecordVO> processRecordVOList = bizProcessRecordService.getProcessRecordList(recordPageDTO.getStatus(), null, null);
        int i1 = pageNum*pageSize < processRecordVOList.size() ? (pageNum-1)*pageSize+pageSize : processRecordVOList.size();
        List<ProcessRecordVO> list1 = new ArrayList<>();
        for (int i = (pageNum-1)*pageSize; i < i1; i++){
            list1.add(processRecordVOList.get(i));
        }
        return getDataTable(list1, processRecordVOList.size());
    }
}
