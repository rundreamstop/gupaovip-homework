package com.gupaovip_homework.work20200223;

/**
 *
 * @Author: Ray Allen  @Time:2020/2/25 0025
 */
public class WechatPayImpl implements IPayService {
    @Override
    public String pay(String orderNo) {
        System.out.println("调用微信支付...");
        return "调用微信支付";
    }

    @Override
    public String notify(String orderNo) {
        System.out.println("微信支付异步通知...");
        return "微信支付异步通知";
    }
}
