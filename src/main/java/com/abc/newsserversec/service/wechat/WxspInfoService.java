package com.abc.newsserversec.service.wechat;


import com.abc.newsserversec.model.wechat.WxspUserInfo;

import java.util.ArrayList;
import java.util.Map;

public interface WxspInfoService {

    //根据openid查询用户
    ArrayList<WxspUserInfo> selectWxspUserByOpenid(String openid);

    //新增微信小程序用户信息
    int insertWxspUserByMap(Map<String,String> map);

    //新增微信小程序用户搜索信息
    int insertWxspUserSearchByMap(Map<String,Object> map);

    int updateWxspUserByMap(Map<String,String> map);
}
