package com.imustsz.framework.interceptor;

import com.imustsz.common.utils.SecurityUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.springframework.stereotype.Component;
import com.imustsz.common.core.domain.BaseEntity;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

@Component
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
public class AutoFillInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement mappedStatement = (MappedStatement) args[0];
        Object parameter = args[1];

        // 1. 尝试获取实体对象
        BaseEntity entity = null;
        if (parameter instanceof BaseEntity) {
            entity = (BaseEntity) parameter;
        } else if (parameter instanceof Map) {
            // 处理参数被封装成 Map 的情况 (例如使用了 @Param)
            Map<?, ?> map = (Map<?, ?>) parameter;
            for (Object arg : map.values()) {
                if (arg instanceof BaseEntity) {
                    entity = (BaseEntity) arg;
                    break;
                }
            }
        }

        // 2. 如果找到了 BaseEntity，执行填充
        if (entity != null) {
            Date now = new Date();
            String username = SecurityUtils.getUsername(); // 注意：如果在非Web环境(如定时任务)调用，这里可能报错或返回null

            SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();

            if (SqlCommandType.INSERT.equals(sqlCommandType)) {
                entity.setCreateTime(now);
                entity.setCreateBy(username);
                entity.setUpdateTime(now);
                entity.setUpdateBy(username);

            } else if (SqlCommandType.UPDATE.equals(sqlCommandType)) {
                entity.setUpdateTime(now);
                entity.setUpdateBy(username);
            }
        }

        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {}
}