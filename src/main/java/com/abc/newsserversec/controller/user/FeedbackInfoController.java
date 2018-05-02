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

    @Autowired
    private UserInfoService userInfoService;

    /**
     * 新增反馈信息数据
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/feedbackInfo/insertFeedbackInfo")
    public String insertFeedbackInfo(HttpServletRequest request, HttpServletResponse response){
        response.setHeader("Access-Control-Allow-Origin", "*");

        String username = request.getParameter("username");
        String openid = request.getParameter("openid");
        if(username != null ){
            if(username.equals("")){
                username = null;
            }
        }
        if(openid != null ){
            if(openid.equals("")){
                openid = null;
            }
        }
        if(username == null && openid == null){
            return "fail";
        }
        String weburl = request.getParameter("weburl");
        String question = request.getParameter("question");
        String advice = request.getParameter("advice");

        Map<String, Object> map = new HashMap<>();
        map.put("username",username);
        UserInfo userInfo = userInfoService.selectUserInfoByCondition(map);
        if(userInfo != null) {
            long userid = userInfo.getId();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = df.format(new Date());
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("userid",userid);
            dataMap.put("weburl",weburl);
            dataMap.put("question",question);
            dataMap.put("advice",advice);
            dataMap.put("createdate",date);

            int count = feedbackInfoService.insertFeedbackInfo(dataMap);
            if(count == 1) {
                return "success";
            }else{
                return "fail";
            }
        }else{
            return "fail";
        }
    }

}
