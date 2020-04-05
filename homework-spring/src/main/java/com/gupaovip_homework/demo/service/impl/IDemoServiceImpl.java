package com.gupaovip_homework.demo.service.impl;

import com.gupaovip_homework.annotation.RunService;
import com.gupaovip_homework.demo.service.IDemoService;

/**
 * @Author: Ray Allen  @Time:2020/4/5 0005
 */

@RunService("ZhIDemoService")
public class IDemoServiceImpl implements IDemoService {
    @Override
    public String getName(String str) {
        return "params is:" + str;
    }
}