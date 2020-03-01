package com.gupaovip_homework.work20200226;

/**
 * 追忆寻梦
 * rundreams.net
 * rundreams@yeah.net
 *
 * @Author: Ray Allen  @Time:2020/2/28 0028
 */
public class ContainerSingleApp {

    public static void main(String[] args) {

//        Object instance1 = ContainerSingleton.getInstance(Pojo.class);
//        Object instance2 = ContainerSingleton.getInstance(Pojo.class);
//        System.out.println(instance1);
//        System.out.println(instance2);
        Thread t1 =new Thread(new ThreadContainerSingle());
        Thread t2 =new Thread(new ThreadContainerSingle());
        t1.start();
        t2.start();
        System.out.println("End");

    }
}
