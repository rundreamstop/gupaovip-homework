package com.gupaovip_homework.v4.webmvc.servlet;

import com.gupaovip_homework.annotation.RunController;
import com.gupaovip_homework.annotation.RunRequestMapping;
import com.gupaovip_homework.v4.context.RunApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 委派模式：
 * 负责请求调度 任务分发
 *
 * @Author: Ray Allen  @Time:2020/4/5 0005
 */
public class RunDispatcherServlet extends HttpServlet {

    private static final long serialVersionUID = 2076670715409371630L;

    // application
    private RunApplicationContext applicationContext;

    // handlerMapping
    private List<RunHandlerMapping> handlerMappings = new ArrayList<RunHandlerMapping>();

    private Map<RunHandlerMapping, RunHandlerAdapter> handlerAdapters = new HashMap<RunHandlerMapping, RunHandlerAdapter>();

    private List<RunViewResolver> viewResolvers = new ArrayList<RunViewResolver>();

    @Override
    public void init(ServletConfig config) {

        // 初始化Spring核心ioc容器
        applicationContext = new RunApplicationContext(config.getInitParameter("contextConfigLocation"));

        // 初始化mvc 九大组件
        initStrategies(applicationContext);

    }

    private void initStrategies(RunApplicationContext context) {

        // TODO 初始化核心 组件

        // 初始化hanlderMapping  得到controller 注解的 RunHandlerMapping 对象
        initHandlerMappings();
        for (RunHandlerMapping handlerMapping : handlerMappings) {
            System.out.println(handlerMapping);
        }

        // 初始化 参数适配器
        initHandlerAdapters();

        for (Map.Entry<RunHandlerMapping, RunHandlerAdapter> runHandlerMappingRunHandlerAdapterEntry : handlerAdapters.entrySet()) {
            System.out.println(runHandlerMappingRunHandlerAdapterEntry);
        }
        // 初始化 视图转化器
        initViewResolvers(context);

        for (RunViewResolver viewResolver : viewResolvers) {
            System.out.println(viewResolver);
        }
    }

    private void initViewResolvers(RunApplicationContext context) {
        String templateRoot = context.getConfig().getProperty("templateRoot");
        String templateRootPath = this.getClass().getClassLoader().
                getResource(templateRoot).getFile();
        File templateRootDir = new File(templateRootPath);
        for (File file : templateRootDir.listFiles()) {
            this.viewResolvers.add(new RunViewResolver(templateRoot));
        }
    }

    private void initHandlerAdapters() {
        for (RunHandlerMapping handlerMapping : handlerMappings) {
            this.handlerAdapters.put(handlerMapping, new RunHandlerAdapter());
        }
    }

    private void initHandlerMappings() {
        if (applicationContext.getBeanDefinitionCount() <= 0) {
            return;
        }
        for (String beanName : applicationContext.getBeanDefinitionNames()) {

            // getBean的时候再去实例化
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
                String regex = ("/" + baseUrl + "/" +
                        requestMapping.value().replaceAll("\\*", ".*")).
                        replaceAll("/+", "/");
                // 转化为正则
                Pattern pattern = Pattern.compile(regex);
                // TODO
                handlerMappings.add(new RunHandlerMapping(pattern, instance, method));
            }
        }
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
                processDispatchResult(req, resp, new RunModelAndView("500"));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {

        // 完成了对handlermapping的封装
        // 完成了对方法 RunModelAndView 的封装

        // 1、通过 URL获得一个HandlerMapping
        RunHandlerMapping handler = getHandler(req);
        if (handler == null) {
            processDispatchResult(req, resp, new RunModelAndView("404"));
            return;
        }

        // 2、根据一个HandlerMapping获得一个HandlerAdapter
        RunHandlerAdapter adapter = getHandlerAdapter(handler);

        // 3、解析某一个方法的形参和返回值之后，统一封装为RunModelAndView
        RunModelAndView mv = adapter.handler(req, resp, handler);

        // 把RunModelAndView 变成 一个 RunViewResolver
        processDispatchResult(req, resp, mv);

    }

    private RunHandlerAdapter getHandlerAdapter(RunHandlerMapping handler) {
        if (this.handlerAdapters.isEmpty()) {
            return null;
        }
        return this.handlerAdapters.get(handler);
    }

    private void processDispatchResult(HttpServletRequest req, HttpServletResponse resp,
                                       RunModelAndView mv) throws Exception {

        if (mv == null) {
            return;
        }
        if (this.viewResolvers.isEmpty()) {
            return;
        }

        for (RunViewResolver viewResolver : this.viewResolvers) {
            RunView view = viewResolver.resolverViewName(mv.getViewName());
            // 直接往浏览器输出
            view.render(mv.getModel(), req, resp);
            return;
        }
    }

    private RunHandlerMapping getHandler(HttpServletRequest req) {
        if (this.handlerMappings.isEmpty()) {
            return null;
        }

        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replaceAll(contextPath, "").replaceAll("/+", "/");

        for (RunHandlerMapping mapping : handlerMappings) {
            Matcher matcher = mapping.getPattern().matcher(url);
            if (!matcher.matches()) {
                continue;
            }
            return mapping;
        }
        return null;
    }

}
