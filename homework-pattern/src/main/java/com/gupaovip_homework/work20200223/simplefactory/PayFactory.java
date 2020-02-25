package com.gupaovip_homework.work20200223.simplefactory;


import com.gupaovip_homework.work20200223.IPayService;

/**
 *
 * @Author: Ray Allen  @Time:2020/2/25 0025
 */
public class PayFactory {

    public IPayService load(Class<? extends IPayService> clazz) {
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
