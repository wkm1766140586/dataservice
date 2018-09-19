package com.abc.newsserversec.service.user;

import com.abc.newsserversec.model.user.UserBusiness;

import java.util.ArrayList;
import java.util.Map;

public interface UserBusinessService {
    int insertUserBusiness(Map<String, Object> map);
    int updateUserBusiness(Map<String, Object> map);
    ArrayList<UserBusiness> selectUserBusinessByCondition(Map<String, Object> map);
    int updateidById(Map<String, Object> map);
}
