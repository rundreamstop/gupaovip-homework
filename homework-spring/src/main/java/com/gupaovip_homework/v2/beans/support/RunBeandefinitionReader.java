package com.gupaovip_homework.v2.beans.support;

import com.gupaovip_homework.v2.beans.config.RunBeandefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 读取 解析bean
 *
 * @Author: Ray Allen  @Time:2020/4/6 0006
 */
public class RunBeandefinitionReader {

    // 配置读取
    private Properties contextConfig = new Properties();

    // 需要注册的bean 的名字  保存扫描的结果
    private List<String> registryBeanClasses = new ArrayList<String>();

    public RunBeandefinitionReader(String... configLocations) {
        doLoadConfig(configLocations[0]);

        //扫描配置文件中 配置的扫描的类
        doScanner(contextConfig.getProperty("scanPackage"));
        for (String beanClass : registryBeanClasses) {
            System.out.println("扫描配置类完成beanClass= " + beanClass);
        }
        System.out.println("配置扫描的类完成");
    }


    public List<RunBeandefinition> loadBeanDefinitions() {
        List<RunBeandefinition> result = new ArrayList<RunBeandefinition>();

        try {
            for (String className : registryBeanClasses) {
                Class<?> beanClass = Class.forName(className);
                // 保存类对象的 ClassName 对应的全类名

                String factoryBeanName = toLowerFirstCase(beanClass.getSimpleName());
                String beanClassName = beanClass.getName();
                result.add(doCreateBeandefinition(factoryBeanName, beanClassName));
                // 还有 beanName 1、默认类名首字母小写，2、自定义  3、接口注入

                // 接口注入
                for (Class<?> i : beanClass.getInterfaces()) {
                    result.add(doCreateBeandefinition(i.getName(), beanClassName));
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return result;
    }

    private String toLowerFirstCase(String str) {
        char[] chars = str.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

    private RunBeandefinition doCreateBeandefinition(String factoryBeanName, String beanClassName) {
        RunBeandefinition beandefinition = new RunBeandefinition();
        beandefinition.setFactoryBeanName(factoryBeanName);
        beandefinition.setBeanClassName(beanClassName);
        return beandefinition;
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
                registryBeanClasses.add(className);
            }
        }

    }

    private void doLoadConfig(String contextConfigLocation) {

        // classpath 路劲下去读取
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation.replaceAll("classpath:", ""));
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
