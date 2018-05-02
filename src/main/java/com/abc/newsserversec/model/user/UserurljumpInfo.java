package com.abc.newsserversec.model.user;


/**
 * 用户页面跳转信息数据类
 */
public class UserurljumpInfo {

    //序号
    private long id;
    //用户序号
    private long userid;
    //前页面
    private String beforeurl;
    //后页面
    private String afterurl;
    //点击名称
    private String clickname;
    //创建日期
    private String createdate;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }

    public String getBeforeurl() {
        return beforeurl;
    }

    public void setBeforeurl(String beforeurl) {
        this.beforeurl = beforeurl;
    }

    public String getAfterurl() {
        return afterurl;
    }

    public void setAfterurl(String afterurl) {
        this.afterurl = afterurl;
    }

    public String getClickname() {
        return clickname;
    }

    public void setClickname(String clickname) {
        this.clickname = clickname;
    }

    public String getCreatedate() {
        return createdate;
    }

    public void setCreatedate(String createdate) {
        this.createdate = createdate;
    }

    @Override
    public String toString() {
        return "UserurljumpInfo{" +
                "id=" + id +
                ", userid=" + userid +
                ", beforeurl='" + beforeurl + '\'' +
                ", afterurl='" + afterurl + '\'' +
                ", clickname='" + clickname + '\'' +
                ", createdate='" + createdate + '\'' +
                '}';
    }
}
