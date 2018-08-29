package com.abc.newsserversec.service.user.imp;

import com.abc.newsserversec.mapper.user.UserloginInfoMapper;
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

    @Override
    public int updateIdById(Map<String, Object> map) {
        return userloginInfoMapper.updateIdById(map);
    }
}
