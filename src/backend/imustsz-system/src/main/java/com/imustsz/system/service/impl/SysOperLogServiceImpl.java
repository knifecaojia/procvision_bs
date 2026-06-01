package com.imustsz.system.service.impl;

import java.util.List;

import com.imustsz.common.exception.ServiceException;
import com.imustsz.common.utils.DateUtils;
import com.imustsz.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.imustsz.system.domain.SysOperLog;
import com.imustsz.system.mapper.SysOperLogMapper;
import com.imustsz.system.service.ISysOperLogService;

/**
 * 操作日志 服务层处理
 * 
 * @author ruoyi
 */
@Service
public class SysOperLogServiceImpl implements ISysOperLogService
{
    @Autowired
    private SysOperLogMapper operLogMapper;

    /**
     * 新增操作日志
     * 
     * @param operLog 操作日志对象
     */
    @Override
    public void insertOperlog(SysOperLog operLog)
    {
        operLogMapper.insertOperlog(operLog);
    }

    /**
     * 查询系统操作日志集合
     * 
     * @param operLog 操作日志对象
     * @return 操作日志集合
     */
    @Override
    public List<SysOperLog> selectOperLogList(SysOperLog operLog)
    {
        // 1. 获取当前登录用户名
        String currentUsername = SecurityUtils.getUsername();

        // 2. 判断当前登录人是否拥有“授权管理员”角色
        boolean isAuthAdmin = SecurityUtils.getLoginUser().getUser().getRoles().stream()
                .anyMatch(role -> "auth_admin".equals(role.getRoleKey()));

        // 3. 如果是授权管理员，强制在 SQL 中排除他自己的操作记录
        if (isAuthAdmin) {
            // 利用若依基类 BaseEntity 中的 params 传递自定义排除参数
            operLog.getParams().put("excludeOperName", currentUsername);

            // 可选：等保严格规范中，授权管理员只审查系统管理员，也可以在这里强制限制他只能看系统管理员的日志
            // operLog.getParams().put("onlyWatchSysAdmin", "sysadmin");
        }

        return operLogMapper.selectOperLogList(operLog);
    }

    /**
     * 批量删除系统操作日志
     * 
     * @param operIds 需要删除的操作日志ID
     * @return 结果
     */
    @Override
    public int deleteOperLogByIds(Long[] operIds)
    {
        return operLogMapper.deleteOperLogByIds(operIds);
    }

    /**
     * 查询操作日志详细
     * 
     * @param operId 操作ID
     * @return 操作日志对象
     */
    @Override
    public SysOperLog selectOperLogById(Long operId)
    {
        return operLogMapper.selectOperLogById(operId);
    }

    /**
     * 清空操作日志
     */
    @Override
    public void cleanOperLog()
    {
        operLogMapper.cleanOperLog();
    }

    /**
     * 审核操作日志
     * * @param operLog 操作日志
     * @return 结果
     */
    @Override
    public int auditOperlog(SysOperLog operLog)
    {
        // 1. 先从数据库查出完整的原始操作记录 (因为前端传过来的可能只有 operId 和 auditStatus)
        SysOperLog dbLog = operLogMapper.selectOperLogById(operLog.getOperId());
        if (dbLog == null) {
            throw new ServiceException("操作日志不存在！");
        }

        // 2. 核心拦截：校验操作人是不是当前登录的授权管理员自己
        if (dbLog.getOperName().equals(SecurityUtils.getUsername())) {
            throw new ServiceException("等保三员规则限制：禁止审核自己的操作记录！");
        }

        // 3. 核心拦截：防止审核“审核动作”本身产生无限循环
        // 假设你 Controller 上的 @Log(title = "操作日志")
        if ("操作日志".equals(dbLog.getTitle())) {
            throw new ServiceException("审计及日志类操作记录无需再次审核！");
        }

        // 4. 执行更新
        operLog.setAuditBy(SecurityUtils.getUsername());
        operLog.setAuditTime(DateUtils.getNowDate());
        return operLogMapper.updateOperlog(operLog);
    }
}
