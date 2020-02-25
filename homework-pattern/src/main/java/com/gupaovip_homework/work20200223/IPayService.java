package com.gupaovip_homework.work20200223;

/**
 *
 * @Author: Ray Allen  @Time:2020/2/25 0025
 */
public interface IPayService {

    /**
     * 支付
     *
     * @param orderNo
     * @return
     */
    String pay(String orderNo);

    /**
     * 异步通知
     *
     * @param orderNo
     * @return
     */
    String notify(String orderNo);
}
