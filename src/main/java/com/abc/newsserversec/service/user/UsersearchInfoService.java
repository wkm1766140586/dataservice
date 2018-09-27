package com.abc.newsserversec.service.user;


import java.util.ArrayList;
import java.util.Map;

public interface UsersearchInfoService {


    //新增数据
    int insertUsersearchInfo(Map<String, Object> map);

    int updateidById(Map<String, Object> map);

    ArrayList<Map<String,Object>> selectUserSearch(long userid);

    ArrayList<String> selectUsersearchWordByCondition(Map<String, Object> map);
}
