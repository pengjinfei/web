package com.pengjinfei.common.lock;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Created by Pengjinfei on 16/5/28.
 * Description:
 */
public class SeizeLockInterceptor implements MethodInterceptor,ApplicationContextAware,InitializingBean {

    private static Logger logger = LoggerFactory.getLogger(SeizeLockInterceptor.class);

    private LockAttribute lockAttribute;

    private PreemptiveLock preemptiveLock;

    private ApplicationContext applicationContext;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Class<?> targetClass = (invocation.getThis() != null ? AopUtils.getTargetClass(invocation.getThis()) : null);
        Object retVal = null;
        String lockPath = null;
        try {
            lockPath = getLockAttribute().getLockPath(invocation.getMethod(), targetClass);
            logger.info("Begin seize lock on path:" + lockPath);
            if (preemptiveLock.getLock(lockPath)) {
                retVal = invocation.proceed();
            }
        } finally {
            if (preemptiveLock != null) {
                preemptiveLock.releaseLock(lockPath);
            }
        }
        return retVal;
    }

    public LockAttribute getLockAttribute() {
        return lockAttribute;
    }

    public void setLockAttribute(LockAttribute lockAttribute) {
        this.lockAttribute = lockAttribute;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        preemptiveLock = applicationContext.getBean("preemptiveLock", PreemptiveLock.class);
    }
}
