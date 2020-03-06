package com.gupaovip_homework.work20200304;

/**
 * @Author: Ray Allen  @Time:2020/3/6 0006
 */
public class App {

    public static void main(String[] args) {

        Menu menu;

        menu = new DefaultMenu();
        System.out.println("默认菜单：" + menu.menuList());

        menu = new StuVipDecorator(menu);
        System.out.println("VIP学员菜单：" + menu.menuList());

        menu = new LecturerDecorator(menu);
        System.out.println("讲师菜单：" + menu.menuList());
    }
}
