package com.abc.newsserversec.controller.user;

import com.abc.newsserversec.model.user.UserInfo;
import com.abc.newsserversec.service.user.FeedbackInfoService;
import com.abc.newsserversec.service.user.UserInfoService;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
public class FeedbackInfoController {

    @Autowired
    private FeedbackInfoService feedbackInfoService;

    /**
     * 新增反馈信息
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/method/insertFeedbackInfo")
    public String insertFeedbackInfo(HttpServletRequest request, HttpServletResponse response){
        response.setHeader("Access-Control-Allow-Origin", "*");

        String userid = request.getParameter("userid");
        String weburl = request.getParameter("weburl");
        String content = request.getParameter("content");
        String contactway = request.getParameter("contactway");

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = df.format(new Date());
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("userid",userid);
        dataMap.put("weburl",weburl);
        dataMap.put("content",content);
        dataMap.put("contactway",contactway);
        dataMap.put("createdate",date);

        int count = feedbackInfoService.insertFeedbackInfo(dataMap);
        if(count == 1) { return "success"; }
        else{ return "fail"; }

    }

}
