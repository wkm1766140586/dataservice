package com.abc.newsserversec.service.user.imp;

import com.abc.newsserversec.mapper.user.FeedbackInfoMapper;
import com.abc.newsserversec.mapper.user.UserloginInfoMapper;
import com.abc.newsserversec.service.user.FeedbackInfoService;
import com.abc.newsserversec.service.user.UserloginInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class FeedbackInfoServiceImp implements FeedbackInfoService {

    @Autowired
    private FeedbackInfoMapper feedbackInfoMapper;

    @Override
    public int insertFeedbackInfo(Map<String, Object> map) {
        return feedbackInfoMapper.insertFeedbackInfo(map);
    }
}
