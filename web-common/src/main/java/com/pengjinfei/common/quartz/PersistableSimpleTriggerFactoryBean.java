package com.pengjinfei.common.quartz;

import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

/**
 * Created by Pengjinfei on 16/7/27.
 * Description:
 */
public class PersistableSimpleTriggerFactoryBean extends SimpleTriggerFactoryBean {
    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        getJobDataMap().remove("jobDetail");
    }
}
