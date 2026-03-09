package com.imustsz.craft.service.impl;

import com.imustsz.craft.domain.Craft;
import com.imustsz.craft.domain.Process;
import com.imustsz.craft.mapper.BizStepMapper;
import com.imustsz.craft.mapper.CraftMapper;
import com.imustsz.craft.mapper.ProcessMapper;
import com.imustsz.craft.service.IProcessService;
import com.imustsz.order.domain.BizWorkOrder;
import com.imustsz.order.mapper.BizWorkOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 工序信息Service业务层处理
 * 
 * @author imustsz
 * @date 2025-12-18
 */
@Service
public class ProcessServiceImpl implements IProcessService 
{
    @Autowired
    private ProcessMapper processMapper;

    @Autowired
    private BizStepMapper bizStepMapper;

    @Autowired
    private BizWorkOrderMapper bizWorkOrderMapper;

    @Autowired
    private CraftMapper craftMapper;

    /**
     * 查询工序信息
     * 
     * @param id 工序信息主键
     * @return 工序信息
     */
    @Override
    public Process selectProcessById(Long id)
    {
        return processMapper.selectProcessById(id);
    }

    /**
     * 查询工序信息列表
     * 
     * @param process 工序信息
     * @return 工序信息
     */
    @Override
    public List<Process> selectProcessList(Process process)
    {
        return processMapper.selectProcessList(process);
    }

    /**
     * 新增工序信息
     * 
     * @param process 工序信息
     * @return 结果
     */
    @Override
    public int insertProcess(Process process)
    {
        return processMapper.insertProcess(process);
    }

    /**
     * 修改工序信息
     * 
     * @param process 工序信息
     * @return 结果
     */
    @Override
    public int updateProcess(Process process)
    {
        return processMapper.updateProcess(process);
    }

    /**
     * 批量删除工序信息
     * 
     * @param ids 需要删除的工序信息主键
     * @return 结果
     */
    @Override
    public int deleteProcessByIds(Long[] ids)
    {
        for (Long id : ids) {
            checkProcess(id);
        }
        return processMapper.deleteProcessByIds(ids);
    }

    /**
     * 删除工序信息信息
     * 
     * @param id 工序信息主键
     * @return 结果
     */
    @Override
    public int deleteProcessById(Long id)
    {
        return processMapper.deleteProcessById(id);
    }

    @Override
    public int deleteProcessByCraftId(Long id) {
        Long[] ids = processMapper.getDelIdsByCraftId(id);
        if (ids.length == 0)
            return 0;
        for (Long id1 : ids){
            checkProcess(id1);
        }
        return processMapper.deleteProcessByIds(ids);
    }

    private void checkProcess(Long id1) {
        Process process = processMapper.selectProcessById(id1);
        Craft craft = craftMapper.selectCraftById(process.getCraftId());
        BizWorkOrder bizWorkOrder = bizWorkOrderMapper.selectWorkOrderByCraftAndProcess(craft.getCode(), craft.getVersion(), process.getCode());
        if (bizWorkOrder != null)
            throw new RuntimeException("工序已关联任务，不能删除");
        bizStepMapper.deleteBizStepByProcessId(id1);
    }

    @Override
    public int bindAlg(Long id, Long algId) {
        return processMapper.bindAlg(id, algId);
    }

    @Override
    public boolean safeDelCheck(Long[] ids) {
        return processMapper.selectProcessByAlgId(ids) > 0;
    }
}
