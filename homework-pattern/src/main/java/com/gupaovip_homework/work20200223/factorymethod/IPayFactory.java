package com.gupaovip_homework.work20200223.factorymethod;


import com.gupaovip_homework.work20200223.IPayService;

/**
 *
 * @Author: Ray Allen  @Time:2020/2/25 0025
 */
public interface IPayFactory {

    IPayService load();
}
