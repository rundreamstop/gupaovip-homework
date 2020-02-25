package com.gupaovip_homework.work20200223;

/**
 *
 * @Author: Ray Allen  @Time:2020/2/25 0025
 */

public class AlipayImpl implements IPayService {
    @Override
    public String pay(String orderNo) {
        System.out.println("调用支付宝支付...");
        return "调用支付宝支付";
    }

    @Override
    public String notify(String orderNo) {
        System.out.println("支付宝异步通知...");
        return "支付宝异步通知";
    }
}
