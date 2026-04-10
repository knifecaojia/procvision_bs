package com.imustsz.order.service.impl;

import java.io.InputStream;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.imustsz.cilent.domain.dto.ResultDTO;
import com.imustsz.cilent.domain.dto.TaskSelectDTO;
import com.imustsz.cilent.domain.dto.WorkOrderProperties;
import com.imustsz.cilent.domain.vo.StepVO;
import com.imustsz.cilent.domain.vo.WorkOrderVO;
import com.imustsz.common.utils.bean.MinioUtils;
import com.imustsz.common.utils.sign.Base64;
import com.imustsz.craft.domain.BizStep;
import com.imustsz.craft.domain.Craft;
import com.imustsz.craft.domain.Process;
import com.imustsz.craft.mapper.BizStepMapper;
import com.imustsz.craft.mapper.CraftMapper;
import com.imustsz.craft.mapper.ProcessMapper;
import com.imustsz.framework.aspectj.AutoFill;
import com.imustsz.order.domain.dto.FinishedOrderDTO;
import com.imustsz.order.domain.json.*;
import com.imustsz.order.domain.vo.PageVO;
import com.imustsz.process.domain.BizProcessRecord;
import com.imustsz.process.mapper.BizProcessRecordMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.imustsz.order.mapper.BizWorkOrderMapper;
import com.imustsz.order.domain.BizWorkOrder;
import com.imustsz.order.service.IBizWorkOrderService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 工单Service业务层处理
 * 
 * @author imustsz
 * @date 2025-12-22
 */
@Service
public class BizWorkOrderServiceImpl implements IBizWorkOrderService
{
    @Autowired
    private BizWorkOrderMapper bizWorkOrderMapper;

    @Autowired
    private BizStepMapper bizStepMapper;

    @Autowired
    private ProcessMapper processMapper;

    @Autowired
    private CraftMapper craftMapper;

    @Autowired
    private MinioUtils minioUtils;

    @Autowired
    private BizProcessRecordMapper bizProcessRecordMapper;

    @Value("${minio.bucketName}")
    private String bucketName;

    @Value("${webservice.url}")
    private String webserviceUrl;

    private final Logger log =  LoggerFactory.getLogger(BizWorkOrderServiceImpl.class.getName());

    /**
     * 查询工单
     * 
     * @param id 工单主键
     * @return 工单
     */
    @Override
    public BizWorkOrder selectBizWorkOrderById(Long id)
    {
        return bizWorkOrderMapper.selectBizWorkOrderById(id);
    }

    /**
     * 查询工单列表
     * 
     * @param bizWorkOrder 工单
     * @return 工单
     */
    @Override
    public List<BizWorkOrder> selectBizWorkOrderList(BizWorkOrder bizWorkOrder) throws Exception {
        List<BizWorkOrder> bizWorkOrders = bizWorkOrderMapper.selectBizWorkOrderList(bizWorkOrder);
        for (BizWorkOrder order : bizWorkOrders){
            if (order.getGuideMapUrl() != null)
                order.setGuideMapUrl(minioUtils.getPresignedUrl(order.getGuideMapUrl()));
        }
        return bizWorkOrders;
    }

    /**
     * 新增工单
     * 
     * @param bizWorkOrder 工单
     * @return 结果
     */
    @Override
    @AutoFill("insert")
    public int insertBizWorkOrder(BizWorkOrder bizWorkOrder)
    {
        Craft craft = craftMapper.selectCraftByCodeAndVersion(bizWorkOrder.getCraftCode(), bizWorkOrder.getCraftVersion());
        if (craft == null)
            return -1;
        Process process = processMapper.selectProcessByCodeAndNameAndCraftId(bizWorkOrder.getProcessCode(), bizWorkOrder.getProcessName(), craft.getId());
        if (process == null)
            return -2;
        if(craft.getStatus() == 1 || craft.getStatus() == 2)
            bizWorkOrder.setStatus(-2);
        else if (craft.getStatus() == 3)
            bizWorkOrder.setStatus(-1);
        else
            bizWorkOrder.setStatus(1);

        return bizWorkOrderMapper.insertBizWorkOrder(bizWorkOrder);
    }

    /**
     * 修改工单
     * 
     * @param bizWorkOrder 工单
     * @return 结果
     */
    @Override
    @AutoFill("update")
    public int updateBizWorkOrder(BizWorkOrder bizWorkOrder)
    {
        return bizWorkOrderMapper.updateBizWorkOrder(bizWorkOrder);
    }

