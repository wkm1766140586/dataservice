package com.abc.newsserversec.model.company;

/**
 * 企业信息类
 */
public class CompanyInfo {

    //序号
    private long id;
    //企业名称
    private String company_name;
    //生产类型（1:I类生产备案 2:II类生产许可 3:III类生产许可）
    private String production_type;
    //经营类型（1:I类经营列名 2:II类经营备案 3:III类经营许可）
    private String manage_type;
    //互联网类型（1:互联网信息服务 2:互联网交易服务）
    private String web_type;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getProduction_type() {
        return production_type;
    }

    public void setProduction_type(String production_type) {
        this.production_type = production_type;
    }

    public String getManage_type() {
        return manage_type;
    }

    public void setManage_type(String manage_type) {
        this.manage_type = manage_type;
    }

    public String getWeb_type() {
        return web_type;
    }

    public void setWeb_type(String web_type) {
        this.web_type = web_type;
    }
}
