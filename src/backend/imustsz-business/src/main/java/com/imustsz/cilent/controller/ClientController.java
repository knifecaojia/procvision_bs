package com.imustsz.cilent.controller;


import com.imustsz.common.core.domain.AjaxResult;
import com.imustsz.common.core.domain.model.LoginBody;
import com.imustsz.common.utils.DateUtils;
import com.imustsz.framework.web.service.SysLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


/**
 * 登录验证
 *
 */
@RestController
@RequestMapping("/client/auth")
public class ClientController {

    @Autowired
    private SysLoginService loginService;

    @Value("${token.expireTime}")
    private Long expireTime;

    @PostMapping("/login")
    public AjaxResult login(@RequestBody LoginBody loginBody) {
        // 生成令牌
        String token = loginService.login(loginBody.getUsername(), loginBody.getPassword(), loginBody.getCode());

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("expire_time", DateUtils.addMinutes(DateUtils.getNowDate(), expireTime.intValue()).getTime());

        return AjaxResult.success("登录成功", data);
    }
}
