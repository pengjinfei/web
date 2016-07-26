package com.pengjinfei.common.quartz;

import com.pengjinfei.common.web.utils.MDCUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Trigger;
import org.quartz.impl.triggers.AbstractTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.JobMethodInvocationFailedException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.util.MethodInvoker;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by Pengjinfei on 16/7/26.
 * Description:
 */
public class MDCMethodInvokingJob extends QuartzJobBean {

    protected static final Logger logger = LoggerFactory.getLogger(MDCMethodInvokingJob.class);

    private MethodInvoker methodInvoker;

    private String jobName;

    public void setMethodInvoker(MethodInvoker methodInvoker) {
        this.methodInvoker = methodInvoker;
    }

    public MethodInvoker getMethodInvoker() {
        return methodInvoker;
    }

    public String getJobName() {
        return jobName;
    }

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            Trigger trigger = context.getTrigger();
            if (trigger instanceof AbstractTrigger) {
                AbstractTrigger abstractTrigger = (AbstractTrigger) trigger;
                jobName=abstractTrigger.getName();
            }
            MDCUtils.set(jobName);
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
