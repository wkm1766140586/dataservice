package com.abc.newsserversec.service.user.imp;

import com.abc.newsserversec.mapper.user.UsersearchInfoMapper;
import com.abc.newsserversec.mapper.user.UserurljumpInfoMapper;
import com.abc.newsserversec.model.user.UserurljumpInfo;
import com.abc.newsserversec.service.user.UsersearchInfoService;
import com.abc.newsserversec.service.user.UserurljumpInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;

@Service
public class UserurljumpInfoServiceImp implements UserurljumpInfoService {

    @Autowired
    private UserurljumpInfoMapper userurljumpInfoMapper;

    @Override
    public int insertUserurljumpInfo(Map<String, Object> map) {
        return userurljumpInfoMapper.insertUserurljumpInfo(map);
    }

    @Override
    public ArrayList<UserurljumpInfo> selectUserurljumpInfoById(long userid) {
        return userurljumpInfoMapper.selectUserurljumpInfoById(userid);
    }
}
