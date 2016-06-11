package com.pengjinfei.common.lock;

import java.lang.annotation.*;

/**
 * Created by Pengjinfei on 16/5/28.
 * Description:
 */
@Target({ElementType.METHOD,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Lock {
    String value() default "";
}
