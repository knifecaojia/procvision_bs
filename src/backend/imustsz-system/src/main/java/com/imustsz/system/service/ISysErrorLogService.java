package com.imustsz.system.service;

import com.imustsz.common.core.domain.entity.SysErrorLog;

import java.util.List;

/**
 * 系统异常日志Service接口
 * 
 * @author ruoyi
 * @date 2026-05-23
 */
public interface ISysErrorLogService 
{
    /**
     * 查询系统异常日志
     * 
     * @param id 系统异常日志主键
     * @return 系统异常日志
     */
    public SysErrorLog selectSysErrorLogById(Long id);

    /**
     * 查询系统异常日志列表
     * 
     * @param sysErrorLog 系统异常日志
     * @return 系统异常日志集合
     */
    public List<SysErrorLog> selectSysErrorLogList(SysErrorLog sysErrorLog);

    /**
     * 新增系统异常日志
     * 
     * @param sysErrorLog 系统异常日志
     * @return 结果
     */
    public int insertSysErrorLog(SysErrorLog sysErrorLog);

    /**
     * 修改系统异常日志
     * 
     * @param sysErrorLog 系统异常日志
     * @return 结果
     */
    public int updateSysErrorLog(SysErrorLog sysErrorLog);

    /**
     * 批量删除系统异常日志
     * 
     * @param ids 需要删除的系统异常日志主键集合
     * @return 结果
     */
    public int deleteSysErrorLogByIds(Long[] ids);

    /**
     * 删除系统异常日志信息
     * 
     * @param id 系统异常日志主键
     * @return 结果
     */
    public int deleteSysErrorLogById(Long id);

    void cleanSysErrorLog();
}
