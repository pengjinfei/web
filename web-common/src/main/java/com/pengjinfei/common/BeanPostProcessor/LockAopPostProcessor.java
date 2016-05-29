package com.pengjinfei.common.BeanPostProcessor;

import com.pengjinfei.common.lock.LockAdvisor;
import com.pengjinfei.common.lock.LockAdvisorAutoProxyCreator;
import com.pengjinfei.common.lock.SeizeLockInterceptor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.Ordered;

/**
 * Created by Pengjinfei on 16/5/29.
 * Description:
 */
public class LockAopPostProcessor implements BeanDefinitionRegistryPostProcessor {
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        RootBeanDefinition creatorBeandefinition = new RootBeanDefinition(LockAdvisorAutoProxyCreator.class);
        creatorBeandefinition.getPropertyValues().add("order", Ordered.HIGHEST_PRECEDENCE);
        creatorBeandefinition.setRole(BeanDefinition.ROLE_APPLICATION);
        registry.registerBeanDefinition("lockAdvisorAutoProxyCreator",creatorBeandefinition);

        RootBeanDefinition interceptorDef = new RootBeanDefinition(SeizeLockInterceptor.class);
        interceptorDef.getPropertyValues().add("order", Ordered.HIGHEST_PRECEDENCE);
        interceptorDef.setRole(BeanDefinition.ROLE_APPLICATION);
        registry.registerBeanDefinition("lockInterceptor",interceptorDef);

        RootBeanDefinition advisorDef = new RootBeanDefinition(LockAdvisor.class);
        advisorDef.getPropertyValues().add("order", Ordered.HIGHEST_PRECEDENCE);
        advisorDef.setRole(BeanDefinition.ROLE_APPLICATION);
        advisorDef.getPropertyValues().add("adviceBeanName","lockInterceptor");
        registry.registerBeanDefinition("lockAdvisor",advisorDef);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }
}
