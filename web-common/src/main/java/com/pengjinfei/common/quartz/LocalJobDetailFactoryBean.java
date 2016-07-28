package com.pengjinfei.common.quartz;

import org.quartz.JobDetail;
import org.quartz.impl.JobDetailImpl;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Pengjinfei on 16/7/28.
 * Description:
 */
public class LocalJobDetailFactoryBean implements FactoryBean<JobDetail>, InitializingBean {

    private JobDetail jobDetail;
    private String beanName;
    private String methodName;
    private String jobName;
    private int maxConcurrent = 0;
    private String name;
    private String group;
    private boolean jdbcStore = false;
    private String dataSourceBeanName;
    private String tablePrefix;
    private String schedulerName;

    public void setDataSourceBeanName(String dataSourceBeanName) {
        this.dataSourceBeanName = dataSourceBeanName;
    }

    public void setTablePrefix(String tablePrefix) {
        this.tablePrefix = tablePrefix;
    }

    public void setSchedulerName(String schedulerName) {
        this.schedulerName = schedulerName;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public void setMaxConcurrent(int maxConcurrent) {
        this.maxConcurrent = maxConcurrent;
    }

    public void setJdbcStore(boolean jdbcStore) {
        this.jdbcStore = jdbcStore;
    }

    @Override
    public JobDetail getObject() throws Exception {
        return jobDetail;
    }

    @Override
    public Class<?> getObjectType() {
        return JobDetail.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        JobDetailImpl detail = new JobDetailImpl();

        detail.setName(name);
        detail.setGroup(group);
        detail.setDurability(true);
        detail.getJobDataMap().put("beanName", beanName);
        detail.getJobDataMap().put("methodName", methodName);
        detail.getJobDataMap().put("jobName", jobName);

        if (maxConcurrent == 1) {
            detail.setJobClass(LocalStatefulQuartzJobBean.class);
        } else if (maxConcurrent == Integer.MAX_VALUE) {
            detail.setJobClass(LocalQuartzJobBean.class);
        } else if (maxConcurrent > 1) {
                detail.setJobClass(LocalConcurrentQuartzJobBean.class);
                detail.getJobDataMap().put("maxConcurrent", maxConcurrent);
                detail.getJobDataMap().put("concurrent", new AtomicInteger(0));
                detail.getJobDataMap().put("jdbcStore", jdbcStore);
                if (jdbcStore) {
                    detail.getJobDataMap().put("dataSourceBeanName", dataSourceBeanName);
                    detail.getJobDataMap().put("tablePrefix", tablePrefix);
                    detail.getJobDataMap().put("schedulerName", schedulerName);
                }
        }

        this.jobDetail = detail;
    }
}
