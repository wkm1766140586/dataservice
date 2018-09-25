package com.abc.newsserversec.mapper.wechat;

import com.abc.newsserversec.model.wechat.WxspUserInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.Map;

@Mapper
public interface WxspInfoMapper {

    ArrayList<WxspUserInfo> selectWxspUserByOpenid(String openid);

    int insertWxspUserByMap(Map<String, String> map);

    int insertWxspUserSearchByMap(Map<String,Object> map);

    int updateWxspUserByMap(Map<String,String> map);

    ArrayList<WxspUserInfo> selectAllUser();
    ArrayList<Map<String,Object>> selectSearchInfoByUserid (long userid);

    int updateUserIds(Map<String,Object> map);
}
