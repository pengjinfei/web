package com.pengjinfei.common.quartz;

import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * Created by Pengjinfei on 16/7/28.
 * Description:
 */
public class LocalSchedulerFactoryBean extends SchedulerFactoryBean {

    static final String APPLICATION_CONTEXT_KEY = "applicationContextKey";

    @Override
    public void afterPropertiesSet() throws Exception {
        super.setApplicationContextSchedulerContextKey(APPLICATION_CONTEXT_KEY);
        super.afterPropertiesSet();
    }
}
