package com.gupaovip_homework.v4.webmvc.servlet;

import lombok.Data;

import java.util.Map;

/**
 * @Author: Ray Allen  @Time:2020/4/10 0010
 */

@Data
public class RunModelAndView {

    private String viewName;

    private Map<String, ?> model;

    public RunModelAndView(String viewName, Map<String, ?> model) {
        this.viewName = viewName;
        this.model = model;
    }

    public RunModelAndView(String viewName) {
        this.viewName = viewName;
    }
}
