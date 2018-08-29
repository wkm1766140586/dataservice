package com.abc.newsserversec.mapper.user;

import com.abc.newsserversec.model.user.UserurljumpInfo;
import org.apache.ibatis.annotations.Mapper;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

@Mapper
public interface UserurljumpInfoMapper {

    int insertUserurljumpInfo(Map<String, Object> map);

    ArrayList<UserurljumpInfo> selectUserurljumpInfoById(long userid);

    int updateIdById(Map<String, Object> map);
}
