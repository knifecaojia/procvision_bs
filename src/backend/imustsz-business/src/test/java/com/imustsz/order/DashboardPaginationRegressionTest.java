package com.imustsz.order;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInterceptor;
import com.imustsz.order.domain.dto.ResultQuery;
import com.imustsz.order.mapper.DashboardResultMapper;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.reflection.ParamNameResolver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

/** Standalone regression using the actual PageHelper interceptor, without a database.
 * Run main with the application's runtime/test classpath. */
public class DashboardPaginationRegressionTest {
    public static void main(String[] args) throws Exception {
        Configuration configuration = new Configuration();
        String resource = "mapper/order/DashboardResultMapper.xml";
        try (InputStream input = DashboardPaginationRegressionTest.class.getClassLoader().getResourceAsStream(resource)) {
            if (input == null) throw new AssertionError("Mapper resource missing");
            new XMLMapperBuilder(input, configuration, resource, configuration.getSqlFragments()).parse();
        }
        PageInterceptor interceptor = new PageInterceptor();
        Properties properties = new Properties();
        properties.setProperty("helperDialect", "mysql");
        properties.setProperty("supportMethodsArguments", "true");
        properties.setProperty("params", "count=countSql");
        interceptor.setProperties(properties);
        ResultQuery query = new ResultQuery();
        query.setPageNum(2);
        query.setPageSize(12);
        query.validated();
        int checked = 0;
        for (Method method : DashboardResultMapper.class.getMethods()) {
            if (method.getDeclaringClass() != DashboardResultMapper.class) continue;
            boolean details = method.getName().equals("selectRecords");
            Object[] values = details ? new Object[]{query, 12, 12} : new Object[]{query};
            check(configuration, interceptor, method, values, details || method.getName().equals("selectNgSteps") ? 1 : 0);
            checked++;
            if (details) {
                check(configuration, interceptor, method, new Object[]{query, 0, 500}, 1);
                checked++;
            }
        }
        System.out.println("PASS: " + checked + " real PageHelper interceptor cases; aggregate queries unpaged, TOP10 and detail/export SQL retain exactly one LIMIT, no implicit count queries.");
    }

    private static void check(Configuration configuration, PageInterceptor interceptor, Method method,
                              Object[] values, int expectedLimits) throws Exception {
        Object parameter = new ParamNameResolver(configuration, method).getNamedParams(values);
        MappedStatement statement = configuration.getMappedStatement(DashboardResultMapper.class.getName() + "." + method.getName());
        List<String> sqls = new ArrayList<>();
        Executor executor = (Executor) Proxy.newProxyInstance(Executor.class.getClassLoader(), new Class<?>[]{Executor.class},
            (proxy, called, args) -> {
                if (called.getName().equals("createCacheKey")) return new CacheKey();
                if (called.getName().equals("query")) {
                    MappedStatement ms = (MappedStatement) args[0];
                    BoundSql bound = args.length == 6 ? (BoundSql) args[5] : ms.getBoundSql(args[1]);
                    sqls.add(bound.getSql());
                    return ms.getId().endsWith("_COUNT") ? Collections.singletonList(100L) : Collections.emptyList();
                }
                if (called.getName().equals("isClosed")) return false;
                return null;
            });
        PageHelper.clearPage();
        try {
            interceptor.intercept(new Invocation(executor, Executor.class.getMethod("query", MappedStatement.class,
                Object.class, RowBounds.class, ResultHandler.class), new Object[]{statement, parameter, RowBounds.DEFAULT, null}));
        } catch (Throwable ex) {
            throw new AssertionError(method.getName() + " interceptor failed", ex);
        } finally {
            PageHelper.clearPage();
        }
        if (sqls.size() != 1) throw new AssertionError(method.getName() + ": implicit pagination/count detected: " + sqls);
        long limits = Pattern.compile("\\bLIMIT\\b", Pattern.CASE_INSENSITIVE).matcher(sqls.get(0)).results().count();
        if (limits != expectedLimits) throw new AssertionError(method.getName() + ": unexpected LIMIT count " + limits + ": " + sqls);
        if (method.getName().equals("selectRecords")) {
            BoundSql bound = statement.getBoundSql(parameter);
            if (!bound.getSql().contains("LIMIT ? OFFSET ?")) throw new AssertionError("Explicit detail pagination missing");
            org.apache.ibatis.reflection.MetaObject meta = configuration.newMetaObject(parameter);
            if (!values[1].equals(meta.getValue("offset")) || !values[2].equals(meta.getValue("limit"))) {
                throw new AssertionError("Explicit page/export bounds changed");
            }
        }
    }
}
