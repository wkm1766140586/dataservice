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
}
