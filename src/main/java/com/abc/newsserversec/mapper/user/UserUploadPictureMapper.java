package com.abc.newsserversec.mapper.user;

import com.abc.newsserversec.model.user.UploadCompanyPicture;
import com.abc.newsserversec.model.user.UploadInfo;
import com.abc.newsserversec.model.user.UserCard;
import com.abc.newsserversec.model.user.UserUploadPicture;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.Map;

/**
 * 用户名片信息
 */
@Mapper
public interface UserUploadPictureMapper {

    int insertUserUploadPicture(Map<String, Object> map);

    ArrayList<UserUploadPicture> selectProductAuditByCondition(Map<String,Object> map);

    int selectProductAuditCountByCondition(Map<String,Object> map);

    int updateProductAuditByCondition(Map<String,Object> map);


    int insertUploadCompanyCertificate(Map<String, Object> map);

    ArrayList<UploadCompanyPicture> selectCompanyAuditByCondition(Map<String,Object> map);

    int selectCompanyAuditCountByCondition(Map<String,Object> map);

    int updateCompanyAuditByCondition(Map<String,Object> map);


    ArrayList<UploadInfo> selectAuditByCondition(Map<String,Object> map);

    int selectAuditCountByCondition(Map<String,Object> map);
}
