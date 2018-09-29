package com.abc.newsserversec.service.user;

import com.abc.newsserversec.model.user.UserBusiness;

import java.util.ArrayList;
import java.util.Map;

public interface UserBusinessService {
    int insertUserBusiness(Map<String, Object> map);
    int updateUserBusiness(Map<String, Object> map);
    ArrayList<UserBusiness> selectUserBusinessByCondition(Map<String, Object> map);
    int updateidById(Map<String, Object> map);
    int deleteByUserId(long userid);

    //根据产品ID查询出产品的负责人的头像（产品列表页使用）
    ArrayList<Map<String,Object>> selectUserheadimgByProductId(Map<String,Object> map);
}
