package com.abc.newsserversec.model.user;


/**
 * 用户信息数据类
 */
public class UserInfo {

    //序号
    private long id;
    //用户名
    private String username;
    //邮箱
    private String email;
    //密码
    private String password;
    //openid
    private String openid;
    //cardde
    private String openidCard;
    //unionid
    private String unionid;
    //昵称
    private String nickname;

    public String getOpenidCard() {
        return openidCard;
    }

    public void setOpenidCard(String openidCard) {
        this.openidCard = openidCard;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    //手机号
    private String mobilephone;
    //真实姓名
    private String realname;
    //性别
    private String sex;
    //身份证号
    private String cardid;
    //头像
    private String headimg;
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
    private String createdate;
    //vip等级
    private int viplevel;
    //登录次数
    private long logincount;

    private String usertype;

    /*用户时名片用户*/
    private int iscard;

    public String getUsertype() {
        return usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    public int getIscard() {
        return iscard;
    }

    public void setIscard(int iscard) {
        this.iscard = iscard;
    }

    public long getId() { return id; }

    public void setId(long id) { this.id = id; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getEmail() {return email;}

    public void setEmail(String email) {this.email = email;}

    public String getHeadimg() {
        return headimg;
    }

    public void setHeadimg(String headimg) {
        this.headimg = headimg;
    }

    public String getWechatnum() {
        return wechatnum;
    }

    public void setWechatnum(String wechatnum) {
        this.wechatnum = wechatnum;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public String getNickname() { return nickname; }

    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getMobilephone() { return mobilephone; }

    public void setMobilephone(String mobilephone) { this.mobilephone = mobilephone; }

    public String getRealname() { return realname; }

    public void setRealname(String realname) { this.realname = realname; }

    public String getSex() { return sex; }

    public void setSex(String sex) { this.sex = sex; }

    public String getCardid() { return cardid; }

    public void setCardid(String cardid) { this.cardid = cardid; }

    public String getCompanyname() { return companyname; }

    public void setCompanyname(String companyname) { this.companyname = companyname; }

    public String getCompanyaddress() { return companyaddress; }

    public void setCompanyaddress(String companyaddress) { this.companyaddress = companyaddress; }

    public String getDepartment() { return department; }

    public void setDepartment(String department) { this.department = department; }

    public String getJob() {  return job; }

    public void setJob(String job) { this.job = job; }

    public String getCreatedate() { return createdate; }

    public void setCreatedate(String createdate) { this.createdate = createdate; }

    public int getViplevel() { return viplevel; }

    public void setViplevel(int viplevel) { this.viplevel = viplevel; }

    public long getLogincount() { return logincount; }

    public void setLogincount(long logincount) { this.logincount = logincount; }

}
