package com.imustsz.system.service.impl;

import com.imustsz.common.core.domain.entity.SysErrorLog;
import com.imustsz.common.utils.DateUtils;
import com.imustsz.system.mapper.SysErrorLogMapper;
import com.imustsz.system.service.ISysErrorLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 系统异常日志Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-05-23
 */
@Service
public class SysErrorLogServiceImpl implements ISysErrorLogService
{
    @Autowired
    private SysErrorLogMapper sysErrorLogMapper;

    /**
     * 查询系统异常日志
     * 
     * @param id 系统异常日志主键
     * @return 系统异常日志
     */
    @Override
    public SysErrorLog selectSysErrorLogById(Long id)
    {
        return sysErrorLogMapper.selectSysErrorLogById(id);
    }

    /**
     * 查询系统异常日志列表
     * 
     * @param sysErrorLog 系统异常日志
     * @return 系统异常日志
     */
    @Override
    public List<SysErrorLog> selectSysErrorLogList(SysErrorLog sysErrorLog)
    {
        return sysErrorLogMapper.selectSysErrorLogList(sysErrorLog);
    }

    /**
     * 新增系统异常日志
     * 
     * @param sysErrorLog 系统异常日志
     * @return 结果
     */
    @Override
    public int insertSysErrorLog(SysErrorLog sysErrorLog)
    {
        sysErrorLog.setCreateTime(DateUtils.getNowDate());
        return sysErrorLogMapper.insertSysErrorLog(sysErrorLog);
    }

    /**
     * 修改系统异常日志
     * 
     * @param sysErrorLog 系统异常日志
     * @return 结果
     */
    @Override
    public int updateSysErrorLog(SysErrorLog sysErrorLog)
    {
        return sysErrorLogMapper.updateSysErrorLog(sysErrorLog);
    }

    /**
     * 批量删除系统异常日志
     * 
     * @param ids 需要删除的系统异常日志主键
     * @return 结果
     */
    @Override
    public int deleteSysErrorLogByIds(Long[] ids)
    {
        return sysErrorLogMapper.deleteSysErrorLogByIds(ids);
    }

    /**
     * 删除系统异常日志信息
     * 
     * @param id 系统异常日志主键
     * @return 结果
     */
    @Override
    public int deleteSysErrorLogById(Long id)
    {
        return sysErrorLogMapper.deleteSysErrorLogById(id);
    }

    @Override
    public void cleanSysErrorLog() {
        sysErrorLogMapper.deleteAll();
    }
}
