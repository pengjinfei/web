package com.pengjinfei.common.lock;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationClassFilter;
import org.springframework.aop.support.annotation.AnnotationMethodMatcher;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

/**
 * Created by Pengjinfei on 16/5/29.
 * Description:
 */
public class LockAdvisor extends AbstractBeanFactoryPointcutAdvisor {

    private String lockPath;

    private Pointcut pointcut = new LockAnnotationMatchingPointcut();

    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }

    private class LockAnnotationMatchingPointcut implements Pointcut {

        private ClassFilter classFilter;

        private MethodMatcher methodMatcher;

        LockAnnotationMatchingPointcut() {
            classFilter = new AnnotationClassFilter(Service.class);
            methodMatcher = new AnnotationMethodMatcher(Lock.class){
                @Override
                public boolean matches(Method method, Class<?> targetClass) {
                    boolean matches = super.matches(method, targetClass);
                    if (matches) {
                        lockPath=method.getAnnotation(Lock.class).value();
                        if (!StringUtils.hasText(lockPath)) {
                            lockPath = targetClass.getName() + "." + method.getName();
                        }
                    }
                    return matches;
                }
            };
        }

        @Override
        public ClassFilter getClassFilter() {
            return classFilter;
        }

        @Override
        public MethodMatcher getMethodMatcher() {
            return methodMatcher;
        }
    }
}
