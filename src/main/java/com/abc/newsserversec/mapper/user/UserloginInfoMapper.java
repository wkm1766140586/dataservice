package com.abc.newsserversec.mapper.user;

import com.abc.newsserversec.model.user.UserInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface UserloginInfoMapper {

    int insertUserloginInfo(Map<String, Object> map);
}
