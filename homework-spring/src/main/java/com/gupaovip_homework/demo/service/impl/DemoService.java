package com.gupaovip_homework.demo.service.impl;

import com.gupaovip_homework.annotation.RunService;
import com.gupaovip_homework.demo.service.IDemoService;

/**
 * @Author: Ray Allen  @Time:2020/4/5 0005
 */

@RunService
public class DemoService implements IDemoService {

    @Override
    public String getName(String str) {
        System.out.println("接口实现DemoService日志");
        return "params is:" + str;
    }
}
