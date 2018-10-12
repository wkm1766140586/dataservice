package com.abc.newsserversec.service.user.imp;

import com.abc.newsserversec.mapper.user.UserCardMapper;
import com.abc.newsserversec.model.user.UserCard;
import com.abc.newsserversec.service.user.UserCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;

@Service
public class UserCardServiceImp implements UserCardService{
    @Autowired
    private UserCardMapper userCardMapper;
    @Override
    public UserCard selectUserCardByCondition(Map<String, Object> map) {
        return userCardMapper.selectUserCardByCondition(map);
    }

    @Override
    public int insertUserCard(Map<String, Object> map) {
        return userCardMapper.insertUserCard(map);
    }

    @Override
    public int updateUserCard(Map<String, Object> map) {
        return userCardMapper.updateUserCard(map);
    }

    @Override
    public void deleteUserCardById(long userid) {
        userCardMapper.deleteUserCardById(userid);
    }

    @Override
    public int updateidById(Map<String, Object> map) {
        return userCardMapper.updateidById(map);
    }

    @Override
    public ArrayList<Map<String, Object>> selectUserheadimgByCompanyName(String companyname) {
        return userCardMapper.selectUserheadimgByCompanyName(companyname);
    }
}
