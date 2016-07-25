package com.pengjinfei.common.quartz;

import com.pengjinfei.common.web.utils.MDCUtils;
import org.quartz.*;
import org.quartz.impl.JobDetailImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.JobMethodInvocationFailedException;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.util.ClassUtils;
import org.springframework.util.MethodInvoker;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

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
                detail.setJobClass(MDCMethodInvokingJob.class);
            } else if (jobClass == StatefulMethodInvokingJob.class) {
                detail.setJobClass(MDCStatefulMethodInvokingJob.class);
            }
        }

    }

    public static class MDCMethodInvokingJob extends QuartzJobBean {

        protected static final Logger logger = LoggerFactory.getLogger(MDCMethodInvokingJob.class);

        private static ConcurrentHashMap<MethodInvoker, AtomicInteger> cachedConcurrent = new ConcurrentHashMap<>();

        private MethodInvoker methodInvoker;
        private int maxConcurrent = 0;

        public void setMethodInvoker(MethodInvoker methodInvoker) {
            this.methodInvoker = methodInvoker;
            setMaxConcurrentByCondition();
        }

        public void setMaxConcurrent(int maxConcurrent) {
            this.maxConcurrent = maxConcurrent;
            setMaxConcurrentByCondition();
        }

        private void setMaxConcurrentByCondition() {
            if (methodInvoker != null && maxConcurrent != 0) {
                if (maxConcurrent != Integer.MAX_VALUE) {
                    cachedConcurrent.putIfAbsent(methodInvoker, new AtomicInteger(0));
                }
            }
        }

        @Override
        protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
            try {
                if (maxConcurrent != Integer.MAX_VALUE) {
                    AtomicInteger before = cachedConcurrent.get(methodInvoker);
                    int after = before.incrementAndGet();
                    if (after > maxConcurrent) {
                        logger.info("Concurrence reaches max value:" + maxConcurrent + ", job quit.");
                        return;
                    }
                }
                Class<?> userClass = ClassUtils.getUserClass(this.methodInvoker.getTargetClass());
                MDCUtils.set("Job:" + userClass.getName() + "." + this.methodInvoker.getTargetMethod());
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
                if (maxConcurrent != Integer.MAX_VALUE) {
                    AtomicInteger before = cachedConcurrent.get(methodInvoker);
                    before.decrementAndGet();
                }
                MDCUtils.clear();
            }
        }
    }

    @PersistJobDataAfterExecution
    @DisallowConcurrentExecution
    public static class MDCStatefulMethodInvokingJob extends MDCMethodInvokingJob {
        @Override
        protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
            super.executeInternal(context);
        }
    }

}
