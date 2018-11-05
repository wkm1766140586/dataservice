package com.abc.newsserversec.service.user;

import com.abc.newsserversec.model.user.UploadCompanyPicture;
import com.abc.newsserversec.model.user.UploadInfo;
import com.abc.newsserversec.model.user.UserCard;
import com.abc.newsserversec.model.user.UserUploadPicture;

import java.util.ArrayList;
import java.util.Map;

public interface UserUploadPictureService {

    //产品图片
    //插入产品上传图片信息
    int insertUserUploadPicture(Map<String, Object> map);

    //根据条件获得产品上传审核信息
    ArrayList<UserUploadPicture> selectProductAuditByCondition(Map<String,Object> map);

    //根据条件获得产品上传数量
    int selectProductAuditCountByCondition(Map<String,Object> map);

    //撤回信息
    int updateProductAuditByCondition(Map<String,Object> map);

    //删除信息
    int deleteProductAuditById(long id);


    //企业资质
    //插入信息
    int insertUploadCompanyCertificate(Map<String, Object> map);

    //根据条件获得企业上传审核信息
    ArrayList<UploadCompanyPicture> selectCompanyAuditByCondition(Map<String,Object> map);

    //根据条件获得企业上传数量
    int selectCompanyAuditCountByCondition(Map<String,Object> map);

    //撤回信息
    int updateCompanyAuditByCondition(Map<String,Object> map);

    //删除信息
    int deleteCompanyAuditById(long id);


    //消息中心
    //根据条件获得消息中心信息
    ArrayList<UploadInfo> selectAuditByCondition(Map<String,Object> map);

    //根据条件获得消息中心数量
    int selectAuditCountByCondition(Map<String,Object> map);
}
