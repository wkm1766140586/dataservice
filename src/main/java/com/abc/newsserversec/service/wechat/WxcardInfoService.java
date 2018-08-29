package com.abc.newsserversec.service.wechat;

import com.abc.newsserversec.model.wechat.WxspUserInfo;

import java.util.ArrayList;
import java.util.Map;

public interface WxcardInfoService {
    //根据openid查询用户
    WxspUserInfo selectUserByOpenid(String openid);

    //新增微信小程序用户信息
    int insertUserByMap(Map<String,String> map);

    ArrayList<Map<String,Object>> selectAllRegion(int pid);

    Map<String,Object> selectRegionById(int id);
}
