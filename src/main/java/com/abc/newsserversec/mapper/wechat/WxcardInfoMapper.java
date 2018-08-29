package com.abc.newsserversec.mapper.wechat;

import com.abc.newsserversec.model.wechat.WxspUserInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.Map;

@Mapper
public interface WxcardInfoMapper {
    WxspUserInfo selectUserByOpenid(String openid);

    int insertUserByMap(Map<String, String> map);

    ArrayList<Map<String,Object>> selectAllRegion(int pid);

   Map<String,Object> selectRegionById(int id);
}
