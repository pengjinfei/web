package com.pengjinfei.common.quartz;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Pengjinfei on 16/7/25.
 * Description:
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface TimerJob {

    String name() default "";

    String group() default "";

    String cronExpression() default "";

    int startDelay() default 0;

    String repeatInterval() default "";

    boolean disabled() default false;

    int maxConcurrent() default 0;

}