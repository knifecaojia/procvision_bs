package com.imustsz.framework.aspectj;

import com.imustsz.common.utils.SecurityUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Date;

@Aspect
@Component
public class AutoFillAspect {

    @Before("@annotation(autoFill)")
    public void before(JoinPoint joinPoint, AutoFill autoFill) {
        Object[] args = joinPoint.getArgs();
        if (args.length == 0) return;

        Object entity = args[0];

        try {
            if ("insert".equals(autoFill.value())) {
                setField(entity, "createBy", SecurityUtils.getUsername());
                setField(entity, "createTime", new Date());
            } else if ("update".equals(autoFill.value())) {
                setField(entity, "updateBy", SecurityUtils.getUsername());
                setField(entity, "updateTime", new Date());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setField(Object obj, String fieldName, Object value) throws Exception {
        Field field = obj.getClass().getSuperclass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, value);
    }
}
