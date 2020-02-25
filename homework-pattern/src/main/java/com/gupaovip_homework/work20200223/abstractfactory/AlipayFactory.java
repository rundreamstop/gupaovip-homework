package com.gupaovip_homework.work20200223.abstractfactory;

/**
 * @Author: Ray Allen  @Time:2020/2/25 0025
 */
public class AlipayFactory extends AbstractPayFactory {

    @Override
    protected String pay(String orderNo) {
        this.init();
        System.out.println("支付宝支付...");
        return "进行支付宝支付";
    }

    @Override
    protected String notify(String orderNo) {
        this.init();
        System.out.println("支付宝支付异步通知...");
        return "支付宝支付异步通知";
    }

}
