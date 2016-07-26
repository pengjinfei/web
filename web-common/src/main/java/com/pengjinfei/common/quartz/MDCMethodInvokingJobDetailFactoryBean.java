package com.pengjinfei.common.quartz;

import org.quartz.*;
import org.quartz.impl.JobDetailImpl;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;

/**
 * Created by Pengjinfei on 16/7/25.
 * Description:
 */
public class MDCMethodInvokingJobDetailFactoryBean extends MethodInvokingJobDetailFactoryBean {

    private int maxConcurrent;

    public void setMaxConcurrent(int maxConcurrent) {
        this.maxConcurrent = maxConcurrent;
    }

    @Override
    protected void postProcessJobDetail(JobDetail jobDetail) {
        jobDetail.getJobDataMap().put("maxConcurrent", maxConcurrent);

        super.postProcessJobDetail(jobDetail);
        if (jobDetail instanceof JobDetailImpl) {
            JobDetailImpl detail = (JobDetailImpl) jobDetail;
            Class<?> jobClass = detail.getJobClass();
            if (jobClass == MethodInvokingJob.class) {
                if (maxConcurrent <= 1) {
                    detail.setJobClass(MDCStatefulMethodInvokingJob.class);
                }else {
                    detail.setJobClass(ConcurrentMDCMethodInvokingJob.class);
                }
            } else if (jobClass == StatefulMethodInvokingJob.class) {
                detail.setJobClass(MDCStatefulMethodInvokingJob.class);
            }
        }

    }
}
