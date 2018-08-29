package com.abc.newsserversec.service.user;


import com.abc.newsserversec.model.user.UserInfo;

import java.util.Map;

public interface UserloginInfoService {


    //新增数据
    int insertUserloginInfo(Map<String, Object> map);

    int updateIdById(Map<String, Object> map);
}
