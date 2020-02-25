package com.gupaovip_homework.work20200223.factorymethod;


import com.gupaovip_homework.work20200223.IPayService;

import java.util.UUID;

/**
 *
 * @Author: Ray Allen  @Time:2020/2/25 0025
 */
public class FactorymethodApp {

    public static void main(String[] args) {

        String ordrNo = UUID.randomUUID().toString();

        IPayFactory aliFactory = new AlipayFactory();
        IPayService load = aliFactory.load();
        load.pay(ordrNo);
        load.notify(ordrNo);

        IPayFactory wechatFactory = new WechatpayFactory();
        IPayService load1 = wechatFactory.load();
        load1.pay(ordrNo);
        load1.notify(ordrNo);
    }
}
