package com.pengjinfei.common.web.session;

import org.springframework.beans.BeanUtils;
import org.springframework.core.MethodParameter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Author: EX-PENGJINFEI001
 * DATE: 2016-02-17
 * Description: 解析@SessionAttr
 * @see SessionAttr
 */
public class SessionAttrArgumentResolver implements WebArgumentResolver {

    @Override
    public Object resolveArgument(MethodParameter methodParameter, NativeWebRequest webRequest) throws Exception {
        SessionAttr annotation = methodParameter.getParameterAnnotation(SessionAttr.class);
        if (annotation == null) {
            return UNRESOLVED;
        }
        String key = annotation.value();
        key = key.trim();
        if (!StringUtils.hasText(key)) {
            throw new WebException("Method[" + methodParameter.getMethod().getDeclaringClass().getName() + "."
                    + methodParameter.getMethod().getName()
                    + "]Annotation[" + SessionAttr.class.getName()
                    + "]Not setter [value].");
        }
        //TODO session放入redis时整改
        Object obj = webRequest.getAttribute(key, NativeWebRequest.SCOPE_SESSION);
        if (obj == null) {
            if (annotation.newable()) {
                obj = resolveNewInstance(methodParameter.getParameterType());
                webRequest.setAttribute(key, obj, NativeWebRequest.SCOPE_SESSION);
            } else {
                if (annotation.required()) {
                    throw new WebException("Not found attr[" + key + "] in session scope");
                } else {
                    return null;
                }
            }
        }
        return obj;
    }

    protected Object resolveNewInstance(Class<?> clazz) throws Exception {
        if (clazz.isInterface()) {
            if (clazz.isAssignableFrom(ArrayList.class)) {
                return new ArrayList<Object>();
            } else if (clazz.isAssignableFrom(HashMap.class)) {
                return new HashMap<Object, Object>();
            } else {
                throw new WebException("Class[" + clazz.getName()
                        + "]is a interface,can't instantiate. ");
            }
        } else {
            return BeanUtils.instantiate(clazz);
        }
    }

}
