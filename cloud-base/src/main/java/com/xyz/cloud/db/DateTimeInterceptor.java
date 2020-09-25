package com.xyz.cloud.db;

import com.xyz.cloud.db.annotation.CreateTime;
import com.xyz.cloud.db.annotation.UpdateTime;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.Objects;
import java.util.Properties;

/**
 * 添加时间注解拦截器,通过拦截sql，自动给带注解的属性添加时间
 *
 * @author sxl
 */
@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class,
        Object.class})})
public class DateTimeInterceptor implements Interceptor {

    /**
     * sql拦截器,用来处理创建时间和更新时间
     *
     * @param invocation
     * @return
     * @throws Throwable
     */
    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];

        // 获取 SQL
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();

        // 获取参数
        Object parameter = invocation.getArgs()[1];
        if (parameter == null)
            return invocation.proceed();
        // 获取私有成员变量
        Class<?> clazz = parameter.getClass();
        while (Objects.nonNull(clazz)) {
            Field[] fields = clazz.getDeclaredFields();
            createAndUpdateTimeHandle(sqlCommandType, parameter, fields);
            clazz = clazz.getSuperclass();
        }
        return invocation.proceed();
    }

    /**
     * 创建和更新时间处理
     *
     * @param sqlCommandType
     * @param parameter
     * @param fields
     * @throws IllegalAccessException
     */
    private void createAndUpdateTimeHandle(SqlCommandType sqlCommandType, Object parameter, Field[] fields) throws IllegalAccessException {
        for (Field field : fields) {
            if (field.getAnnotation(CreateTime.class) != null) {
                if (SqlCommandType.INSERT.equals(sqlCommandType)) {
                    // insert语句插入createTime
                    field.setAccessible(true);
                    // 这里设置时间，当然时间格式可以自定。比如转成String类型
                    field.set(parameter, new Date());
                    continue;
                }
            }

            if (field.getAnnotation(UpdateTime.class) != null) {
                if (SqlCommandType.INSERT.equals(sqlCommandType)
                        || SqlCommandType.UPDATE.equals(sqlCommandType)) {
                    // insert 或update语句插入updateTime
                    field.setAccessible(true);
                    field.set(parameter, new Date());
                }
            }
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }
}