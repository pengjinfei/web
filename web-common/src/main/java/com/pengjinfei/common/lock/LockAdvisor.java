package com.pengjinfei.common.lock;

import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;

/**
 * Created by Pengjinfei on 16/5/29.
 * Description:
 */
public class LockAdvisor extends AbstractBeanFactoryPointcutAdvisor implements Ordered{

    private LockAttribute lockAttribute;

    private Pointcut pointcut = new LockAnnotationMatchingPointcut() {
        @Override
        public LockAttribute getLockAttribute() {
            return lockAttribute;
        }
    };

    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    public LockAttribute getLockAttribute() {
        return lockAttribute;
    }

    public void setLockAttribute(LockAttribute lockAttribute) {
        this.lockAttribute = lockAttribute;
    }

    @Override
    @Value("lockInterceptor")
    public void setAdviceBeanName(String adviceBeanName) {
        super.setAdviceBeanName(adviceBeanName);
    }
}
