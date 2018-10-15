package com.abc.newsserversec.service.user;

import com.abc.newsserversec.model.user.UserCard;

import java.util.ArrayList;
import java.util.Map;

public interface UserCardService {
    UserCard selectUserCardByCondition(Map<String, Object> map);

    int insertUserCard(Map<String, Object> map);

    int updateUserCard(Map<String,Object> map);

    void deleteUserCardById(long userid);

    int updateidById(Map<String, Object> map);

    ArrayList<Map<String,Object>> selectUserheadimgByCompanyName(String companyname);

    ArrayList<Map<String,Object>> selectUserCardByCompanyName(Map<String, Object> map);

    int selectCountByCompanyName(Map<String, Object> map);
}
