package com.gupaovip_homework.annotation;

import java.lang.annotation.*;

/**
 * @Author: Ray Allen  @Time:2020/4/5 0005
 */

@Target(ElementType.FIELD) //** 字段声明（包括枚举常量） */
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RunAutowired {
    String value() default "";
}
