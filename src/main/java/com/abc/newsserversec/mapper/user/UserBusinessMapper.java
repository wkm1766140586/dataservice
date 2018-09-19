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
}
