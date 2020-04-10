package com.gupaovip_homework.v2.webmvc.servlet;

import com.gupaovip_homework.annotation.RunController;
import com.gupaovip_homework.annotation.RunRequestMapping;
import com.gupaovip_homework.v2.context.RunApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 委派模式：
 * 负责请求调度 任务分发
 *
 * @Author: Ray Allen  @Time:2020/4/5 0005
 */
public class RunDispatcherServlet extends HttpServlet {

    private static final long serialVersionUID = 2076670715409371630L;
    // 配置读取
    private Properties contextConfig = new Properties();

    //享元模式，缓存起来
    private List<String> classNames = new ArrayList<String>();

    // ioc 容器  key默认是类名首字母小写，value 类的实例
    private Map<String, Object> ioc = new HashMap<String, Object>();

    // handlerMapping
    private Map<String, Method> handlerMapping = new HashMap<String, Method>();


    private RunApplicationContext applicationContext;

    @Override
    public void init(ServletConfig config) throws ServletException {

        // 初始化Spring核心ioc容器
        applicationContext = new RunApplicationContext(config.getInitParameter("contextConfigLocation"));

        // 5.初始化HandlerMapping   // TODO 还未改造完
        doInitHandlerMapping();

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {

        // 6. 委派,根据URL找到一个对应的Method 再通过反射进行 invoke 调用
        try {
            doDispatch(req, resp);
        } catch (Exception e) {
            try {
                resp.getWriter().write(e.getMessage());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }


    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replaceAll(contextPath, "").replaceAll("/+", "/");
        if (!this.handlerMapping.containsKey(url)) {
            resp.getWriter().write("404 Not Found!!!");
            return;
        }

        Method method = this.handlerMapping.get(url);
        System.out.println("doDispatch method " + method);

        Map<String, String[]> params = req.getParameterMap();
        String beanName = toLowerFirstCase(method.getDeclaringClass().getSimpleName());
        // 再反射调用 控制器，传入request  等参数  再去get了一次
        method.invoke(applicationContext.getBean(beanName), new Object[]{req, resp, params.get("name")[0]});

        // 参数灵活配置
        Class<?>[] parameterTypes = method.getParameterTypes();

    }

    private void doInitHandlerMapping() {
        if (applicationContext.getBeanDefinitionCount() <= 0) {
            return;
        }
        for (String beanName : applicationContext.getBeanDefinitionNames()) {
            Object instance = applicationContext.getBean(beanName);
            Class<?> clazz = instance.getClass();

            // 判断加了controller 注解
            if (!clazz.isAnnotationPresent(RunController.class)) {
                continue;
            }
            // 类上面的mapping url
            String baseUrl = "";
            if (clazz.isAnnotationPresent(RunRequestMapping.class)) {
                RunRequestMapping requestMapping = clazz.getAnnotation(RunRequestMapping.class);
                baseUrl = requestMapping.value();
            }

            // 只获取public 方法
            for (Method method : clazz.getMethods()) {
                // 判断 mapping 注解
                if (!method.isAnnotationPresent(RunRequestMapping.class)) {
                    continue;
                }
                // 方法上面的url
                RunRequestMapping requestMapping = method.getAnnotation(RunRequestMapping.class);
                String url = ("/" + baseUrl + "/" + requestMapping.value()).
                        replaceAll("/+", "/");
                handlerMapping.put(url, method);
            }

        }
    }

    private String toLowerFirstCase(String str) {
        char[] chars = str.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

}
