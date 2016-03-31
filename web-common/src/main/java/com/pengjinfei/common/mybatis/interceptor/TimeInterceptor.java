package com.pengjinfei.common.mybatis.interceptor;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author: EX-PENGJINFEI001
 * Date: 2016-03-30
 * Description:
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class})})
public class TimeInterceptor implements Interceptor {

    private final static String MYSQL_DIALECT = "mysql";

    private final static String ORACLE_DIALECT = "oracle";

    private final static String CREATE_DATE = "create_date";

    private final static String UPDATE_DATE = "update_date";

    private final static Pattern UPDATE_PATTERN = Pattern.compile("update(\\s+\\w+)*\\s+set(\\s*\\w+=\\s*\\S+)(,\\s*\\w+=\\s*\\S+)*", Pattern.CASE_INSENSITIVE);
    private final static Pattern INSERT_PATTERN = Pattern.compile("\\)\\s*values\\s+.*(?=\\))", Pattern.CASE_INSENSITIVE);

    private static Logger logger = LoggerFactory.getLogger(TimeInterceptor.class);

    private String dialect;

    private Configuration configuration;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        BoundSql boundSql = statementHandler.getBoundSql();
        changeBoundSql(boundSql);

        return invocation.proceed();
    }

    private void changeBoundSql(BoundSql boundSql) {
        String originSql = boundSql.getSql();
        String newSql = null;
        Matcher matcherInsert = INSERT_PATTERN.matcher(originSql);
        if (matcherInsert.find()) {
            newSql = originSql.substring(0, matcherInsert.start()) +
                    "," + CREATE_DATE + matcherInsert.group() + "," +
                    getTimestampByDialect(dialect) + originSql.substring(matcherInsert.end());
        } else {
            Matcher matcherUpdate = UPDATE_PATTERN.matcher(originSql);
            if (matcherUpdate.find()) {
                String table = matcherUpdate.group(1);
                String added = " ," + table + "." + UPDATE_DATE + "=" + getTimestampByDialect(dialect);
                newSql = originSql.substring(0, matcherUpdate.end()) + added + originSql.substring(matcherUpdate.end());
            }
        }
        if (newSql != null) {
            try {
                Field field = BoundSql.class.getDeclaredField("sql");
                field.setAccessible(true);
                field.set(boundSql, newSql);
                logger.info(newSql);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                logger.info("Add timestamp for sql:"+originSql+" failed." + e);
            }
        }
    }

    private String getTimestampByDialect(String dialect) {
        if (dialect.equalsIgnoreCase(MYSQL_DIALECT)) {
            return "now()";
        } else if (dialect.equalsIgnoreCase(ORACLE_DIALECT)) {
            return "sysdate";
        }
        throw new UnsupportedOperationException("Database dialect is unkown");
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        String dialect = properties.getProperty("dialect");
        if (!StringUtils.hasText(dialect)) {
            throw new UnsupportedOperationException("Database dialect is not setted");
        }
        if (dialect.equalsIgnoreCase(ORACLE_DIALECT)) {
            this.dialect = ORACLE_DIALECT;
        } else if (dialect.equalsIgnoreCase(MYSQL_DIALECT)) {
            this.dialect = MYSQL_DIALECT;
        } else {
            throw new UnsupportedOperationException("Database dialect is unkown");
        }
    }
}
