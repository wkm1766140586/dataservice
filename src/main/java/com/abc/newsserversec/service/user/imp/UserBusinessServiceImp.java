package com.abc.newsserversec.service.user.imp;

import com.abc.newsserversec.mapper.user.UserBusinessMapper;
import com.abc.newsserversec.model.user.UserBusiness;
import com.abc.newsserversec.service.user.UserBusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;

@Service
public class UserBusinessServiceImp implements UserBusinessService {
    @Autowired
    private UserBusinessMapper userBusinessMapper;

    @Override
    public int insertUserBusiness(Map<String, Object> map) {
        return userBusinessMapper.insertUserBusiness(map);
    }

    @Override
    public int updateUserBusiness(Map<String, Object> map) {
        return userBusinessMapper.updateUserBusiness(map);
    }

    @Override
    public ArrayList<UserBusiness> selectUserBusinessByCondition(Map<String, Object> map) {
        return userBusinessMapper.selectUserBusinessByCondition(map);
    }

    @Override
    public int updateidById(Map<String, Object> map) {
        return userBusinessMapper.updateidById(map);
    }

    @Override
    public int deleteByUserId(long userid) {
        return userBusinessMapper.deleteByUserId(userid);
    }

    @Override
    public int deleteBussiness(Map<String, Object> map) {
        return userBusinessMapper.deleteBussiness(map);
    }

    @Override
    public int selectCountByCondition(Map<String, Object> map) {
        return userBusinessMapper.selectCountByCondition(map);
    }

    @Override
    public ArrayList<Map<String, Object>> selectUserCardByProductId(Map<String, Object> map) {
        return userBusinessMapper.selectUserCardByProductId(map);
    }


    @Override
    public ArrayList<Map<String, Object>> selectUserheadimgByProductId(Map<String, Object> map) {
        return userBusinessMapper.selectUserheadimgByProductId(map);
    }

    @Override
    public ArrayList<Map<String, Object>> selectUserheadimgByCompanyName(String companyname) {
        return userBusinessMapper.selectUserheadimgByCompanyName(companyname);
    }

    @Override
    public ArrayList<Map<String, Object>> selectProductInfosByUserid(long userid) {
        return userBusinessMapper.selectProductInfosByUserid(userid);
    }
}
