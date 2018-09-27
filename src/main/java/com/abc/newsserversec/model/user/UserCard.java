package com.abc.newsserversec.model.user;

/**
 * 名片类型
 */
public class UserCard {
    private long id;
    //用户ID
    private int userid;
    //名片邮箱
    private String email;
    //手机号
    private String mobilephone;
    //真实姓名
    private String realname;
    //微信号
    private String wechatnum;
    //公司名称
    private String companyname;
    //公司地址
    private String companyaddress;
    //部门
    private String department;
    //职位
    private String job;
    //创建日期
    private String createtime;
    /*用户时名片用户*/
    private int iscard;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobilephone() {
        return mobilephone;
    }

    public void setMobilephone(String mobilephone) {
        this.mobilephone = mobilephone;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getWechatnum() {
        return wechatnum;
    }

    public void setWechatnum(String wechatnum) {
        this.wechatnum = wechatnum;
    }

    public String getCompanyname() {
        return companyname;
    }

    public void setCompanyname(String companyname) {
        this.companyname = companyname;
    }

    public String getCompanyaddress() {
        return companyaddress;
    }

    public void setCompanyaddress(String companyaddress) {
        this.companyaddress = companyaddress;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public int getIscard() {
        return iscard;
    }

    public void setIscard(int iscard) {
        this.iscard = iscard;
    }
}
