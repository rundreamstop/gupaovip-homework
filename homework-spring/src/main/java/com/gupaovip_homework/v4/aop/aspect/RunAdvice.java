package com.gupaovip_homework.v4.aop.aspect;

import lombok.Data;

import java.lang.reflect.Method;

/**
 * @Author: Ray Allen  @Time:2020/4/14 0014
 */

@Data
public class RunAdvice {

    private Object aspect;

    private Method adviceMethod;

    private String throwName;

    public RunAdvice(Object aspect, Method adviceMethod, String throwName) {
        this.aspect = aspect;
        this.adviceMethod = adviceMethod;
        this.throwName = throwName;
    }

    public RunAdvice(Object aspect, Method advice) {
        this.aspect = aspect;
        this.adviceMethod = advice;
    }
}
