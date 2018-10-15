package com.abc.newsserversec.service.user.imp;

import com.abc.newsserversec.mapper.user.UserCardMapper;
import com.abc.newsserversec.mapper.user.UserUploadPictureMapper;
import com.abc.newsserversec.model.user.UserCard;
import com.abc.newsserversec.model.user.UserUploadPicture;
import com.abc.newsserversec.service.user.UserCardService;
import com.abc.newsserversec.service.user.UserUploadPictureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;

@Service
public class UserUploadPictureServiceImp implements UserUploadPictureService{
    @Autowired
    private UserUploadPictureMapper userUploadPictureMapper;

    @Override
    public int insertUserUploadPicture(Map<String, Object> map) {
        return userUploadPictureMapper.insertUserUploadPicture(map);
    }

    @Override
    public ArrayList<UserUploadPicture> selectAuditByCondition(Map<String,Object> map) {
        return userUploadPictureMapper.selectAuditByCondition(map);
    }

    @Override
    public int selectAuditCountByCondition(Map<String, Object> map) {
        return userUploadPictureMapper.selectAuditCountByCondition(map);
    }

    @Override
    public int recallAuditById(long id) {
        return userUploadPictureMapper.recallAuditById(id);
    }
}
