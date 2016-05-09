package com.pengjinfei.common.BeanPostProcessor;

import com.alibaba.dubbo.config.spring.ServiceBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: EX-PENGJINFEI001
 * Date: 2016-05-09
 * Description:
 */
public class BeanDefinitionAddPostProcessor implements BeanDefinitionRegistryPostProcessor {

    private static final List<Class<?>> ignoredInterfaces = new ArrayList<>();

    private static final Logger logger = LoggerFactory.getLogger(BeanDefinitionAddPostProcessor.class);

    static {
        ignoredInterfaces.add(InitializingBean.class);
        ignoredInterfaces.add(ApplicationContextAware.class);
        ignoredInterfaces.add(ApplicationListener.class);
        ignoredInterfaces.add(DisposableBean.class);
        ignoredInterfaces.add(BeanFactoryAware.class);
        ignoredInterfaces.add(BeanNameAware.class);
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        String[] beanDefinitionNames = registry.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            BeanDefinition existBeanDefinition = registry.getBeanDefinition(beanDefinitionName);
            String className = existBeanDefinition.getBeanClassName();

            Class<?> beanClass = null;
            try {
                beanClass = Class.forName(className);
            } catch (ClassNotFoundException e) {
                logger.error(className + "load failed.", e);
            }
            assert beanClass != null;
            Service service = beanClass.getAnnotation(Service.class);
            if (service == null) {
                continue;
            }
            Class<?>[] interfaces = beanClass.getInterfaces();
            List<Class<?>> foundInterfaces = new ArrayList<>();
            for (Class<?> aClass : interfaces) {
                if (!ignoredInterfaces.contains(aClass)) {
                    foundInterfaces.add(aClass);
                }
            }
            if (foundInterfaces.isEmpty() || foundInterfaces.size() != 1) {
                continue;
            }
            Class<?> exposeInterface = foundInterfaces.get(0);
            RootBeanDefinition beanDefinition = new RootBeanDefinition();
            beanDefinition.setBeanClass(ServiceBean.class);
            beanDefinition.setLazyInit(false);
            String generatedBeanName = exposeInterface.getCanonicalName();
            if (generatedBeanName == null || generatedBeanName.length() == 0) {
                generatedBeanName = ServiceBean.class.getName();
            }
            String id = generatedBeanName;
            int counter = 2;
            while (registry.containsBeanDefinition(id)) {
                id = generatedBeanName + (counter++);
            }
            if (id != null && id.length() > 0) {
                if (registry.containsBeanDefinition(id)) {
                    throw new IllegalStateException("Duplicate spring bean id " + id);
                }
                registry.registerBeanDefinition(id, beanDefinition);
                beanDefinition.getPropertyValues().addPropertyValue("id", id);
            }

            String refId = service.value();
            if (refId.equals("")) {
                String simpleName = beanClass.getSimpleName();
                refId = simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
            }

            RuntimeBeanReference reference = new RuntimeBeanReference(refId);
            beanDefinition.getPropertyValues().addPropertyValue("ref", reference);

            beanDefinition.getPropertyValues().addPropertyValue("interface",exposeInterface.getName());

        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }
}
