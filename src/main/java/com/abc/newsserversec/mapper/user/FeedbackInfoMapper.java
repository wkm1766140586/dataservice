package com.abc.newsserversec.mapper.user;

import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface FeedbackInfoMapper {

    int insertFeedbackInfo(Map<String, Object> map);
}
