package com.pengjinfei.common.quartz;

import org.quartz.Scheduler;
import org.quartz.SimpleTrigger;

import java.lang.annotation.*;

/**
 * Created by Pengjinfei on 16/7/25.
 * Description:
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface TimerJob {

    String name() default "";

    String group() default Scheduler.DEFAULT_GROUP;

    int startDelay() default 0;

    int maxConcurrent() default Integer.MAX_VALUE;

    int misfireInstruction() default SimpleTrigger.MISFIRE_INSTRUCTION_SMART_POLICY;

    long repeatInterval() default 0L;

    int repeatCount() default SimpleTrigger.REPEAT_INDEFINITELY;

    String cronExpression() default "";
}