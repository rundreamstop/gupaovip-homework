package com.gupaovip_homework.v4.context;

import com.gupaovip_homework.annotation.RunAutowired;
import com.gupaovip_homework.annotation.RunController;
import com.gupaovip_homework.annotation.RunService;
import com.gupaovip_homework.v4.aop.RunAdviseSupport;
import com.gupaovip_homework.v4.aop.config.RunAopConfig;
import com.gupaovip_homework.v4.aop.support.RunJdkDynamicAopProxy;
import com.gupaovip_homework.v4.beans.RunBeanWrapper;
import com.gupaovip_homework.v4.beans.config.RunBeandefinition;
import com.gupaovip_homework.v4.beans.support.RunBeandefinitionReader;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * ApplicationContext : 完成bean的创建 、DI
 *
 * @Author: Ray Allen  @Time:2020/4/6 0006
 */
public class RunApplicationContext {

    // 读取bean
    private RunBeandefinitionReader reader;

    // bean definition
    private Map<String, RunBeandefinition> beandefinitionMap = new HashMap<String, RunBeandefinition>();

    // bean wrapper 的缓存
    private Map<String, RunBeanWrapper> factoryBeanInstanceCache = new HashMap<String, RunBeanWrapper>();

    // 原生对象 单独的容器保存原生对象
    private Map<String, Object> factoryBeanObjectCache = new HashMap<String, Object>();

    public int getBeanDefinitionCount() {
        return this.beandefinitionMap.size();
    }

    public String[] getBeanDefinitionNames() {
        return this.beandefinitionMap.keySet().toArray(new String[this.beandefinitionMap.size()]);
    }

    public RunApplicationContext(String... configLocations) {

        try {
            // 1.读取配置文件
            reader = new RunBeandefinitionReader(configLocations);

            // 2.解析配置文件，封装成 Beandefinition
            List<RunBeandefinition> beandefinitions = reader.loadBeanDefinitions();
            for (RunBeandefinition beandefinition : beandefinitions) {
                System.out.println("封装为beandefinition= " + beandefinition);
            }

            // 3.把Beandefinition
            doRegistBeanDefinition(beandefinitions);

            doAutowrited();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doAutowrited() {
        // 这一步，所有的bean还没有真正的实例化，还在配置阶段
        for (Map.Entry<String, RunBeandefinition> beandefinitionEntry : this.beandefinitionMap.entrySet()) {
            String factoryBeanName = beandefinitionEntry.getKey();
            getBean(factoryBeanName);
        }
        // 调用getBean才触发
    }

    private void doRegistBeanDefinition(List<RunBeandefinition> beandefinitions) throws Exception {

        for (RunBeandefinition beandefinition : beandefinitions) {
            // 两种方式注入 缓存起来
            if (this.beandefinitionMap.containsKey(beandefinition.getFactoryBeanName())) {
                throw new Exception("这个bean已存在" + beandefinition.getFactoryBeanName());
            }
            beandefinitionMap.put(beandefinition.getFactoryBeanName(), beandefinition);
            beandefinitionMap.put(beandefinition.getBeanClassName(), beandefinition);
        }
    }

    // bean 的实例化，DI 从这里开始
    public Object getBean(String beanName) {

        // 1. 拿到配置信息 BeanDefinition
        RunBeandefinition beandefinition = this.beandefinitionMap.get(beanName);
        // 2. 反射实例化 newInstance
        Object instance = instantiateBean(beanName, beandefinition);
        // 3. 封装成 叫做  BeanWrapper 装饰器模式
        RunBeanWrapper beanWrapper = new RunBeanWrapper(instance);
        // 4. 保存到IoC容器
        factoryBeanInstanceCache.put(beanName, beanWrapper);

        // 5. 执行依赖注入
        populateBean(beanName, beandefinition, beanWrapper);

        return beanWrapper.getWrapperInstance();
    }

    private void populateBean(String beanName, RunBeandefinition beandefinition,
                              RunBeanWrapper beanWrapper) {

        // 可能会涉及到循环依赖  TODO 两个缓存，循环两次
        // TODO 把第一次读取结果为空的Beandefinition存在第一个缓存
        // TODO 等第一次循环完后，第二次循环再检查第一次的缓存，再进行赋值
        Object instance = beanWrapper.getWrapperInstance();
        Class<?> clazz = beanWrapper.getWrapperClass();

        // 注入条件判断  在spring中 @Component
        if (!(clazz.isAnnotationPresent(RunController.class) ||
                clazz.isAnnotationPresent(RunService.class))) {
            return;
        }

        for (Field field : clazz.getDeclaredFields()) {
            if (!field.isAnnotationPresent(RunAutowired.class)) {
                continue;
            }
            RunAutowired autowired = field.getAnnotation(RunAutowired.class);
            // 如果用户没有自定义beanName ,就默认注入类型
            String autowiredBeanName = autowired.value().trim();

            if ("".equals(autowiredBeanName)) {
                // 获取字段的类型
                autowiredBeanName = field.getType().getName();
            }
            // 强制访问
            field.setAccessible(true);
            try {
                // 通过接口的全名拿到接口的实例
                if (this.factoryBeanInstanceCache.get(autowiredBeanName) == null) {
                    continue;
                }
                field.set(instance, this.factoryBeanInstanceCache.
                        get(autowiredBeanName).getWrapperInstance());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 创建真正的实例对象
    private Object instantiateBean(String beanName, RunBeandefinition beandefinition) {
        System.out.println("实例化对象beanName= " + beanName);
        System.out.println("实例化对象beandefinition= " + beandefinition);
        String className = beandefinition.getBeanClassName();
        Object instance = null;
        try {
            if (this.factoryBeanObjectCache.containsKey(beanName)) {
                instance = factoryBeanObjectCache.get(beanName);
            } else {
                Class<?> clazz = Class.forName(className);
                // 默认对的类名首字母小写、
                instance = clazz.newInstance();

                //TODO AOP 动态代理  切面  开始

                // 读取aop配置
                RunAdviseSupport config = instantionAopConfig(beandefinition);
                config.setTargetClass(clazz);
                config.setTarget(instance);

                // 判断规则是否生成代理类，如果不要不做任何处理返回原生对象
                if (config.pointCutMath()) {
                    instance = new RunJdkDynamicAopProxy(config).getProxy();
                }

                //TODO AOP 动态代理  切面  结束

                // 保存原生实例化对象  备忘录模式
                this.factoryBeanObjectCache.put(beanName, instance);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return instance;
    }

    private RunAdviseSupport instantionAopConfig(RunBeandefinition beandefinition) {

        RunAopConfig config = new RunAopConfig();

        config.setPointCut(this.reader.getConfig().getProperty("pointCut"));
        config.setAspectClass(this.reader.getConfig().getProperty("aspectClass"));
        config.setAspectBefore(this.reader.getConfig().getProperty("aspectBefore"));
        config.setAspectAfter(this.reader.getConfig().getProperty("aspectAfter"));
        config.setAspectAfterThrow(this.reader.getConfig().getProperty("aspectAfterThrow"));
        config.setAspectAfterThrowingName(this.reader.getConfig().getProperty("aspectAfterThrowingName"));

        return new RunAdviseSupport(config);
    }

    public Object getBean(Class beanClass) {
        return getBean(beanClass.getName());
    }

    public Properties getConfig() {
        return reader.getConfig();
    }
}
