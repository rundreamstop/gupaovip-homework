package com.gupaovip_homework.demo.controller;

import com.gupaovip_homework.annotation.RunAutowired;
import com.gupaovip_homework.annotation.RunController;
import com.gupaovip_homework.annotation.RunRequestMapping;
import com.gupaovip_homework.annotation.RunRequestParam;
import com.gupaovip_homework.demo.service.IDemoService;
import com.gupaovip_homework.v3.webmvc.servlet.RunModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Ray Allen  @Time:2020/4/5 0005
 */

@RunController
@RunRequestMapping("/api")
public class DemoController {

    @RunAutowired
    IDemoService iDemoService;

    @RunRequestMapping("/query*")
    public RunModelAndView query(HttpServletRequest request, HttpServletResponse response,
                                 @RunRequestParam("name") String name) {

        String name1 = iDemoService.getName(name);

        return out(name1, request, response);
    }

    @RunRequestMapping("/mvc*")
    public RunModelAndView mvc(HttpServletRequest request, HttpServletResponse response,
                               @RunRequestParam("data") String data) {

//        String data1 = iDemoService.getName(data);
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("data", "zhangzihao");

        return new RunModelAndView("first", model);
    }

    @RunRequestMapping("/add")
    public RunModelAndView add(HttpServletRequest request, HttpServletResponse response,
                               @RunRequestParam("name") String name) {

        return out(name, request, response);
    }

    private RunModelAndView out(String str, HttpServletRequest request,
                                HttpServletResponse response) {
        try {
            response.getWriter().write(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
