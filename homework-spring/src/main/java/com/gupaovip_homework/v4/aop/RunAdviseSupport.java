package com.gupaovip_homework.v4.aop;

import com.gupaovip_homework.v4.aop.aspect.RunAdvice;
import com.gupaovip_homework.v4.aop.config.RunAopConfig;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 配置解析
 * @Author: Ray Allen  @Time:2020/4/14 0014
 */

@Data
public class RunAdviseSupport {

    private RunAopConfig config;

    private Object target;

    private Class targetClass;

    private Pattern pointCutClassPattern;

    private Map<Method, Map<String, RunAdvice>> methodCache;

    public RunAdviseSupport(RunAopConfig config) {
        this.config = config;
    }

    private void parse() throws Exception {
        // 正则解析
        String poinCut = config.getPointCut()
                .replaceAll("\\.", "\\\\.")
                .replaceAll("\\\\.\\*", ".*")
                .replaceAll("\\(", "\\\\(")
                .replaceAll("\\)", "\\\\)");

        // 三段
        // 第一段：方法的修饰符和返回值
        // 第二段：类名
        // 第三段：方法的名称和形参
        String ponitCutForClassRegex = poinCut.substring(0,
                poinCut.lastIndexOf("\\(") - 4);
        // 生成匹配Class的正则
        pointCutClassPattern = Pattern.compile("class " + ponitCutForClassRegex.substring(
                ponitCutForClassRegex.lastIndexOf(" ") + 1));
        methodCache = new HashMap<Method, Map<String, RunAdvice>>();
        Pattern pointCutPattern = Pattern.compile(poinCut);

        Class aspectClass = Class.forName(this.config.getAspectClass());

        Map<String, Method> aspectMethods = new HashMap<String, Method>();
        for (Method method : aspectClass.getMethods()) {
            aspectMethods.put(method.getName(), method);
        }

        // 封装 RunAdvice
        for (Method method : this.targetClass.getMethods()) {
            String methodString = method.toString();

            if (methodString.contains("throws")) {
                methodString = methodString.substring(0, methodString.lastIndexOf("throws")).trim();
            }

            Matcher matcher = pointCutPattern.matcher(methodString);
            if (matcher.matches()) {
                Map<String, RunAdvice> advices = new HashMap<String, RunAdvice>();
                if (!(config.getAspectBefore() == null || "".equals(config.getAspectBefore()))) {
                    advices.put("before",
                            new RunAdvice(aspectClass.newInstance(),
                                    aspectMethods.get(config.getAspectBefore())));
                }

                if (!(config.getAspectAfter() == null || "".equals(config.getAspectAfter()))) {
                    advices.put("after",
                            new RunAdvice(aspectClass.newInstance(),
                                    aspectMethods.get(config.getAspectAfter())));
                }

                if (!(config.getAspectAfterThrow() == null || "".equals(config.getAspectAfterThrow()))) {
                    advices.put("afterThrowing",
                            new RunAdvice(aspectClass.newInstance(),
                                    aspectMethods.get(config.getAspectAfterThrow()), config.getAspectAfterThrowingName()));
                }
                methodCache.put(method, advices);
            }
        }


    }

    public Map<String, RunAdvice> getAdvices(Method method, Object o) throws Exception {
        Map<String, RunAdvice> cache = methodCache.get(method);
        if (cache == null) {
            Method m = targetClass.getMethod(method.getName(), method.getParameterTypes());
            cache = methodCache.get(m);
            this.methodCache.put(m, cache);
        }
        return cache;
    }

    public boolean pointCutMath() {
        return pointCutClassPattern.matcher(this.targetClass.toString()).matches();
    }

    public void setTargetClass(Class<?> targetClass) {
        this.targetClass = targetClass;
        try {
            parse();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTarget(Object target) {
        this.target = target;
    }
}
