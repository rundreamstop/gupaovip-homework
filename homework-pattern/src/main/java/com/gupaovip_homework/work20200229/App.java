package com.gupaovip_homework.work20200229;

/**
 * 追忆寻梦
 * rundreams.net
 * rundreams@yeah.net
 *
 * @Author: Ray Allen  @Time:2020/3/1 0001
 */
public class App {

    public static void main(String[] args) throws CloneNotSupportedException {

        MemberVO m1 = new MemberVO();
        m1.setName("张自豪");
        m1.setAge("27");

        System.out.println("m1=" + m1);

        MemberVO m2 = m1.clone();
        System.out.println("m2=" + m2);

        System.out.println(m1 == m2);
        System.out.println(m1.getName() == m2.getName());

        // TODO 深克隆
        MemberVO memberVO = m1.deepClone();
        System.out.println(memberVO);
        System.out.println(m1.getName() == memberVO.getName());

        StringBuffer stringBuffer=new StringBuffer();

    }
}
