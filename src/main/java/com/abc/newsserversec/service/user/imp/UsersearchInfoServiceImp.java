package com.abc.newsserversec.service.user.imp;

import com.abc.newsserversec.mapper.user.UsersearchInfoMapper;
import com.abc.newsserversec.service.user.UsersearchInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UsersearchInfoServiceImp implements UsersearchInfoService {

    @Autowired
    private UsersearchInfoMapper usersearchInfoMapper;

    @Override
    public int insertUsersearchInfo(Map<String, Object> map) {
        return usersearchInfoMapper.insertUsersearchInfo(map);
    }
}
