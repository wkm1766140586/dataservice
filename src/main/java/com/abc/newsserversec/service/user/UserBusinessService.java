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
    /*根据userid和公司名称删除*/
    int deleteBussiness(Map<String, Object> map);
    int selectCountByCondition(Map<String, Object> map);
    ArrayList<Map<String,Object>> selectUserCardByProductId(Map<String, Object> map);
    //根据产品ID查询出产品的负责人的头像（名片）
    ArrayList<Map<String,Object>> selectUserheadimgByProductId(Map<String,Object> map);

    //根据企业名称查询该企业名片人员的头像（名片）
    ArrayList<Map<String,Object>> selectUserheadimgByCompanyName(String companyname);

    //根据用户id获得用户负责产品的信息（名片）
    ArrayList<Map<String,Object>> selectProductInfosByUserid(long userid);
}
