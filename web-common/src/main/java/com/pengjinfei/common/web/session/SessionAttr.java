package com.pengjinfei.common.web.session;

import java.lang.annotation.*;

/**
 * Author: EX-PENGJINFEI001
 * DATE: 2016-02-17
 * Description: 用于springmvc在session中取出参数
 */
@Target({ ElementType.PARAMETER, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SessionAttr {

    String value();

    boolean newable() default false;

    boolean required() default true;
}
