package com.gupaovip_homework.v3.beans;

import lombok.Data;

/**
 * @Author: Ray Allen  @Time:2020/4/6 0006
 */

@Data
public class RunBeanWrapper {

    private Object wrapperInstance;

    private Class<?> wrapperClass;

    public RunBeanWrapper(Object instance) {
        this.wrapperInstance = instance;
        this.wrapperClass = instance.getClass();
    }
}
