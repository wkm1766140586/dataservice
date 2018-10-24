package com.abc.newsserversec.mapper.user;

import com.abc.newsserversec.model.user.UserBusiness;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.Map;

/**
 * 用户的业务信息，负责的产品
 */
@Mapper
public interface UserBusinessMapper {
    int insertUserBusiness(Map<String, Object> map);
    int updateUserBusiness(Map<String, Object> map);
    int selectCountByUserId(Map<String, Object> map);
    ArrayList<UserBusiness> selectUserBusinessByCondition(Map<String, Object> map);
    int updateidById(Map<String, Object> map);
    int deleteByUserId(long userid);
    int deleteBussiness(Map<String, Object> map);
    ArrayList<Map<String,Object>> selectUserCardByProductId(Map<String, Object> map);
    int selectCountByCondition(Map<String, Object> map);
    ArrayList<Map<String,Object>> selectUserheadimgByProductId(Map<String,Object> map);

    ArrayList<Map<String,Object>> selectUserheadimgByCompanyName(String companyname);

    ArrayList<Map<String,Object>> selectProductInfosByUserid(long userid);
}
