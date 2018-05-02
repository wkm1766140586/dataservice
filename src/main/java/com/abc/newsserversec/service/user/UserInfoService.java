package com.abc.newsserversec.service.user;


import com.abc.newsserversec.model.user.UserInfo;

import java.util.ArrayList;
import java.util.Map;

public interface UserInfoService {


    //根据条件查询用户信息
    UserInfo selectUserInfoByCondition(Map<String, Object> map);

    //根据用户id更新用户登录次数
    int updateLoginCountById(long id);

    //新增用户信息
    int insertUserInfo(Map<String, Object> map);

}
