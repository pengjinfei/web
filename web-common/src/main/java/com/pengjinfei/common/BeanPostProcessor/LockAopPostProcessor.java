package com.pengjinfei.common.BeanPostProcessor;

import com.pengjinfei.common.lock.LockAdvisor;
import com.pengjinfei.common.lock.LockAttribute;
import com.pengjinfei.common.lock.SeizeLockInterceptor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Created by Pengjinfei on 16/5/29.
 * Description:
 */
public class LockAopPostProcessor implements BeanDefinitionRegistryPostProcessor {
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
/*        BeanDefinition beanDefinition = registry.getBeanDefinition(AopConfigUtils.AUTO_PROXY_CREATOR_BEAN_NAME);
        if (beanDefinition != null) {
            AbstractBeanDefinition abstractBeanDefinition= (AbstractBeanDefinition) beanDefinition;
            MethodOverrides methodOverrides = abstractBeanDefinition.getMethodOverrides();
            ReplaceOverride replaceOverride = new ReplaceOverride("customizeProxyFactory", "customizeProxyFactoryMethodReplacer");
            methodOverrides.addOverride(replaceOverride);
        }*/

        RootBeanDefinition attributeDef = new RootBeanDefinition(LockAttribute.class);
        attributeDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        registry.registerBeanDefinition("lockAttribute",attributeDef);

        RootBeanDefinition interceptorDef = new RootBeanDefinition(SeizeLockInterceptor.class);
        interceptorDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        interceptorDef.getPropertyValues().add("lockAttribute", new RuntimeBeanReference("lockAttribute"));
        registry.registerBeanDefinition("lockInterceptor",interceptorDef);

        RootBeanDefinition advisorDef = new RootBeanDefinition(LockAdvisor.class);
        advisorDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        advisorDef.getPropertyValues().add("adviceBeanName","lockInterceptor");
        advisorDef.getPropertyValues().add("lockAttribute", new RuntimeBeanReference("lockAttribute"));
        registry.registerBeanDefinition("lockAdvisor",advisorDef);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }
}