    /**
     * 批量删除工单
     * 
     * @param ids 需要删除的工单主键
     * @return 结果
     */
    @Override
    public int deleteBizWorkOrderByIds(Long[] ids)
    {
        for (Long id : ids){
            BizWorkOrder bizWorkOrder = bizWorkOrderMapper.selectBizWorkOrderById(id);
            bizProcessRecordMapper.deleteBizProcessRecordByTaskNo(bizWorkOrder.getWorkOrderCode());
        }
        return bizWorkOrderMapper.deleteBizWorkOrderByIds(ids);
    }

    /**
     * 删除工单信息
     * 
     * @param id 工单主键
     * @return 结果
     */
    @Override
    public int deleteBizWorkOrderById(Long id)
    {
        return bizWorkOrderMapper.deleteBizWorkOrderById(id);
    }

    /**
     * 从MMO获取订单信息
     */
    @Override
    @Transactional
    @AutoFill("insert")
    public int importOrderFromMMo(WorkOrderTaskData workOrderTaskData) {
        int flag = 0;
        String productionOrderNo = workOrderTaskData.getProductionOrderNo();
        List<WorkOrder> taskSync = workOrderTaskData.getWorkOrderList();
        for (WorkOrder order : taskSync) {
            List<DispatchTask> dispatchTaskInfo = order.getDispatchTaskInfo();
            Craft craft = craftMapper.selectCraftByProductionOrderNo(productionOrderNo);
            if (craft == null)
                throw new RuntimeException("工艺信息有误，请核对工艺信息");

            for (DispatchTask task : dispatchTaskInfo) {

                BizWorkOrder bizWorkOrder = new BizWorkOrder();
                bizWorkOrder.setWorkOrderCode(order.getWorkOrderNo());
                bizWorkOrder.setProdOrderNo(productionOrderNo);
                bizWorkOrder.setCraftCode(craft.getCode());
                bizWorkOrder.setCraftVersion(craft.getVersion());

                BizWorkOrder existFlag = bizWorkOrderMapper.checkWorkOrderExist(order.getWorkOrderNo());
                if (existFlag != null)
                    throw new RuntimeException(String.format("工单：%s已存在", order.getWorkOrderNo()));

                if(craft.getStatus() == 1 || craft.getStatus() == 2)
                    bizWorkOrder.setStatus(-2);
                else if (craft.getStatus() == 3)
                    bizWorkOrder.setStatus(-1);
                else
                    bizWorkOrder.setStatus(1);

                Process process = processMapper.selectProcessByCodeAndNameAndCraftId(task.getOperationNo(), task.getOperationName(), craft.getId());
                if (process == null)
                    throw new RuntimeException("工序信息有误，请核对工序信息");

                bizWorkOrder.setProcessCode(task.getOperationNo());
                bizWorkOrder.setProcessName(task.getOperationName());

                Date date1 = Date.from(task.getPlannedStartTime().atZone(ZoneId.systemDefault()).toInstant());
                Date date2 = Date.from(task.getPlannedEndTime().atZone(ZoneId.systemDefault()).toInstant());
                bizWorkOrder.setStartTime(date1);
                bizWorkOrder.setEndTime(date2);

                bizWorkOrder.setWorkerCode(task.getWorkerCode());
                bizWorkOrder.setWorkerName(task.getWorkerName());

                flag += bizWorkOrderMapper.insertBizWorkOrder(bizWorkOrder);
            }

        }

        return flag;

    }
    public PageVO workOrderVOList(WorkOrderProperties workOrderProperties) {
        BizWorkOrder bizWorkOrder = new BizWorkOrder();
        bizWorkOrder.setStatus(workOrderProperties.getStatus());
        PageHelper.startPage(workOrderProperties.getPageNum(), workOrderProperties.getPageSize());
        List<BizWorkOrder> bizWorkOrders = bizWorkOrderMapper.selectBizWorkOrderList(bizWorkOrder);
        PageInfo<BizWorkOrder> pageInfo = new PageInfo<>(bizWorkOrders);

        List<WorkOrderVO> collect = pageInfo.getList().stream().map(workOrder -> {
            WorkOrderVO workOrderVO = new WorkOrderVO();

            workOrderVO.setTask_no(workOrder.getWorkOrderCode());
            workOrderVO.setCraft_no(workOrder.getCraftCode());
            workOrderVO.setCraft_version(workOrder.getCraftVersion());
            Craft craft = craftMapper.selectCraftByCodeAndVersion(workOrder.getCraftCode(), workOrder.getCraftVersion());
            workOrderVO.setCraft_name(craft.getName());
            workOrderVO.setProcess_code(workOrder.getProcessCode());
            workOrderVO.setProcess_name(workOrder.getProcessName());
            workOrderVO.setStart_time(workOrder.getStartTime());
            workOrderVO.setEnd_time(workOrder.getEndTime());
            workOrderVO.setWorker_code(workOrder.getWorkerCode());
            workOrderVO.setWorker_name(workOrder.getWorkerName());
            workOrderVO.setProd_order_no(workOrder.getProdOrderNo());

            Process process = processMapper.selectProcessIdByCodeAndCraftId(workOrder.getProcessCode(), craft.getId());

            List<StepVO> stepVOS = bizStepMapper.selectStepByProcessId(process.getId());
            stepVOS.forEach(stepVO -> {
                try {
                    if (stepVO.getGuide_url() != null)
                        stepVO.setGuide_url(getLabeledUrl(stepVO.getGuide_url()));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            workOrderVO.setStep_infos(stepVOS);
            workOrderVO.setAlgorithm_id(process.getAlgorithmId());

            workOrderVO.setStatus(workOrder.getStatus());

            return workOrderVO;

        }).collect(Collectors.toList());
        PageVO pageVO = new PageVO();
        pageVO.setList(collect);
        pageVO.setTotal((int) pageInfo.getTotal());
        return pageVO;
    }

    private String getLabeledUrl(String urls) throws Exception {
        String[] split = urls.substring(1, urls.length() - 1).replace("\"", "").split(",");
        return minioUtils.getPresignedUrl(split[1]);
    }

    @Override
    public PageVO selectByCondition(TaskSelectDTO taskSelectDTO) {

        PageHelper.startPage(taskSelectDTO.getPagination().getPage(), taskSelectDTO.getPagination().getPage_size());
        List<BizWorkOrder> bizWorkOrders = bizWorkOrderMapper.selectByCondition(taskSelectDTO);
        PageInfo<BizWorkOrder> pageInfo = new PageInfo<>(bizWorkOrders);

        List<WorkOrderVO> collect = pageInfo.getList().stream().map(workOrder -> {
            WorkOrderVO workOrderVO = new WorkOrderVO();

            workOrderVO.setTask_no(workOrder.getWorkOrderCode());
            workOrderVO.setCraft_no(workOrder.getCraftCode());
            workOrderVO.setCraft_version(workOrder.getCraftVersion());
            Craft craft = craftMapper.selectCraftByCodeAndVersion(workOrder.getCraftCode(), workOrder.getCraftVersion());
            workOrderVO.setCraft_name(craft.getName());
            workOrderVO.setProcess_code(workOrder.getProcessCode());
            workOrderVO.setProcess_name(workOrder.getProcessName());
            workOrderVO.setStart_time(workOrder.getStartTime());
            workOrderVO.setEnd_time(workOrder.getEndTime());
            workOrderVO.setWorker_code(workOrder.getWorkerCode());
            workOrderVO.setWorker_name(workOrder.getWorkerName());
            workOrderVO.setProd_order_no(workOrder.getProdOrderNo());

            Process process = processMapper.selectProcessIdByCodeAndCraftId(workOrder.getProcessCode(), craft.getId());

            List<StepVO> stepVOS = bizStepMapper.selectStepByProcessId(process.getId());
            stepVOS.forEach(stepVO -> {
                try {
                    if (stepVO.getGuide_url() != null)
                        stepVO.setGuide_url(getLabeledUrl(stepVO.getGuide_url()));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            workOrderVO.setStep_infos(stepVOS);
            workOrderVO.setAlgorithm_id(process.getAlgorithmId());

            workOrderVO.setStatus(workOrder.getStatus());

            return workOrderVO;

        }).collect(Collectors.toList());
        PageVO pageVO = new PageVO();
        pageVO.setList(collect);
        pageVO.setTotal((int) pageInfo.getTotal());
        return pageVO;
    }

    // TODO 照片回传
    @Override
    public int uploadToMMO(FinishedOrderDTO finishedOrderDTO) {
        BizWorkOrder workOrder = bizWorkOrderMapper.selectBizWorkOrderByCodeAndProcessCode(finishedOrderDTO.getWorkOrderCode(), finishedOrderDTO.getProcessCode());
        BizProcessRecord record = bizProcessRecordMapper.selectBizProcessRecordByOrderAndProcessCodeAndStepNo(workOrder.getWorkOrderCode(), workOrder.getProcessCode(), finishedOrderDTO.getStepNo());

        //构建内层JSON
        JSONObject innerJson = new JSONObject();

        JSONObject batchInfo = new JSONObject();
        batchInfo.put("work_order_no", finishedOrderDTO.getWorkOrderCode());
        batchInfo.put("operation_no", finishedOrderDTO.getProcessCode());
        batchInfo.put("total_count", 1);
        batchInfo.put("upload_time", DateUtil.now().replace(" ", "T"));
        batchInfo.put("worker_name", workOrder.getWorkerName());
        batchInfo.put("worker_code", workOrder.getWorkerCode());
        batchInfo.put("system_Id", "VGS");

        innerJson.put("batch_info", batchInfo);

        JSONArray imageList = new JSONArray();
        for (int i = 0; i < 1; i++) {
            String base64Image = "";

            try (InputStream stream = minioUtils.getFileInputStream(bucketName, record.getImagePath())){
                base64Image = Base64.encode(IoUtil.readBytes(stream));
            } catch (Exception e) {
                log.error("从 MinIO 读取图片失败", e);
                throw new RuntimeException(e);
            }

            String fileName = finishedOrderDTO.getWorkOrderCode() + "_" + finishedOrderDTO.getProcessCode() + "_" + finishedOrderDTO.getStepNo() + String.format("%03d", i+1) + ".png";

            JSONObject imageObj = new JSONObject();

            imageObj.put("image_no", fileName);
            imageObj.put("image_base64", base64Image);
            imageObj.put("image_format", "png");
            imageObj.put("image_desc", "工序:" + finishedOrderDTO.getProcessCode() + "-" + finishedOrderDTO.getProcessName() + "步骤:" + finishedOrderDTO.getStepNo() + "-" + finishedOrderDTO.getStepName());

            imageList.add(imageObj);
        }
        innerJson.put("work_order_image_list", imageList);

        String innerJsonString = innerJson.toJSONString();

        // 构建外层 JSON 并塞入内层 JSON 字符串
        JSONObject rootJson = new JSONObject();
        rootJson.put("oriSysName", "视觉引导系统");
        rootJson.put("oriSysNum", "VGS");
        rootJson.put("uniqueFlag", "1000001");
        rootJson.put("timestamp", String.valueOf(System.currentTimeMillis()));

        JSONArray outerFileDataArray = new JSONArray();
        JSONObject outerFileDataObj = new JSONObject();
        outerFileDataObj.put("fileName", "records.json");
        outerFileDataObj.put("fileData", innerJsonString);

        outerFileDataArray.add(outerFileDataObj);
        rootJson.put("fileData", outerFileDataArray);

        String finalJsonString = rootJson.toJSONString();
        log.info("构建完成的外层 JSON: {}", finalJsonString);

        //构建 SOAP XML 并发送
        String soapXml = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.example.com/\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <web:yourMethodName>\n" +
                "         <data><![CDATA[" + finalJsonString + "]]></data>\n" +
                "      </web:yourMethodName>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";

        try {
            String resultXml = HttpRequest.post(webserviceUrl)
                    .header("Content-Type", "text/xml;charset=UTF-8")
                    .body(soapXml)
                    .timeout(60000) // 多张图片 Base64 会很大，超时时间放宽到 60 秒
                    .execute()
                    .body();

            log.info("WebService 响应结果: {}", resultXml);

        } catch (Exception e) {
            log.error("发送 WebService 请求失败", e);
        }

        return 1;
    }

    @Override
    @Transactional
    @AutoFill("update")
    public int changeWorkOrderStatusByCode(String workOrderCode, String statusCode) {
        return bizWorkOrderMapper.changeWorkOrderStatusByCode(workOrderCode, statusCode);
    }

    @Override
    @Transactional
    @AutoFill("update")
    public int updateBizWorkOrderResultByUpload(ResultDTO resultDTO) {
        BizWorkOrder bizWorkOrder = new BizWorkOrder();
        bizWorkOrder.setStatus(resultDTO.getResult_status());
        bizWorkOrder.setGuideMapUrl(resultDTO.getObject_name());
        bizWorkOrder.setWorkOrderCode(resultDTO.getTask_no());
        return bizWorkOrderMapper.updateBizWorkOrderByCode(bizWorkOrder);
    }

    @Override
    public StepVO getStepByWorkOrderCode(String workOrderCode, String stepCode) {
        BizWorkOrder bizWorkOrder = bizWorkOrderMapper.selectBizWorkOrderByCode(workOrderCode);
        Craft craft = craftMapper.selectCraftByCodeAndVersion(bizWorkOrder.getCraftCode(), bizWorkOrder.getCraftVersion());
        Process process = processMapper.selectProcessIdByCodeAndCraftId(bizWorkOrder.getProcessCode(), craft.getId());
        BizStep bizStep = bizStepMapper.selectBizStepByStepCodeAndProcessId(stepCode, process.getId());

        StepVO step = new StepVO();
        step.setStep_code(bizStep.getCode());
        step.setStep_name(bizStep.getName());
        step.setStep_content(bizStep.getContent());

        return step;
    }

    @Override
    public BizWorkOrder selectBizWorkOrderByCode(String workOrderCode) {
        return bizWorkOrderMapper.selectBizWorkOrderByCode(workOrderCode);
    }
}
