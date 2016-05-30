package com.pengjinfei.common.BeanPostProcessor;

import com.pengjinfei.common.lock.LockAdvisor;
import com.pengjinfei.common.lock.SeizeLockInterceptor;
import org.springframework.aop.config.AopConfigUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.*;

/**
 * Created by Pengjinfei on 16/5/29.
 * Description:
 */
public class LockAopPostProcessor implements BeanDefinitionRegistryPostProcessor {
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        BeanDefinition beanDefinition = registry.getBeanDefinition(AopConfigUtils.AUTO_PROXY_CREATOR_BEAN_NAME);
        if (beanDefinition != null) {
            AbstractBeanDefinition abstractBeanDefinition= (AbstractBeanDefinition) beanDefinition;
            MethodOverrides methodOverrides = abstractBeanDefinition.getMethodOverrides();
            ReplaceOverride replaceOverride = new ReplaceOverride("customizeProxyFactory", "customizeProxyFactoryMethodReplacer");
            methodOverrides.addOverride(replaceOverride);
        }

        RootBeanDefinition interceptorDef = new RootBeanDefinition(SeizeLockInterceptor.class);
        interceptorDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        registry.registerBeanDefinition("lockInterceptor",interceptorDef);

        RootBeanDefinition advisorDef = new RootBeanDefinition(LockAdvisor.class);
        advisorDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        advisorDef.getPropertyValues().add("adviceBeanName","lockInterceptor");
        registry.registerBeanDefinition("lockAdvisor",advisorDef);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }
}
