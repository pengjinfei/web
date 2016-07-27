package com.pengjinfei.common.quartz;

import org.springframework.scheduling.quartz.CronTriggerFactoryBean;

import java.text.ParseException;

/**
 * Created by Pengjinfei on 16/7/27.
 * Description:
 */
public class PersistableCronTriggerFactoryBean extends CronTriggerFactoryBean {
    @Override
    public void afterPropertiesSet() throws ParseException {
        super.afterPropertiesSet();
        // Remove the JobDetail element
        getJobDataMap().remove("jobDetail");
    }
}
