package com.pengjinfei.common.quartz;

import com.pengjinfei.common.web.utils.MDCUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;

/**
 * Created by Pengjinfei on 16/7/28.
 * Description:
 */
public class LocalQuartzJobBean extends QuartzJobBean {

    protected Logger logger = LoggerFactory.getLogger(LocalQuartzJobBean.class);

    private String beanName;

    private String methodName;

    private String jobName;

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            Object object = getBean(context,beanName);
            Class<?> userClass = ClassUtils.getUserClass(object);
            Method declaredMethod = userClass.getDeclaredMethod(methodName);
            MDCUtils.set(jobName);
            long begin = System.currentTimeMillis();
            logger.debug("begin to execute...");
            context.setResult(declaredMethod.invoke(object));
            long end = System.currentTimeMillis();
            logger.debug("Execute complete in " + (end - begin) + " ms.");
        } catch (Exception e) {
            throw new JobExecutionException(e);
        } finally {
            MDCUtils.clear();
        }
    }

    protected Object getBean(JobExecutionContext context,String beanName) throws JobExecutionException {
        try {
            ApplicationContext applicationContext = (ApplicationContext) context.getScheduler().getContext().get(LocalSchedulerFactoryBean.APPLICATION_CONTEXT_KEY);
            return applicationContext.getBean(beanName);
        } catch (SchedulerException e) {
            throw new JobExecutionException(e);
        }
    }
}
