package com.wg.redisson.redisson.entity.vo;

import com.alibaba.excel.annotation.ExcelProperty;

import java.time.LocalDateTime;


public class WxUser {
    @ExcelProperty("appid")
    private String appid;
    @ExcelProperty("nickname")
    private String nickname;
    @ExcelProperty("openid")
    private String openid;
    @ExcelProperty("sex")
    private Integer sex;
    @ExcelProperty("subscribe")
    private Integer subscribe;
//    @ExcelProperty("subscribe_time")
//    private LocalDateTime subscribeTime;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Integer getSubscribe() {
        return subscribe;
    }

    public void setSubscribe(Integer subscribe) {
        this.subscribe = subscribe;
    }

//    public LocalDateTime getSubscribeTime() {
//        return subscribeTime;
//    }
//
//    public void setSubscribeTime(LocalDateTime subscribeTime) {
//        this.subscribeTime = subscribeTime;
//    }


    @Override
    public String toString() {
        return "WxUser{" +
                "appid='" + appid + '\'' +
                ", nickname='" + nickname + '\'' +
                ", openid='" + openid + '\'' +
                ", sex=" + sex +
                ", subscribe=" + subscribe +
                '}';
    }
}
