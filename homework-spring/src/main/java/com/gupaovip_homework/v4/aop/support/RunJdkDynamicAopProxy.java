package com.gupaovip_homework.v4.aop.support;

import com.gupaovip_homework.v4.aop.RunAdviseSupport;
import com.gupaovip_homework.v4.aop.aspect.RunAdvice;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * jdk 动态代理
 *
 * @Author: Ray Allen  @Time:2020/4/14 0014
 */

public class RunJdkDynamicAopProxy implements InvocationHandler {

    private RunAdviseSupport config;

    public RunJdkDynamicAopProxy(RunAdviseSupport config) {
        this.config = config;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        Map<String, RunAdvice> advices = config.getAdvices(method, null);
        Object returnValues = null;

        try {

            invokeAdivce(advices.get("before"));

            returnValues = method.invoke(this.config.getTarget(), args);

            invokeAdivce(advices.get("after"));
        } catch (Exception e) {
            invokeAdivce(advices.get("afterThrowing"));
            throw e;
        }

        return returnValues;
    }

    private void invokeAdivce(RunAdvice advice) {
        try {
            advice.getAdviceMethod().invoke(advice.getAspect());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    public Object getProxy() {
        return Proxy.newProxyInstance(this.getClass().getClassLoader(),
                this.config.getTargetClass().getInterfaces(), this);
    }
}
