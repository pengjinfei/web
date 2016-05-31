package com.pengjinfei.common.lock;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.aop.support.annotation.AnnotationClassFilter;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

/**
 * Created by Pengjinfei on 16/6/1.
 * Description:
 */
public abstract class LockAnnotationMatchingPointcut extends StaticMethodMatcherPointcut {

    private ClassFilter classFilter=new AnnotationClassFilter(Service.class);

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        LockAttribute lockAttribute = getLockAttribute();
        // Ignore CGLIB subclasses - introspect the actual user class.
        Class<?> userClass = ClassUtils.getUserClass(targetClass);
        // The method may be on an interface, but we need attributes from the target class.
        // If the target class is null, the method will be unchanged.
        Method specificMethod = ClassUtils.getMostSpecificMethod(method, userClass);
        // If we are dealing with method with generic parameters, find the original method.
        specificMethod = BridgeMethodResolver.findBridgedMethod(specificMethod);
        Lock lock = AnnotationUtils.getAnnotation(specificMethod, Lock.class);
        if (lock == null) {
            return false;
        }
        String value = lock.value();
        if (!StringUtils.hasText(value)) {
            value = targetClass.getName() + "." + method.getName();
        }
        lockAttribute.cache(method,targetClass,value);
        return true;
    }

    @Override
    public ClassFilter getClassFilter() {
        return classFilter;
    }

    public abstract LockAttribute getLockAttribute();
}
