package com.abc.newsserversec.mapper.wechat;

import com.abc.newsserversec.model.wechat.WxspUserInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface WxspInfoMapper {

    WxspUserInfo selectWxspUserByOpenid(String openid);

    int insertWxspUserByMap(Map<String, String> map);

    int insertWxspUserSearchByMap(Map<String,Object> map);
}
