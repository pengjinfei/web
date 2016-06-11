package com.pengjinfei.common.lock;

import com.pengjinfei.common.lock.impl.ZookeeperPreemptiveLock;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;

/**
 * Created by Pengjinfei on 16/5/28.
 * Description:
 */
public class SeizeLockInterceptor implements MethodInterceptor {

    private static Logger logger = LoggerFactory.getLogger(SeizeLockInterceptor.class);

    private LockAttribute lockAttribute;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Class<?> targetClass = (invocation.getThis() != null ? AopUtils.getTargetClass(invocation.getThis()) : null);
        Object retVal = null;
        PreemptiveLock preemptiveLock = null;
        try {
            String lockPath = getLockAttribute().getLockPath(invocation.getMethod(), targetClass);
            logger.info("Begin seize lock on papth:" + lockPath);
            preemptiveLock = new ZookeeperPreemptiveLock();
            if (preemptiveLock.getLock(lockPath)) {
                retVal = invocation.proceed();
            }
        } finally {
            if (preemptiveLock != null) {
                preemptiveLock.releaseLock();
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
}
