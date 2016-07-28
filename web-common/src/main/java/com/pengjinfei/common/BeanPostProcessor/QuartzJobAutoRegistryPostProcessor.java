package com.pengjinfei.common.BeanPostProcessor;

import com.pengjinfei.common.quartz.LocalJobDetailFactoryBean;
import com.pengjinfei.common.quartz.PersistableCronTriggerFactoryBean;
import com.pengjinfei.common.quartz.PersistableSimpleTriggerFactoryBean;
import com.pengjinfei.common.quartz.TimerJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.*;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Properties;

import static org.quartz.impl.jdbcjobstore.Constants.DEFAULT_TABLE_PREFIX;

/**
 * Created by Pengjinfei on 16/7/25.
 * Description:
 */
public class QuartzJobAutoRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {

    private static final String RAM_JOBSTORE = "org.quartz.simpl.RAMJobStore";
    private static final String JDBC_JOBSTORE = "org.quartz.impl.jdbcjobstore.JobStoreTX";
    private static final String JOBSTORE_CLASS = "org.quartz.jobStore.class";
    private static final String TABLE_PREFIX = "org.quartz.jobStore.tablePrefix";
    private static Logger logger = LoggerFactory.getLogger(QuartzJobAutoRegistryPostProcessor.class);

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {

        BeanDefinition scheduler = registry.getBeanDefinition("scheduler");
        MutablePropertyValues schedulerPropertyValues = scheduler.getPropertyValues();

        boolean isJdbcStore = false;

        //configLocation
        Object configLocationDef = schedulerPropertyValues.get("configLocation");
        Assert.notNull(configLocationDef, "ConfigLocation of quartz schedulerFactory must not be null.");
        String configLocation = ((TypedStringValue) configLocationDef).getValue();
        Properties properties;
        try {
            ResourceLoader loader = new DefaultResourceLoader();
            Resource resource = loader.getResource(configLocation);
            properties = PropertiesLoaderUtils.loadProperties(resource);
            String jobStoreClass = properties.getProperty(JOBSTORE_CLASS);
            if (JDBC_JOBSTORE.equals(jobStoreClass)) {
                isJdbcStore = true;
            } else if (!RAM_JOBSTORE.equals(jobStoreClass)) {
                throw new IllegalStateException("org.quartz.jobStore.class [" + jobStoreClass + "] is not supported.");
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } catch (Exception e) {
            throw new BeanDefinitionValidationException(e.getMessage(),e);
        }

        String dataSourceBeanName = null;
        String tablePrefix=DEFAULT_TABLE_PREFIX;
        String schedulerName=null;
        if (isJdbcStore) {
            //dataSource
            Object dataSourceDef = schedulerPropertyValues.get("dataSource");
            Assert.notNull(dataSourceDef, "DataSource of quartz schedulerFactory must not be null when jdbcStore applied.");
            RuntimeBeanReference dataSourceRef = (RuntimeBeanReference) dataSourceDef;
            dataSourceBeanName = dataSourceRef.getBeanName();

            //tablePrefix
            String tablePrefixeInProperty = properties.getProperty(TABLE_PREFIX);
            if (StringUtils.hasText(tablePrefixeInProperty)) {
                tablePrefix = tablePrefixeInProperty;
            }

            //schedulerName
            Object schedulerNameDef = schedulerPropertyValues.get("schedulerName");
            Assert.notNull(schedulerNameDef, "SchedulerName of quartz schedulerFactory must not be null.");
            schedulerName = ((TypedStringValue) schedulerNameDef).getValue();
        }

        ManagedList<RuntimeBeanReference> triggersRefList = new ManagedList<>();
        ManagedList<RuntimeBeanReference> jobsRefList = new ManagedList<>();
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
                boolean finalIsJdbcStore = isJdbcStore;
                String finalDataSourceBeanName = dataSourceBeanName;
                String finalTablePrefix = tablePrefix;
                String finalSchedulerName = schedulerName;
                ReflectionUtils.doWithMethods(beanClass, new ReflectionUtils.MethodCallback() {
                    @Override
                    public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                        TimerJob timerJob = AnnotationUtils.findAnnotation(method, TimerJob.class);
                        if (timerJob != null) {
                            RootBeanDefinition jobDetailBeanDef = new RootBeanDefinition();
                            jobDetailBeanDef.setBeanClass(LocalJobDetailFactoryBean.class);
                            jobDetailBeanDef.setLazyInit(false);

                            MutablePropertyValues jobDetailPV = jobDetailBeanDef.getPropertyValues();

                            jobDetailPV.addPropertyValue("beanName", finalValue);
                            jobDetailPV.addPropertyValue("methodName", method.getName());
                            jobDetailPV.addPropertyValue("maxConcurrent", timerJob.maxConcurrent());
                            jobDetailPV.addPropertyValue("jdbcStore", finalIsJdbcStore);
                            if (finalIsJdbcStore) {
                                jobDetailPV.addPropertyValue("dataSourceBeanName", finalDataSourceBeanName);
                                jobDetailPV.addPropertyValue("tablePrefix", finalTablePrefix);
                                jobDetailPV.addPropertyValue("schedulerName", finalSchedulerName);
                            }
                            String name = timerJob.name();
                            String id = finalValue + "." + method.getName();
                            if (!StringUtils.hasText(name)) {
                                name = id;
                            }
                            jobDetailPV.addPropertyValue("name", name);
                            jobDetailPV.addPropertyValue("jobName", name);
                            jobDetailPV.addPropertyValue("group", timerJob.group());

                            String jobDetailBeanId = "job:" + id;
                            registry.registerBeanDefinition(jobDetailBeanId, jobDetailBeanDef);

                            RootBeanDefinition triggerBeanDef = new RootBeanDefinition();
                            RuntimeBeanReference jobDetailRef = new RuntimeBeanReference(jobDetailBeanId);
                            MutablePropertyValues triggerPV = triggerBeanDef.getPropertyValues();
                            triggerPV.addPropertyValue("jobDetail", jobDetailRef);
                            triggerPV.addPropertyValue("name", name);
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
                                long repeatInterval = timerJob.repeatInterval() * 1000;
                                if (repeatInterval > 0) {
                                    triggerPV.addPropertyValue("repeatInterval", repeatInterval);
                                }
                            } else {
                                triggerBeanDef.setBeanClass(PersistableCronTriggerFactoryBean.class);
                                triggerPV.addPropertyValue("cronExpression", timerJob.cronExpression());
                            }

                            String triggerBeanId = "trigger:" + id;
                            registry.registerBeanDefinition(triggerBeanId, triggerBeanDef);

                            triggersRefList.add(new RuntimeBeanReference(triggerBeanId));
                            jobsRefList.add(new RuntimeBeanReference(jobDetailBeanId));
                        }
                    }
                });
            } catch (ClassNotFoundException e) {
                //ignore
            }
        }

        if (!CollectionUtils.isEmpty(triggersRefList)) {
            schedulerPropertyValues.addPropertyValue("triggers", triggersRefList);
            schedulerPropertyValues.addPropertyValue("jobDetails", jobsRefList);
        }

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }
}
