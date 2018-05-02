package com.abc.newsserversec.service.user.imp;

import com.abc.newsserversec.mapper.user.UserInfoMapper;
import com.abc.newsserversec.mapper.user.UserloginInfoMapper;
import com.abc.newsserversec.model.user.UserInfo;
import com.abc.newsserversec.service.user.UserInfoService;
import com.abc.newsserversec.service.user.UserloginInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserloginInfoServiceImp implements UserloginInfoService {

    @Autowired
    private UserloginInfoMapper userloginInfoMapper;


    @Override
    public int insertUserloginInfo(Map<String, Object> map) {
        return userloginInfoMapper.insertUserloginInfo(map);
    }
}
