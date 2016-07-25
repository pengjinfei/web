package com.pengjinfei.common.quartz;

import com.pengjinfei.common.web.utils.MDCUtils;
import org.quartz.*;
import org.quartz.impl.JobDetailImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.JobMethodInvocationFailedException;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.util.MethodInvoker;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by Pengjinfei on 16/7/25.
 * Description:
 */
public class MDCMethodInvokingJobDetailFactoryBean extends MethodInvokingJobDetailFactoryBean {

    @Override
    protected void postProcessJobDetail(JobDetail jobDetail) {
        super.postProcessJobDetail(jobDetail);
        if (jobDetail instanceof JobDetailImpl) {
            JobDetailImpl detail = (JobDetailImpl) jobDetail;
            Class<?> jobClass = detail.getJobClass();
            if (jobClass == MethodInvokingJob.class) {
                detail.setJobClass(MDCMethodInvokingJob.class);
            } else if (jobClass == StatefulMethodInvokingJob.class) {
                detail.setJobClass(MDCStatefulMethodInvokingJob.class);
            }
        }
    }

    private static class MDCMethodInvokingJob extends QuartzJobBean {

        protected static final Logger logger = LoggerFactory.getLogger(MDCMethodInvokingJob.class);

        private MethodInvoker methodInvoker;

        public void setMethodInvoker(MethodInvoker methodInvoker) {
            this.methodInvoker = methodInvoker;
        }

        @Override
        protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
            try {
                MDCUtils.set("Job:"+this.methodInvoker.getTargetClass() + "." + this.methodInvoker.getTargetMethod());
                long begin = System.currentTimeMillis();
                logger.debug("begin to execute...");
                context.setResult(this.methodInvoker.invoke());
                long end = System.currentTimeMillis();
                logger.debug("Execute complete in " + (end - begin) + " ms.");
            } catch (InvocationTargetException ex) {
                if (ex.getTargetException() instanceof JobExecutionException) {
                    // -> JobExecutionException, to be logged at info level by Quartz
                    throw (JobExecutionException) ex.getTargetException();
                } else {
                    // -> "unhandled exception", to be logged at error level by Quartz
                    throw new JobMethodInvocationFailedException(this.methodInvoker, ex.getTargetException());
                }
            } catch (Exception ex) {
                // -> "unhandled exception", to be logged at error level by Quartz
                throw new JobMethodInvocationFailedException(this.methodInvoker, ex);
            } finally {
                MDCUtils.clear();
            }
        }
    }

    @PersistJobDataAfterExecution
    @DisallowConcurrentExecution
    private static class MDCStatefulMethodInvokingJob extends MDCMethodInvokingJob {
        @Override
        protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
            super.executeInternal(context);
        }
    }

}
