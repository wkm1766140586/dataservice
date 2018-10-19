package com.abc.newsserversec.service.user;

import com.abc.newsserversec.model.user.UserCard;
import com.abc.newsserversec.model.user.UserUploadPicture;

import java.util.ArrayList;
import java.util.Map;

public interface UserUploadPictureService {

    //上传产品图片
    //插入产品上传图片信息
    int insertUserUploadPicture(Map<String, Object> map);

    //根据条件获得产品上传审核信息
    ArrayList<UserUploadPicture> selectAuditByCondition(Map<String,Object> map);

    //根据条件获得产品上传数量
    int selectAuditCountByCondition(Map<String,Object> map);

    //撤回产品上图信息
    int recallAuditById(long id);

    //上传企业资质图片
    //插入信息
    int insertUploadCompanyCertificate(Map<String, Object> map);
}
