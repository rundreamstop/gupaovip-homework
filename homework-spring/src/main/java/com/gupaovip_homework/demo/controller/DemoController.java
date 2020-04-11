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
    public void query(HttpServletRequest request, HttpServletResponse response,
                      @RunRequestParam("name") String name) throws IOException {

        System.out.println("name values " + name);
        String name1 = iDemoService.getName(name);

        response.getWriter().write(name1);

    }

    @RunRequestMapping("/mvcList*")
    public RunModelAndView mvcList(HttpServletRequest request, HttpServletResponse response,
                                   @RunRequestParam("data") String data, @RunRequestParam("name") String name) {

        String data1 = iDemoService.getName(data);
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("data", data1);
        model.put("name", name);
        model.put("time", System.currentTimeMillis());

        return new RunModelAndView("first", model);
    }

    @RunRequestMapping("/voids*")
    public RunModelAndView voids(HttpServletRequest request, HttpServletResponse response,@RunRequestParam("name") String name) {
        try {
            response.getWriter().write(name);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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
