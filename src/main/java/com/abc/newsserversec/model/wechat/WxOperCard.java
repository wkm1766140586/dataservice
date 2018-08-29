package com.abc.newsserversec.model.wechat;

import com.abc.newsserversec.model.user.UserInfo;

/**
 * 点赞、查看、转发通用类
 */
public class WxOperCard {
    /*操作人userID*/
    private long viewid;
    /*被操作人userID*/
    private long viewedid;
    /*操作类型，1.点赞，2.查看，3.转发*/
    private int opertype;
    /*操作次数*/
    private int opercount;
    /*创建时间*/
    private String createtime;
    /*用户信息*/
    private UserInfo userInfo;

    public long getViewid() {
        return viewid;
    }
    public void setViewid(long viewid) {
        this.viewid = viewid;
    }
    public long getViewedid() {
        return viewedid;
    }
    public void setViewedid(long viewedid) {
        this.viewedid = viewedid;
    }
    public String getCreatetime() {
        return createtime;
    }
    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public int getOpertype() {
        return opertype;
    }

    public void setOpertype(int opertype) {
        this.opertype = opertype;
    }

    public int getOpercount() {
        return opercount;
    }

    public void setOpercount(int opercount) {
        this.opercount = opercount;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
}
