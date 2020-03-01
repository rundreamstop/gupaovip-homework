package com.gupaovip_homework.work20200226;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 追忆寻梦
 *
 * @Author: Ray Allen  @Time:2020/2/28 0028
 */
public class ContainerSingleton {

    private ContainerSingleton() {
    }

    private static volatile Map<String, Object> ioc = new ConcurrentHashMap<>();

    public static Object getInstance(Class<?> clazz) {

        String className = clazz.getName();
        // 如果ioc里面有，直接返回
        if (ioc.containsKey(className)) {

            System.out.println("直接返回单例对象");
            return ioc.get(className);
        } else {

            System.out.println("put单例对象");
            Object instance = clazz.getInterfaces();
            // 没有进行put值
            map(className, instance);
            return instance;
        }
    }

    private static synchronized void map(String className, Object instance) {
        ioc.put(className, instance);
    }

}
