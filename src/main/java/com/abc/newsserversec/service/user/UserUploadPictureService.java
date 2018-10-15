package com.abc.newsserversec.service.user;

import com.abc.newsserversec.model.user.UserCard;
import com.abc.newsserversec.model.user.UserUploadPicture;

import java.util.ArrayList;
import java.util.Map;

public interface UserUploadPictureService {

    int insertUserUploadPicture(Map<String, Object> map);

    ArrayList<UserUploadPicture> selectAuditByCondition(Map<String,Object> map);

    int selectAuditCountByCondition(Map<String,Object> map);

    int recallAuditById(long id);

}
