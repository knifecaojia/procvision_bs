package com.imustsz.framework.interceptor;

import com.imustsz.common.utils.SecurityUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.springframework.stereotype.Component;
import com.imustsz.common.core.domain.BaseEntity;
import java.util.Date;
import java.util.Properties;

/**
 * MyBatis 自动填充创建/修改时间拦截器
 */
@Component // 交给Spring容器管理
@Intercepts({
        // 拦截Executor的update方法（包含insert/update）
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
public class AutoFillInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 1. 获取拦截的参数
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        Object parameter = invocation.getArgs()[1];

        // 2. 仅处理继承自BaseEntity的实体类
        if (parameter instanceof BaseEntity) {
            BaseEntity entity = (BaseEntity) parameter;
            Date now = new Date();

            // 3. 根据SQL类型填充时间
            SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
            if (SqlCommandType.INSERT.equals(sqlCommandType)) {
                // 新增：填充创建时间+修改时间
                entity.setCreateTime(now);
                entity.setCreateBy(SecurityUtils.getUsername());
                entity.setUpdateTime(now);
                entity.setUpdateBy(SecurityUtils.getUsername());
            } else if (SqlCommandType.UPDATE.equals(sqlCommandType)) {
                // 修改：仅填充修改时间
                entity.setUpdateTime(now);
                entity.setUpdateBy(SecurityUtils.getUsername());
            }
        }

        // 4. 执行原方法
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        // 生成代理对象
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        // 可配置拦截器参数（无需则空实现）
    }
}