package com.abc.newsserversec.mapper.user;

import com.abc.newsserversec.model.user.UserCard;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

/**
 * 用户名片信息
 */
@Mapper
public interface UserCardMapper {

    UserCard selectUserCardByCondition(Map<String, Object> map);

    int insertUserCard(Map<String, Object> map);

    int updateUserCard(Map<String,Object> map);

    void deleteUserCardById(long userid);

    int updateidById(Map<String, Object> map);
}
