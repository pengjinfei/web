package com.pengjinfei.common.BeanPostProcessor;

import com.pengjinfei.common.quartz.MDCMethodInvokingJobDetailFactoryBean;
import com.pengjinfei.common.quartz.PersistableCronTriggerFactoryBean;
import com.pengjinfei.common.quartz.PersistableSimpleTriggerFactoryBean;
import com.pengjinfei.common.quartz.TimerJob;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Calendar;

/**
 * Created by Pengjinfei on 16/7/25.
 * Description:
 */
public class QuartzJobAutoRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {

        ManagedList<RuntimeBeanReference> triggersRefList = new ManagedList<>();
        for (String jobDetailBeanDefName : registry.getBeanDefinitionNames()) {
            BeanDefinition jobDetailBeanDef = registry.getBeanDefinition(jobDetailBeanDefName);
            String beanClassName = jobDetailBeanDef.getBeanClassName();
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
                            RootBeanDefinition jobDetailBeanDef = new RootBeanDefinition();
                            jobDetailBeanDef.setBeanClass(MDCMethodInvokingJobDetailFactoryBean.class);
                            jobDetailBeanDef.setLazyInit(false);

                            MutablePropertyValues jobDetailPV = jobDetailBeanDef.getPropertyValues();

                            RuntimeBeanReference serviceRef = new RuntimeBeanReference(finalValue);
                            jobDetailPV.addPropertyValue("targetObject", serviceRef);
                            jobDetailPV.addPropertyValue("targetMethod", method.getName());
                            jobDetailPV.addPropertyValue("maxConcurrent",timerJob.maxConcurrent());

                            String id = finalValue + "." + method.getName();
                            id += "(";
                            Class<?>[] parameterTypes = method.getParameterTypes();
                            int temp = 0;
                            for (Class<?> parameterType : parameterTypes) {
                                if (temp++ != 0) {
                                    id += ",";
                                }
                                id += parameterType.getSimpleName();
                            }
                            id += ")";
                            String jobDetailBeanId = "job:" + id;
                            registry.registerBeanDefinition(jobDetailBeanId, jobDetailBeanDef);

                            RootBeanDefinition triggerBeanDef = new RootBeanDefinition();
                            RuntimeBeanReference jobDetailRef = new RuntimeBeanReference(jobDetailBeanId);
                            MutablePropertyValues triggerPV = triggerBeanDef.getPropertyValues();
                            triggerPV.addPropertyValue("jobDetail", jobDetailRef);
                            if (StringUtils.hasText(timerJob.name())) {
                                triggerPV.addPropertyValue("name", timerJob.name());
                            }
                            triggerPV.addPropertyValue("group", timerJob.group());
                            triggerPV.addPropertyValue("misfireInstruction", timerJob.misfireInstruction());

                            int startDelay = timerJob.startDelay() * 1000;
                            if (startDelay > 0) {
                                Calendar startTime = Calendar.getInstance();
                                startTime.add(Calendar.MILLISECOND, startDelay);
                                triggerPV.addPropertyValue("startTime", startTime.getTime());
                            }

                            String cronExpression = timerJob.cronExpression();
                            if (!StringUtils.hasText(cronExpression)) {
                                triggerBeanDef.setBeanClass(PersistableSimpleTriggerFactoryBean.class);
                                triggerPV.addPropertyValue("repeatCount", timerJob.repeatCount());
                                long repeatInterval = timerJob.repeatInterval()*1000;
                                if (repeatInterval > 0) {
                                    triggerPV.addPropertyValue("repeatInterval", repeatInterval);
                                }
                            } else {
                                triggerBeanDef.setBeanClass(PersistableCronTriggerFactoryBean.class);
                                triggerPV.addPropertyValue("cronExpression",timerJob.cronExpression());
                            }

                            String triggerBeanId="trigger:"+id;
                            registry.registerBeanDefinition(triggerBeanId,triggerBeanDef);

                            triggersRefList.add(new RuntimeBeanReference(triggerBeanId));
                        }
                    }
                });
            } catch (ClassNotFoundException e) {
                //ignore
            }
        }

        BeanDefinition scheduler = registry.getBeanDefinition("scheduler");
        if (!CollectionUtils.isEmpty(triggersRefList)) {
            scheduler.getPropertyValues().addPropertyValue("triggers", triggersRefList);
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }
}
