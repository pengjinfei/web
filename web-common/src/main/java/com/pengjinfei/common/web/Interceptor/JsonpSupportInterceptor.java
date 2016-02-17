package com.pengjinfei.common.web.Interceptor;

import com.pengjinfei.common.util.SecurityUtil;
import com.pengjinfei.common.web.session.WebException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.regex.Pattern;

/**
 * Author: EX-PENGJINFEI001
 * DATE: 2016-02-17
 * Description:
 */
public class JsonpSupportInterceptor extends HandlerInterceptorAdapter
        implements InitializingBean {

    private String defaultPattern="^\\w+$";

    private Pattern pattern = null;

    private String callbackParameterName = "callback";

    private Logger logger= LoggerFactory.getLogger(JsonpSupportInterceptor.class);

    @Override
    public void afterPropertiesSet() throws Exception {
        pattern = Pattern.compile(defaultPattern);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String parameter = request.getParameter(callbackParameterName);
        if (StringUtils.hasText(parameter)) {
            if(pattern.matcher(parameter).matches()){
                PrintWriter writer = response.getWriter();
                writer.write(SecurityUtil.outputfilter(parameter));
                writer.write('(');
            }else {
                logger.error("jsonp callback function name must be matched by [^\\w+$]");
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String parameter = request.getParameter(callbackParameterName);
        if(StringUtils.hasText(parameter)){
            try {
                Writer out = response.getWriter();
                out.write(");");
                out.flush();
            } catch (IOException e) {
                throw new WebException(e.getMessage(), e);
            }
        }
    }

    public String getDefaultPattern() {
        return defaultPattern;
    }

    public void setDefaultPattern(String defaultPattern) {
        this.defaultPattern = defaultPattern;
    }

    public String getCallbackParameterName() {
        return callbackParameterName;
    }

    public void setCallbackParameterName(String callbackParameterName) {
        this.callbackParameterName = callbackParameterName;
    }
}
