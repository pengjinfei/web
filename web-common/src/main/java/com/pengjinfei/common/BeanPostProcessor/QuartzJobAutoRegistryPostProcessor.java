package com.pengjinfei.common.BeanPostProcessor;

import com.pengjinfei.common.quartz.MDCMethodInvokingJobDetailFactoryBean;
import com.pengjinfei.common.quartz.TimerJob;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * Created by Pengjinfei on 16/7/25.
 * Description:
 */
public class QuartzJobAutoRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {
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

                String finalValue = value;
                ReflectionUtils.doWithMethods(beanClass, new ReflectionUtils.MethodCallback() {
                    @Override
                    public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                        TimerJob timerJob = AnnotationUtils.findAnnotation(method, TimerJob.class);
                        if (timerJob != null) {
                            RootBeanDefinition beanDefinition = new RootBeanDefinition();
                            beanDefinition.setBeanClass(MDCMethodInvokingJobDetailFactoryBean.class);
                            beanDefinition.setLazyInit(false);

                            MutablePropertyValues propertyValues = beanDefinition.getPropertyValues();

                            RuntimeBeanReference reference = new RuntimeBeanReference(finalValue);
                            propertyValues.addPropertyValue("targetObject",reference);
                            propertyValues.addPropertyValue("targetMethod",method.getName());

                            String id=finalValue+"."+method.getName();
                            id += "(";
                            Class<?>[] parameterTypes = method.getParameterTypes();
                            int temp=0;
                            for (Class<?> parameterType : parameterTypes) {
                                if (temp++ != 0) {
                                    id += ",";
                                }
                                id += parameterType.getSimpleName();
                            }
                            id += ")";
                            registry.registerBeanDefinition(id, beanDefinition);
                        }
                    }
                });
            }catch (ClassNotFoundException e) {
               //ignore
            }
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }
}
