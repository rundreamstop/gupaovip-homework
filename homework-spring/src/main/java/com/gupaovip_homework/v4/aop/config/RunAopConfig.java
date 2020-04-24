package com.gupaovip_homework.v4.aop.config;

import lombok.Data;

/**
 * 读取用户配置 放在内存里面
 *
 * @Author: Ray Allen  @Time:2020/4/14 0014
 */

@Data
public class RunAopConfig {

    private String pointCut;

    private String aspectClass;

    private String aspectBefore;

    private String aspectAfter;

    private String aspectAfterThrow;

    private String aspectAfterThrowingName;
}
