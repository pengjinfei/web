package com.pengjinfei.common.quartz;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * Created by Pengjinfei on 16/7/4.
 * Description:
 */
public class LocalSchedulerFactoryBean extends SchedulerFactoryBean implements BeanPostProcessor, BeanFactoryAware {
    private BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        //读取注解
        //MDCMethodInvokingJobDetailFactoryBean设置jobDetail
        //根据注解配置生成trigger
        //调度jobDetail和trigger
        return null;
    }
}
