package com.pengjinfei.common.BeanPostProcessor;

import com.pengjinfei.common.annotation.ServiceProxy;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Author: EX-PENGJINFEI001
 * DATE: 2016-02-17
 * Description:
 */
public class ServiceProxyBeanPostProcessor implements BeanPostProcessor, Ordered,
        ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        Service service = beanClass.getAnnotation(Service.class);
        if (service != null) {
            //TODO 将接口注册到zookeeper
            return bean;
        }
        Field[] fields = beanClass.getDeclaredFields();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            ServiceProxy serviceProxy = field.getAnnotation(ServiceProxy.class);
            //TODO 给field赋值代理对象
        }
        return bean;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
