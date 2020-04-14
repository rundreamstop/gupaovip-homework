package com.gupaovip_homework.demo.aspect;


/**
 * @Author: Ray Allen  @Time:2020/4/14 0014
 */

public class LogAspect {


    public void before() {
        System.out.println("Invoker before method");
    }

    public void after() {
        System.out.println("Invoker after method");
    }
}
