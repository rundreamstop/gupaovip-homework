package com.gupaovip_homework.v4.webmvc.servlet;

import com.gupaovip_homework.annotation.RunRequestParam;
import lombok.Data;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Ray Allen  @Time:2020/4/10 0010
 */
@Data
public class RunHandlerAdapter {

    public RunModelAndView handler(HttpServletRequest req, HttpServletResponse resp, RunHandlerMapping handler) throws Exception {

        // 保存形参列表 将参数名称和参数位置保存起来
        Map<String, Integer> paramIndexMapping = new HashMap<String, Integer>();

        //通过运行时的状态去拿到你  //TODO 二维数组
        Annotation[][] pa = handler.getMethod().getParameterAnnotations();
        for (int i = 0; i < pa.length; i++) {
            for (Annotation a : pa[i]) {
                if (a instanceof RunRequestParam) {
                    String paramName = ((RunRequestParam) a).value();
                    if (!"".equals(paramName.trim())) {
                        paramIndexMapping.put(paramName, i);
                    }
                }
            }
        }

        Class<?>[] paramTypes = handler.getMethod().getParameterTypes();
        for (int i = 0; i < paramTypes.length; i++) {
            Class<?> paramterType = paramTypes[i];
            if (paramterType == HttpServletRequest.class) {
                paramIndexMapping.put(paramterType.getName(), i);
            } else if (paramterType == HttpServletResponse.class) {
                paramIndexMapping.put(paramterType.getName(), i);
            } else if (paramterType == String.class) {
            }
        }

        // 去拼接 实参
        // 数组，多个参数
        Map<String, String[]> params = req.getParameterMap();
        Object[] paramValues = new Object[paramTypes.length];
        for (Map.Entry<String, String[]> param : params.entrySet()) {
            String value = Arrays.toString(params.get(param.getKey()))
                    .replaceAll("\\[|\\]", "")
                    .replaceAll("\\s+", ",");
            if (!paramIndexMapping.containsKey(param.getKey())) {
                continue;
            }
            int index = paramIndexMapping.get(param.getKey());
            // 允许自定义的类型转化器 Converter
            paramValues[index] = castStringValue(value, paramTypes[index]);
        }

        // 特殊参数转型
        if (paramIndexMapping.containsKey(HttpServletRequest.class.getName())) {
            int index = paramIndexMapping.get(HttpServletRequest.class.getName());
            paramValues[index] = req;
        }
        if (paramIndexMapping.containsKey(HttpServletResponse.class.getName())) {
            int index = paramIndexMapping.get(HttpServletResponse.class.getName());
            paramValues[index] = resp;
        }

        // 反射调用 TODO
        Object result = handler.getMethod().invoke(handler.getController(), paramValues);
        if (result == null || result instanceof Void) {
            return null;
        }

        boolean isMoedelAndView = handler.getMethod().getReturnType() == RunModelAndView.class;
        if (isMoedelAndView) {
            return (RunModelAndView) result;
        }
        return null;

    }

    private Object castStringValue(String value, Class<?> paramType) {
        if (String.class == paramType) {
            return value;
        } else if (Integer.class == paramType) {
            return Integer.valueOf(value);
        } else if (Double.class == paramType) {
            return Double.valueOf(value);
        } else {
            return value;
        }
    }
}
