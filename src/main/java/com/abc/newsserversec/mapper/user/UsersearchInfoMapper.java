package com.abc.newsserversec.mapper.user;

import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface UsersearchInfoMapper {

    int insertUsersearchInfo(Map<String, Object> map);

    int updateidById(Map<String, Object> map);
}
