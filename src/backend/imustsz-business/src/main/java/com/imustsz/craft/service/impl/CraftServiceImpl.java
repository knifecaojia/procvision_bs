package com.imustsz.craft.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.imustsz.cilent.domain.vo.StepVO;
import com.imustsz.common.utils.DateUtils;
import com.imustsz.common.utils.SecurityUtils;
import com.imustsz.craft.domain.BizStep;
import com.imustsz.craft.domain.Craft;
import com.imustsz.craft.domain.dto.SelectorInfoVO;
import com.imustsz.craft.domain.json.*;
import com.imustsz.craft.domain.Process;
import com.imustsz.craft.mapper.BizStepMapper;
import com.imustsz.craft.mapper.CraftMapper;
import com.imustsz.craft.mapper.ProcessMapper;
import com.imustsz.craft.service.ICraftService;
import com.imustsz.craft.service.IProcessService;
import com.imustsz.framework.aspectj.AutoFill;
import com.imustsz.order.domain.BizWorkOrder;
import com.imustsz.order.mapper.BizWorkOrderMapper;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 工艺信息Service业务层处理
 *
 * @author imustsz
 * @date 2025-12-18
 */
@Service
public class CraftServiceImpl implements ICraftService {
    @Autowired
    private CraftMapper craftMapper;

    @Autowired
    private ProcessMapper processMapper;

    @Autowired
    private BizStepMapper bizStepMapper;

    @Autowired
    private IProcessService processService;

    @Autowired
    private BizWorkOrderMapper bizWorkOrderMapper;

    /**
     * 查询工艺信息
     *
     * @param id 工艺信息主键
     * @return 工艺信息
     */
    @Override
    public Craft selectCraftById(Long id) {
        return craftMapper.selectCraftById(id);
    }

    /**
     * 查询工艺信息列表
     *
     * @param craft 工艺信息
     * @return 工艺信息
     */
    @Override
    public List<Craft> selectCraftList(Craft craft) {
        return craftMapper.selectCraftList(craft);
    }

    /**
     * 新增工艺信息
     *
     * @param craft 工艺信息
     * @return 结果
     */
    @Override
    @AutoFill("insert")
    public int insertCraft(Craft craft) {
        return craftMapper.insertCraft(craft);
    }

    /**
     * 修改工艺信息
     *
     * @param craft 工艺信息
     * @return 结果
     */
    @Override
    @AutoFill("update")
    public int updateCraft(Craft craft) {
        return craftMapper.updateCraft(craft);
    }

    /**
     * 批量删除工艺信息
     *
     * @param ids 需要删除的工艺信息主键
     * @return 结果
     */
    @Override
    public int deleteCraftByIds(Long[] ids) {
        for (Long id : ids) {
            processService.deleteProcessByCraftId(id);
        }
        return craftMapper.deleteCraftByIds(ids);
    }

    /**
     * 删除工艺信息信息
     *
     * @param id 工艺信息主键
     * @return 结果
     */
    @Override
    public int deleteCraftById(Long id) {
        return craftMapper.deleteCraftById(id);
    }

