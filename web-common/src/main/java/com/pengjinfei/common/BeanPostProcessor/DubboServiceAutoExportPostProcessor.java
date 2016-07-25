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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Pengjinfei on 16/5/22.
 * Description:
 */
public class DubboServiceAutoExportPostProcessor implements BeanDefinitionRegistryPostProcessor {

    private static final List<Class<?>> IGNORED_INTERFACES = new ArrayList<>();

    private static Logger logger = LoggerFactory.getLogger(DubboServiceAutoExportPostProcessor.class);

    private static Map<Class<?>, String> cachedInterfaces = new HashMap<>();

    static {
        IGNORED_INTERFACES.add(InitializingBean.class);
        IGNORED_INTERFACES.add(ApplicationContextAware.class);
        IGNORED_INTERFACES.add(ApplicationListener.class);
        IGNORED_INTERFACES.add(DisposableBean.class);
        IGNORED_INTERFACES.add(BeanFactoryAware.class);
        IGNORED_INTERFACES.add(BeanNameAware.class);
        IGNORED_INTERFACES.add(Serializable.class);
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        for (String beanDefinitionName : registry.getBeanDefinitionNames()) {
            BeanDefinition beanDefinition = registry.getBeanDefinition(beanDefinitionName);
            String beanClassName = beanDefinition.getBeanClassName();
            try {
                Class<?> beanClass = Class.forName(beanClassName);
                Service service = beanClass.getAnnotation(Service.class);
                if (service == null) {
                    continue;
                }
                String value = service.value();
                if (value.equals("")) {
                    String simpleName = beanClass.getSimpleName();
                    value = simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
                }
                RootBeanDefinition newBeanDefinition = new RootBeanDefinition();
                newBeanDefinition.setBeanClass(ServiceBean.class);
                newBeanDefinition.setLazyInit(false);

                logger.debug("Find service annotation on class: " + beanClassName + ", value:" + value);
                RuntimeBeanReference reference = new RuntimeBeanReference(value);
                newBeanDefinition.getPropertyValues().addPropertyValue("ref", reference);

                Class<?>[] interfaces = beanClass.getInterfaces();
                List<Class<?>> foundInterfaces = new ArrayList<>();
                for (Class<?> aClass : interfaces) {
                    if (!IGNORED_INTERFACES.contains(aClass)) {
                        foundInterfaces.add(aClass);
                    }
                }
                if (foundInterfaces.isEmpty() || foundInterfaces.size() != 1) {
                    continue;
                }
                Class<?> foundInterface = foundInterfaces.get(0);
                newBeanDefinition.getPropertyValues().addPropertyValue("interface", foundInterface.getName());

                String id = value;
                int counter = 2;
                while (registry.containsBeanDefinition(id)) {
                    id = value + (counter++);
                }

                if (registry.containsBeanDefinition(id)) {
                    throw new IllegalStateException("Duplicate spring bean id " + id);
                }
/*                String cached = cachedInterfaces.get(foundInterface);
                if (cached == null) {
                    cachedInterfaces.put(foundInterface, id);
                } else {
                    newBeanDefinition.getPropertyValues().addPropertyValue("version",value);
                    BeanDefinition cachedBeanDefinition = registry.getBeanDefinition(cached);
                    MutablePropertyValues propertyValues = cachedBeanDefinition.getPropertyValues();
                    if (!propertyValues.contains("version")) {
                        String s = propertyValues.get("id").toString();
                        propertyValues.addPropertyValue("version",s.replaceAll("\\d*",""));
                    }
                }*/

                newBeanDefinition.getPropertyValues().addPropertyValue("version",value);
                registry.registerBeanDefinition(id, newBeanDefinition);
                newBeanDefinition.getPropertyValues().addPropertyValue("id", id);

            } catch (ClassNotFoundException e) {
                //ignore
            }
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }
}
