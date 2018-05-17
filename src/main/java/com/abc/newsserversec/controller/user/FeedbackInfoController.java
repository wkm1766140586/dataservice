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

        String userid_string = request.getParameter("userid");
        if(userid_string == null) return "fail";
        if(userid_string.equals("")) return "fail";

        String weburl = request.getParameter("weburl");
        String question = request.getParameter("question");
        String advice = request.getParameter("advice");

        long userid = Long.valueOf(userid_string);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = df.format(new Date());
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("userid",userid);
        dataMap.put("weburl",weburl);
        dataMap.put("question",question);
        dataMap.put("advice",advice);
        dataMap.put("createdate",date);

        int count = feedbackInfoService.insertFeedbackInfo(dataMap);
        if(count == 1) { return "success"; }
        else{ return "fail"; }

    }

}
