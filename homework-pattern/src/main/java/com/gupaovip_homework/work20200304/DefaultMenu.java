package com.gupaovip_homework.work20200304;

import java.util.Arrays;
import java.util.List;

/**
 * @Author: Ray Allen  @Time:2020/3/6 0006
 */
public class DefaultMenu extends Menu {


    @Override
    protected List<String> menuList() {

        return Arrays.asList("问答", "文章", "精品课", "冒泡", "商城");
    }
}
