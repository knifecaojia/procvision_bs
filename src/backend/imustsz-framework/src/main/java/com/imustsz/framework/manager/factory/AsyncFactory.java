package com.imustsz.framework.manager.factory;

import java.util.TimerTask;

import com.imustsz.common.core.domain.entity.SysErrorLog;
import com.imustsz.common.utils.*;
import com.imustsz.system.service.ISysErrorLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.imustsz.common.constant.Constants;
import com.imustsz.common.utils.ip.AddressUtils;
import com.imustsz.common.utils.ip.IpUtils;
import com.imustsz.common.utils.spring.SpringUtils;
import com.imustsz.system.domain.SysLogininfor;
import com.imustsz.system.domain.SysOperLog;
import com.imustsz.system.service.ISysLogininforService;
import com.imustsz.system.service.ISysOperLogService;
import eu.bitwalker.useragentutils.UserAgent;

import javax.servlet.http.HttpServletRequest;

/**
 * 异步工厂（产生任务用）
 * 
 * @author ruoyi
 */
public class AsyncFactory
{
    private static final Logger sys_user_logger = LoggerFactory.getLogger("sys-user");

    /**
     * 记录登录信息
     * 
     * @param username 用户名
     * @param status 状态
     * @param message 消息
     * @param args 列表
     * @return 任务task
     */
    public static TimerTask recordLogininfor(final String username, final String status, final String message,
            final Object... args)
    {
        final UserAgent userAgent = UserAgent.parseUserAgentString(ServletUtils.getRequest().getHeader("User-Agent"));
        final String ip = IpUtils.getIpAddr();
        return new TimerTask()
        {
            @Override
            public void run()
            {
                String address = AddressUtils.getRealAddressByIP(ip);
                StringBuilder s = new StringBuilder();
                s.append(LogUtils.getBlock(ip));
                s.append(address);
                s.append(LogUtils.getBlock(username));
                s.append(LogUtils.getBlock(status));
                s.append(LogUtils.getBlock(message));
                // 打印信息到日志
                sys_user_logger.info(s.toString(), args);
                // 获取客户端操作系统
                String os = userAgent.getOperatingSystem().getName();
                // 获取客户端浏览器
                String browser = userAgent.getBrowser().getName();
                // 封装对象
                SysLogininfor logininfor = new SysLogininfor();
                logininfor.setUserName(username);
                logininfor.setIpaddr(ip);
                logininfor.setLoginLocation(address);
                logininfor.setBrowser(browser);
                logininfor.setOs(os);
                logininfor.setMsg(message);
                // 日志状态
                if (StringUtils.equalsAny(status, Constants.LOGIN_SUCCESS, Constants.LOGOUT, Constants.REGISTER))
                {
                    logininfor.setStatus(Constants.SUCCESS);
                }
                else if (Constants.LOGIN_FAIL.equals(status))
                {
                    logininfor.setStatus(Constants.FAIL);
                }
                // 插入数据
                SpringUtils.getBean(ISysLogininforService.class).insertLogininfor(logininfor);
            }
        };
    }

    /**
     * 操作日志记录
     * 
     * @param operLog 操作日志信息
     * @return 任务task
     */
    public static TimerTask recordOper(final SysOperLog operLog)
    {
        return new TimerTask()
        {
            @Override
            public void run()
            {
                // 远程查询操作地点
                operLog.setOperLocation(AddressUtils.getRealAddressByIP(operLog.getOperIp()));
                SpringUtils.getBean(ISysOperLogService.class).insertOperlog(operLog);
            }
        };
    }

    /**
     * 记录全局异常日志
     */
    public static TimerTask recordErrorLog(final Exception e) {
        // 提前获取 Request 信息（因为定时任务是在另一个线程执行，无法直接获取Request上下文）
        HttpServletRequest request = ServletUtils.getRequest();
        final String requestUri = request.getRequestURI();
        final String requestMethod = request.getMethod();
        // 尝试获取当前登录用户（未登录则为空）
        String username = "";
        try {
            username = SecurityUtils.getUsername();
        } catch (Exception ignored) {}

        final String finalUsername = username;

        return new TimerTask() {
            @Override
            public void run() {
                SysErrorLog errorLog = new SysErrorLog();
                errorLog.setRequestUri(requestUri);
                errorLog.setRequestMethod(requestMethod);
                errorLog.setExceptionName(e.getClass().getName());
                errorLog.setExceptionMessage(e.getMessage());
                errorLog.setCreateBy(finalUsername);
                errorLog.setCreateTime(new java.util.Date());

                // 获取 Spring Bean 并插入数据库
                SpringUtils.getBean(ISysErrorLogService.class).insertSysErrorLog(errorLog);
            }
        };
    }
}
