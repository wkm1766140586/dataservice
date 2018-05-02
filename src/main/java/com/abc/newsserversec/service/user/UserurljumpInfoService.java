package com.abc.newsserversec.service.user;


import com.abc.newsserversec.model.user.UserurljumpInfo;

import java.util.ArrayList;
import java.util.Map;

public interface UserurljumpInfoService {


    //新增数据
    int insertUserurljumpInfo(Map<String, Object> map);

    //根据用户id查询数据
    ArrayList<UserurljumpInfo> selectUserurljumpInfoById(long userid);
}
