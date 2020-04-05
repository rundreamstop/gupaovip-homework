package com.gupaovip_homework.demo.controller;

import com.gupaovip_homework.annotation.RunController;
import com.gupaovip_homework.annotation.RunRequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author: Ray Allen  @Time:2020/4/5 0005
 */
@RunController
@RunRequestMapping("member")
public class MemberController {

    @RunRequestMapping("/find")
    public void query(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.getWriter().write("memberFind方法");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RunRequestMapping("/remove")
    public void add(HttpServletRequest request, HttpServletResponse response) {

        try {
            response.getWriter().write("memberRemove方法");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
