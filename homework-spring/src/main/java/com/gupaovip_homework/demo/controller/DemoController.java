package com.gupaovip_homework.demo.controller;

import com.gupaovip_homework.annotation.RunAutowired;
import com.gupaovip_homework.annotation.RunController;
import com.gupaovip_homework.annotation.RunRequestMapping;
import com.gupaovip_homework.annotation.RunRequestParam;
import com.gupaovip_homework.demo.service.IDemoService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author: Ray Allen  @Time:2020/4/5 0005
 */

@RunController
@RunRequestMapping("/api")
public class DemoController {

    @RunAutowired
    IDemoService iDemoService;

    @RunRequestMapping("/query")
    public void query(HttpServletRequest request, HttpServletResponse response,
                      @RunRequestParam("name") String name) {

        String name1 = iDemoService.getName(name);
        try {
            response.getWriter().write(name1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RunRequestMapping("/add")
    public void add(HttpServletRequest request, HttpServletResponse response,
                    @RunRequestParam("name") String name) {
        try {
            response.getWriter().write(name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
