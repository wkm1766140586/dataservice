package com.abc.newsserversec.mapper.user;

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

    ArrayList<UserUploadPicture> selectAuditByCondition(Map<String,Object> map);

    int selectAuditCountByCondition(Map<String,Object> map);

    int recallAuditById(long id);
}
