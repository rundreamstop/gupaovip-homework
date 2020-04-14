package com.gupaovip_homework.v4.webmvc.servlet;

import lombok.Data;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * @Author: Ray Allen  @Time:2020/4/10 0010
 */

@Data
public class RunHandlerMapping {

    private Pattern pattern; // url

    private Method method; //方法

    private Object controller; // method对应的实例对象

    public RunHandlerMapping(Pattern pattern, Object controller, Method method) {
        this.pattern = pattern;
        this.method = method;
        this.controller = controller;
    }
}
