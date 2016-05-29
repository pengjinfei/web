package com.pengjinfei.common.lock;

import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.stereotype.Service;

/**
 * Created by Pengjinfei on 16/5/29.
 * Description:
 */
public class LockAdvisor extends AbstractBeanFactoryPointcutAdvisor {

    private Pointcut pointcut = new AnnotationMatchingPointcut(Service.class,Lock.class);

    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }
}
