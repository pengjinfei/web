package com.pengjinfei.common.quartz;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.util.MethodInvoker;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Pengjinfei on 16/7/26.
 * Description:
 */
public class ConcurrentMDCMethodInvokingJob extends MDCMethodInvokingJob {

    private static ConcurrentHashMap<MethodInvoker, AtomicInteger> cachedConcurrent = new ConcurrentHashMap<>();

    private int maxConcurrent = 0;

    public void setMethodInvoker(MethodInvoker methodInvoker) {
        super.setMethodInvoker(methodInvoker);
        setMaxConcurrentByCondition();
    }

    public void setMaxConcurrent(int maxConcurrent) {
        this.maxConcurrent = maxConcurrent;
        setMaxConcurrentByCondition();
    }

    private void setMaxConcurrentByCondition() {
        if (getMethodInvoker() != null && maxConcurrent != 0) {
            if (maxConcurrent != Integer.MAX_VALUE) {
                cachedConcurrent.putIfAbsent(getMethodInvoker(), new AtomicInteger(0));
            }
        }
    }

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        if (maxConcurrent != Integer.MAX_VALUE) {
            AtomicInteger before = cachedConcurrent.get(getMethodInvoker());
            int after = before.incrementAndGet();
            if (after > maxConcurrent) {
                before.decrementAndGet();
                logger.info("Concurrence reaches max value:" + maxConcurrent + ", job:"+getJobName()+" quit.");
                return;
            }
        }
        super.executeInternal(context);
        if (maxConcurrent != Integer.MAX_VALUE) {
            AtomicInteger before = cachedConcurrent.get(getMethodInvoker());
            before.decrementAndGet();
        }
    }
}