    /**
     * 从MMO导入工艺信息
     *
     * @param CrackProcess MMO工艺信息
     */
    @Override
    @Transactional
    @AutoFill("insert")
    public void importCraftFromMMo(OrderProcessData CrackProcess) {
        ProcessInfo crackInfo = CrackProcess.getProcessInfo();
        List<Operation> operationList = CrackProcess.getOperationList();

        Craft cf = craftMapper.selectCraftByCodeAndVersion(crackInfo.getProcessNo(), crackInfo.getProcessVersion());

        if (cf != null)
            throw new RuntimeException("该工艺已存在");

        //导入工艺基本信息
        Craft craft = new Craft();
        craft.setProductionOrderNo(crackInfo.getProductionOrderNo());
        craft.setCode(crackInfo.getProcessNo());
        craft.setName(crackInfo.getProcessName());
        craft.setVersion(crackInfo.getProcessVersion());
        craft.setDesc(crackInfo.getProcessDesc());
        craft.setStatus(1);
        craft.setCreateTime(DateUtils.getNowDate());
        craft.setCreateBy(SecurityUtils.getUsername());
        craftMapper.insertCraft(craft);

        //导入工序信息
        operationList.forEach(processMMO -> {
            OperationInfo processInfo = processMMO.getOperationInfo();
            Process process = new Process();
            process.setCode(processInfo.getOperationNo());
            process.setName(processInfo.getOperationName());
            process.setCraftId(craft.getId());
            process.setCraftCode(craft.getCode());
            process.setDesc(processInfo.getOperationDesc());
            process.setProcessMaterialInfo(JSONObject.toJSONString(processMMO.getOperationMaterialInfo()));
            process.setCreateTime(DateUtils.getNowDate());
            process.setCreateBy(SecurityUtils.getUsername());
            processMapper.insertProcess(process);

            //导入工步信息
            processMMO.getStepList().forEach(step -> {
                BizStep bizStep = new BizStep();
                bizStep.setProcessId(process.getId());
                bizStep.setCode(step.getStepNo());
                bizStep.setName(step.getStepName());
                bizStep.setContent(step.getStepContent());
                bizStep.setCreateTime(DateUtils.getNowDate());
                bizStep.setCreateBy(SecurityUtils.getUsername());
                bizStepMapper.insertBizStep(bizStep);
            });

        });

    }

    @Override
    @AutoFill("update")
    public void checkStatus(Long id) {
        boolean isNotAlg = false;
        boolean isNotGuide = false;
        List<Process> processes = processMapper.selectProcessByCraftId(id);
        for (Process process : processes) {
            if (process.getAlgorithmId() == null) {
                isNotAlg = true;
                break;
            }
        }
        for (Process process : processes) {
            List<StepVO> steps = bizStepMapper.selectStepByProcessId(process.getId());
            for (StepVO step : steps)
                if (step.getGuide_url() == null) {
                    isNotGuide = true;
                    break;
                }
            if (isNotGuide)
                break;
        }

        Craft craft = craftMapper.selectCraftById(id);
        List<BizWorkOrder> bizWorkOrders = bizWorkOrderMapper.selectWorkOrderByCraft(craft.getCode(), craft.getVersion());

        if (isNotAlg) {
            craftMapper.changeCraftStatus(id, 3);
            for (BizWorkOrder bizWorkOrder : bizWorkOrders) {
                bizWorkOrder.setStatus(-2);
                bizWorkOrderMapper.updateBizWorkOrderByCode(bizWorkOrder);
            }
        }
        else if (isNotGuide) {
            craftMapper.changeCraftStatus(id, 2);
            for (BizWorkOrder bizWorkOrder : bizWorkOrders) {
                bizWorkOrder.setStatus(-1);
                bizWorkOrderMapper.updateBizWorkOrderByCode(bizWorkOrder);
            }
        }
        else {
            craftMapper.changeCraftStatus(id, 4);
            for (BizWorkOrder bizWorkOrder : bizWorkOrders) {
                bizWorkOrder.setStatus(1);
                bizWorkOrderMapper.updateBizWorkOrderByCode(bizWorkOrder);
            }
        }
    }

    @Override
    public List<String> getCraftSelector() {
        return craftMapper.getCodeList();
    }

    @Override
    public List<SelectorInfoVO> getSelectorOptions() {

        return List.of();
    }

    @Override
    @Transactional
    @AutoFill("insert")
    public int insertCraftByHand(Craft craft) {
        Date now = new Date();
        String sb = "100" + DateFormatUtils.format(now, "MMdd") +
                DateUtils.dateTimeNow().substring(DateUtils.dateTimeNow().length() - 2) + "C";
        craft.setProductionOrderNo(sb);
        return craftMapper.insertCraft(craft);
    }
}
