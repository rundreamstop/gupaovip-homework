package com.gupaovip_homework.work20200223.simplefactory;


import com.gupaovip_homework.work20200223.AlipayImpl;
import com.gupaovip_homework.work20200223.IPayService;
import com.gupaovip_homework.work20200223.WechatPayImpl;

import java.util.UUID;

/**
 *
 * @Author: Ray Allen  @Time:2020/2/25 0025
 */
public class SimpleFactoryApp {

    public static void main(String[] args) {
        String ordrNo = UUID.randomUUID().toString();

        PayFactory payFactory = new PayFactory();
        IPayService alipay = payFactory.load(AlipayImpl.class);
        alipay.pay(ordrNo);
        alipay.notify(ordrNo);

        IPayService wechatpay = payFactory.load(WechatPayImpl.class);
        wechatpay.pay(ordrNo);
        wechatpay.notify(ordrNo);


    }

}
