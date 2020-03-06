package com.gupaovip_homework.work20200304;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Ray Allen  @Time:2020/3/6 0006
 */
public class LecturerDecorator extends MenuDecorator {

    public LecturerDecorator(Menu menu) {
        super(menu);
    }

    protected List<String> menuList() {
        List<String> strings = super.menuList();
        List arrList = new ArrayList(strings);
        arrList.add("学员管理");
        return arrList;
    }
}
