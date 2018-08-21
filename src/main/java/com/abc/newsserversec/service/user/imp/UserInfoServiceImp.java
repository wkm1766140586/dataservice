package com.abc.newsserversec.service.user.imp;

import com.abc.newsserversec.mapper.user.UserInfoMapper;
import com.abc.newsserversec.model.user.UserInfo;
import com.abc.newsserversec.service.user.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserInfoServiceImp implements UserInfoService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public UserInfo selectUserInfoByCondition(Map<String, Object> map) {
        return userInfoMapper.selectUserInfoByCondition(map);
    }

    @Override
    public int updateLoginCountById(long id) {
        return userInfoMapper.updateLoginCountById(id);
    }

    @Override
    public int insertUserInfo(Map<String, Object> map) {
        return userInfoMapper.insertUserInfo(map);
    }

    @Override
    public int updateUserInfo(Map<String, Object> map) {
        return userInfoMapper.updateUserInfo(map);
    }

    @Override
    public void deleteUserById(long id) {
        userInfoMapper.deleteUserById(id);
    }

}
