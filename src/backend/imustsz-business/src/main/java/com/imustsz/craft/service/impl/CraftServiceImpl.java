package com.imustsz.craft.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.imustsz.cilent.domain.vo.StepVO;
import com.imustsz.common.constant.UserConstants;
import com.imustsz.common.utils.DateUtils;
import com.imustsz.common.utils.SecurityUtils;
import com.imustsz.craft.domain.BizStep;
import com.imustsz.craft.domain.Craft;
import com.imustsz.craft.domain.dto.ProcessCodeAndNameVO;
import com.imustsz.craft.domain.dto.SelectorInfoVO;
import com.imustsz.craft.domain.json.Operation;
import com.imustsz.craft.domain.json.OperationInfo;
import com.imustsz.craft.domain.json.ProcessInfo;
import com.imustsz.craft.domain.json.ProductProcess;
import com.imustsz.craft.domain.Process;
import com.imustsz.craft.mapper.BizStepMapper;
import com.imustsz.craft.mapper.CraftMapper;
import com.imustsz.craft.mapper.ProcessMapper;
import com.imustsz.craft.service.ICraftService;
import com.imustsz.craft.service.IProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
     * @param productProcess MMO工艺信息
     */
    @Override
    @Transactional
    public void importCraftFromMMo(ProductProcess productProcess) {
        ProcessInfo processInfo = productProcess.getProcessInfo();
        List<Operation> operationList = productProcess.getOperationList();
        //导入工艺基本信息
        Craft craft = new Craft();
        craft.setCode(processInfo.getProcessNo());
        craft.setName(processInfo.getProcessName());
        craft.setVersion(processInfo.getProcessVersion());
        craft.setDesc(processInfo.getProcessDesc());
        craft.setCreateTime(DateUtils.getNowDate());
        craft.setCreateBy(SecurityUtils.getUsername());
        craftMapper.insertCraft(craft);

        //导入工序信息
        operationList.forEach(operation -> {
            OperationInfo operationInfo = operation.getOperationInfo();
            Process process = new Process();
            process.setCode(operationInfo.getOperationNo());
            process.setName(operationInfo.getOperationName());
            process.setCraftId(craft.getId());
            process.setCraftCode(craft.getCode());
            process.setDesc(operationInfo.getOperationDesc());
            process.setProcessMaterialInfo(JSONObject.toJSONString(operation.getOperationMaterialInfo()));
            process.setCreateTime(DateUtils.getNowDate());
            process.setCreateBy(SecurityUtils.getUsername());
            processMapper.insertProcess(process);

            //导入工步信息
            operation.getStepList().forEach(step -> {
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
        if (isNotAlg)
            craftMapper.changeCraftStatus(id, 3);
        else if (isNotGuide)
            craftMapper.changeCraftStatus(id, 2);
        else
            craftMapper.changeCraftStatus(id, 4);
    }

    @Override
    public List<String> getCraftSelector() {
        return craftMapper.getCodeList();
    }

    @Override
    public List<SelectorInfoVO> getSelectorOptions() {

        return List.of();
    }
}
