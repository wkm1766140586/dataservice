package com.abc.newsserversec.mapper.user;

import com.abc.newsserversec.model.user.UserInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.Map;

@Mapper
public interface UserInfoMapper {

    UserInfo selectUserInfoByCondition(Map<String, Object> map);

    int updateLoginCountById(long id);

    int insertUserInfo(Map<String, Object> map);

    int updateUserInfo(Map<String,Object> map);
}
