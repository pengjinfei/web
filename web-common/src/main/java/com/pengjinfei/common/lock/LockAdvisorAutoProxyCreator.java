package com.pengjinfei.common.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.autoproxy.AbstractAdvisorAutoProxyCreator;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.PriorityOrdered;

/**
 * Created by Pengjinfei on 16/5/29.
 * Description:
 */
public class LockAdvisorAutoProxyCreator extends AbstractAdvisorAutoProxyCreator implements PriorityOrdered {

    private static Logger logger = LoggerFactory.getLogger(LockAdvisorAutoProxyCreator.class);

    private ConfigurableListableBeanFactory beanFactory;

    @Override
    protected void initBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        super.initBeanFactory(beanFactory);
        this.beanFactory = beanFactory;
    }

    @Override
    protected boolean isEligibleAdvisorBean(String beanName) {
        return (this.beanFactory.containsBeanDefinition(beanName) &&
                this.beanFactory.getBeanDefinition(beanName).getRole() == BeanDefinition.ROLE_APPLICATION);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
