package com.gupaovip_homework.v1;

import com.gupaovip_homework.annotation.RunAutowired;
import com.gupaovip_homework.annotation.RunController;
import com.gupaovip_homework.annotation.RunRequestMapping;
import com.gupaovip_homework.annotation.RunService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

/**
 * @Author: Ray Allen  @Time:2020/4/5 0005
 */
public class RunDispatcherServlet extends HttpServlet {
    private static final long serialVersionUID = -4882354658732090357L;

    // 配置读取
    private Properties contextConfig = new Properties();

    //享元模式，缓存起来
    private List<String> classNames = new ArrayList<String>();

    // ioc 容器  key默认是类名首字母小写，value 类的实例
    private Map<String, Object> ioc = new HashMap<String, Object>();

    // handlerMapping
    private Map<String, Method> handlerMapping = new HashMap<String, Method>();

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


    @Override
    public void init(ServletConfig config) throws ServletException {
        // 1.加载配置文件
        doLoadConfig(config.getInitParameter("contextConfigLocation"));
        // 2.扫描相关的类,根据配置的扫描路劲，得到所有的类名全路劲
        doScanner(contextConfig.getProperty("scanPackage"));
        printScannerList();

        // 3.初始化IoC容器,扫描到的相关的类实例化(加了注解的类)，保存到Ioc容器 map 中，key保存类首字母小写，value保存类的实例化对象
        // 3.1 后续加入 AOP 新生成的代理对象
        doInstance();
        printIocList();

        // 4.依赖注入 DI  通过 有 @RunAutowired 注解的 类，自动注入
        doAutowired();

        // 5.初始化HandlerMapping
        doInitHandlerMapping();

        printHandlerMapper();
        System.out.println("Run Spring Init Finsh");
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
        // 再反射调用 控制器，传入request  等参数
        method.invoke(ioc.get(beanName), new Object[]{req, resp, params.get("name")[0]});

        // 参数灵活配置
        Class<?>[] parameterTypes = method.getParameterTypes();

    }

    private void doInitHandlerMapping() {
        if (ioc.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            Class<?> clazz = entry.getValue().getClass();

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
                System.out.println("Mapped：" + url + " ," + method);
            }

        }
    }

    private void doAutowired() {
        if (ioc.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            for (Field field : entry.getValue().getClass().getDeclaredFields()) {
                if (!field.isAnnotationPresent(RunAutowired.class)) {
                    continue;
                }
                RunAutowired autowired = field.getAnnotation(RunAutowired.class);
                // 如果用户没有自定义beanName ,就默认注入类型
                String beanName = autowired.value().trim();
                System.out.println("DI beanName" + beanName);
                if ("".equals(beanName)) {
                    // 获取字段的类型
                    beanName = field.getType().getName();
                }
                // 强制访问
                field.setAccessible(true);
                // TODO qa
                try {
                    // 通过接口的全名拿到接口的实例
                    printIocList();
                    System.out.println("doAutowired 1" + entry.getValue());
                    System.out.println("doAutowired 2" + ioc.get(beanName));
                    field.set(entry.getValue(), ioc.get(beanName));
                    printIocList();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doInstance() {
        if (classNames.isEmpty()) {
            return;
        }
        try {
            for (String className : classNames) {
                Class<?> clazz = Class.forName(className);

                // 加了这些注解对的控制权 才反转
                if (clazz.isAnnotationPresent(RunController.class)) {

                    Object instance = clazz.newInstance();
                    ioc.put(toLowerFirstCase(clazz.getSimpleName()), instance);
                } else if (clazz.isAnnotationPresent(RunService.class)) {

                    String beanName = clazz.getAnnotation(RunService.class).value();
                    // 如果没有自定义命名，默认使用首字母小写
                    if ("".equals(beanName.trim())) {
                        beanName = toLowerFirstCase(clazz.getSimpleName());
                    }
                    // 默认对的类名首字母小写、
                    Object instance = clazz.newInstance();
                    ioc.put(toLowerFirstCase(beanName), instance);

                    // 多个包下出现相同的类名，自定义全局唯一的名字
                    // 如果是接口，判断多少个实现类，如果只有一个， 默认选择一个 ，多个抛异常
                    for (Class<?> i : clazz.getInterfaces()) {
                        if (ioc.containsKey(i.getName())) {
                            throw new Exception("The " + i.getName() + " is exists!");
                        }
                        ioc.put(i.getName(), instance);
                    }
                } else {
                    continue;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String toLowerFirstCase(String str) {
        char[] chars = str.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

    private void doScanner(String scanPackage) {
        // 包扫描，其实就是文件夹
        URL url = this.getClass().getClassLoader().getResource("/"
                + scanPackage.replaceAll("\\.", "/"));
        File classPath = new File(url.getFile());
        // 当成是一个ClassPath文件夹
        for (File file : classPath.listFiles()) {

            // 递归子文件夹
            if (file.isDirectory()) {
                doScanner(scanPackage + "." + file.getName());
            } else {
                // 如果不是 .class结尾的  直接跳过  是class文件才加入
                if (!file.getName().endsWith(".class")) {
                    continue;
                }
                String className = (scanPackage + "." + file.getName().
                        replace(".class", ""));
                classNames.add(className);
            }
        }

    }

    private void printScannerList() {
        System.out.println("打印扫描类开始=================================");
        for (String className : classNames) {
            System.out.println(className);
        }
        System.out.println("打印扫描类结束=================================");
    }

    private void printIocList() {
        System.out.println("打印ioc容器开始=================================");
        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            System.out.println(entry.getKey());
            System.out.println(entry.getValue());
        }
        System.out.println("打印ioc容器结束=================================");
    }

    private void printHandlerMapper() {
        System.out.println("打印printHandlerMapper开始=================================");
        for (Map.Entry<String, Method> stringMethodEntry : this.handlerMapping.entrySet()) {
            System.out.println(stringMethodEntry.getKey());
            System.out.println(stringMethodEntry.getValue());
        }
        System.out.println("打印printHandlerMapper结束=================================");
    }

    private void doLoadConfig(String contextConfigLocation) {
        System.out.println("进入doLoadConfig");
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation);
        try {
            contextConfig.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
