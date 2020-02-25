package com.gupaovip_homework.work20200223.factorymethod;


import com.gupaovip_homework.work20200223.IPayService;
import com.gupaovip_homework.work20200223.WechatPayImpl;

/**
 *
 * @Author: Ray Allen  @Time:2020/2/25 0025
 */
public class WechatpayFactory implements IPayFactory {
    @Override
    public IPayService load() {
        return new WechatPayImpl();
    }
}
