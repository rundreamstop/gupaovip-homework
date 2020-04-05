package com.gupaovip_homework.annotation;

import java.lang.annotation.*;

/**
 * @Author: Ray Allen  @Time:2020/4/5 0005
 */

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RunRequestMapping {
    String value() default "";
}
