package com.gupaovip_homework.work20200304;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: Ray Allen  @Time:2020/3/6 0006
 */
public class StuVipDecorator extends MenuDecorator {

    public StuVipDecorator(Menu menu) {
        super(menu);
    }

    protected List<String> menuList() {

        return Arrays.asList("问答", "文章", "精品课", "冒泡", "商城","作业","题库","成长墙");
    }
}
