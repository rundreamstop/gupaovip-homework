package com.gupaovip_homework.work20200304;

import java.util.List;

/**
 * @Author: Ray Allen  @Time:2020/3/6 0006
 */
public class MenuDecorator extends Menu {

    private Menu menu;

    public MenuDecorator(Menu menu) {
        this.menu = menu;
    }

    @Override
    protected List<String> menuList() {
        return this.menu.menuList();
    }
}
