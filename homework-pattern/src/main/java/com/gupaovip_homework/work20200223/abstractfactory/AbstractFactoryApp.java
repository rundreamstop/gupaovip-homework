package com.gupaovip_homework.work20200223.abstractfactory;

import java.util.UUID;

/**
 * @Author: Ray Allen  @Time:2020/2/25 0025
 */
public class AbstractFactoryApp {

    public static void main(String[] args) {
        String ordrNo = UUID.randomUUID().toString();
        AlipayFactory alipayFactory =new AlipayFactory();
        alipayFactory.pay(ordrNo);
        alipayFactory.notify(ordrNo);
    }
}
