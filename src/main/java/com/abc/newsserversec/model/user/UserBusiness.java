package com.abc.newsserversec.model.user;
/**
 * 用户的业务信息，负责的产品
 */
public class UserBusiness {
    private int id;
    private int userid;
    private String productids;
    private String areaids;
    private String createtime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getProductids() {
        return productids;
    }

    public String getAreaids() {
        return areaids;
    }

    public void setAreaids(String areaids) {
        this.areaids = areaids;
    }

    public void setProductids(String productids) {
        this.productids = productids;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }
}
