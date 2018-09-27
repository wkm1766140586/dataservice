package com.abc.newsserversec.service.user;

import com.abc.newsserversec.model.user.UserCard;

import java.util.Map;

public interface UserCardService {
    UserCard selectUserCardByCondition(Map<String, Object> map);

    int insertUserCard(Map<String, Object> map);

    int updateUserCard(Map<String,Object> map);

    void deleteUserCardById(long userid);

    int updateidById(Map<String, Object> map);
}
