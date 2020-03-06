package com.gupaovip_homework.work20200304;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Ray Allen  @Time:2020/3/6 0006
 */
public class StuVipDecorator extends MenuDecorator {

    public StuVipDecorator(Menu menu) {
        super(menu);
    }

    protected List<String> menuList() {
        List<String> strings = super.menuList();
        List arrList = new ArrayList(strings);
        arrList.add("作业");
        arrList.add("题库");
        arrList.add("成长墙");
        return arrList;
    }
}
