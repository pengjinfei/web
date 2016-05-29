package com.pengjinfei.common.lock;

import com.pengjinfei.common.lock.impl.ZookeeperPreemptiveLock;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.AopUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

/**
 * Created by Pengjinfei on 16/5/28.
 * Description:
 */
public class SeizeLockInterceptor implements MethodInterceptor {

    private PreemptiveLock preemptiveLock=new ZookeeperPreemptiveLock();

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Class<?> targetClass = (invocation.getThis() != null) ? AopUtils.getTargetClass(invocation.getThis()) : null;

        Object retVal=null;
        try {
            Method method = invocation.getMethod();
            Lock lock = method.getAnnotation(Lock.class);
            if (lock != null) {
                String value = lock.value();
                if (!StringUtils.hasText(value)) {
                    value = targetClass.getName() + "." + method.getName();
                }
                if (!preemptiveLock.getLock(value)) {
                    return null;
                }
            }
            retVal = invocation.proceed();
        } finally {
            preemptiveLock.releaseLock();
        }
        return retVal;
    }
}
