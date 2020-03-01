package com.gupaovip_homework.work20200229;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Ray Allen  @Time:2020/3/1 0001
 */

@Data
public class MemberVO implements  Serializable {

    private static final long serialVersionUID = 1927717987651398932L;
    private String name;

    private String age;

    private String address;

    @Override
    public MemberVO clone() throws CloneNotSupportedException {
        return (MemberVO) super.clone();
    }

    // 深克隆
    public MemberVO deepClone() {
        MemberVO memberVO = this;
        JSONObject jsonObj = (JSONObject) JSON.toJSON(memberVO);
        MemberVO data = JSON.parseObject(jsonObj.toString(), MemberVO.class);
        return data;
    }

}
