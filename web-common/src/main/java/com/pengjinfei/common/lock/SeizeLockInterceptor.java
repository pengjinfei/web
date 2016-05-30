package com.pengjinfei.common.lock;

import com.pengjinfei.common.lock.impl.ZookeeperPreemptiveLock;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * Created by Pengjinfei on 16/5/28.
 * Description:
 */
public class SeizeLockInterceptor implements MethodInterceptor {

    private PreemptiveLock preemptiveLock = new ZookeeperPreemptiveLock();

    private LockAttribute lockAttribute;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object retVal = null;
        try {
            String lockPath = lockAttribute.getLockPath();
            if (preemptiveLock.getLock(lockPath)) {
                retVal = invocation.proceed();
            }
        } finally {
            preemptiveLock.releaseLock();
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
