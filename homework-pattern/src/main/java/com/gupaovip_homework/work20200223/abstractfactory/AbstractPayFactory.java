package com.gupaovip_homework.work20200223.abstractfactory;

/**
 *
 * @Author: Ray Allen  @Time:2020/2/25 0025
 */
public abstract class AbstractPayFactory {

    public void init() {
        System.out.println("支付相关初始化方法...");
    }

    protected abstract String pay(String orderNo);

    protected abstract String notify(String orderNo);

//    public abstract  String getT();
}
