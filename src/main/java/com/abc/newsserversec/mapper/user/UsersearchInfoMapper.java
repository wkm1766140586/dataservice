package com.abc.newsserversec.mapper.user;

import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.Map;

@Mapper
public interface UsersearchInfoMapper {

    int insertUsersearchInfo(Map<String, Object> map);

    int updateidById(Map<String, Object> map);

    ArrayList<Map<String,Object>> selectUserSearch(long userid);

    ArrayList<String> selectUsersearchWordByCondition(Map<String, Object> map);
}
