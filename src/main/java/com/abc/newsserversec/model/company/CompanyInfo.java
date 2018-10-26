package com.abc.newsserversec.model.company;

import java.util.ArrayList;
import java.util.Map;

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
    //产品数量
    private String product_count;
    //中标数量
    private String tenderbid_count;

    private ArrayList<Map<String,Object>> headimgList;

    /*展位信息*/
    private Map<String,Object> exhibitionInfo;

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

    public String getProduct_count() {
        return product_count;
    }

    public void setProduct_count(String product_count) {
        this.product_count = product_count;
    }

    public String getTenderbid_count() {
        return tenderbid_count;
    }

    public void setTenderbid_count(String tenderbid_count) {
        this.tenderbid_count = tenderbid_count;
    }

    public ArrayList<Map<String, Object>> getHeadimgList() {
        return headimgList;
    }

    public void setHeadimgList(ArrayList<Map<String, Object>> headimgList) {
        this.headimgList = headimgList;
    }

    public Map<String, Object> getExhibitionInfo() {
        return exhibitionInfo;
    }

    public void setExhibitionInfo(Map<String, Object> exhibitionInfo) {
        this.exhibitionInfo = exhibitionInfo;
    }
}
