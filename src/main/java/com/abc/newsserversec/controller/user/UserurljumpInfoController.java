package com.abc.newsserversec.controller.user;

import com.abc.newsserversec.model.user.UserInfo;
import com.abc.newsserversec.model.user.UserurljumpInfo;
import com.abc.newsserversec.service.user.UserInfoService;
import com.abc.newsserversec.service.user.UserurljumpInfoService;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
public class UserurljumpInfoController {

    @Autowired
    private UserurljumpInfoService userurljumpInfoService;

    @Autowired
    private UserInfoService userInfoService;

    /**
     * 新增用户跳转页面信息数据
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/userurljumpInfo/insertUserurljumpInfo")
    public String insertUserurljumpInfo(HttpServletRequest request, HttpServletResponse response){
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
        if(username == null && openid == null) return "fail";
        String beforeurl = request.getParameter("beforeurl");
        String afterurl = request.getParameter("afterurl");
        String clickname = request.getParameter("clickname");

        Map<String, Object> map = new HashMap<>();
        map.put("username",username);
        map.put("openid",openid);
        UserInfo userInfo = userInfoService.selectUserInfoByCondition(map);
        if(userInfo != null) {
            long userid = userInfo.getId();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = df.format(new Date());
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("userid", userid);
            dataMap.put("beforeurl",beforeurl);
            dataMap.put("afterurl",afterurl);
            dataMap.put("clickname",clickname);
            dataMap.put("createdate",date);

            int count = userurljumpInfoService.insertUserurljumpInfo(dataMap);
            if(count == 1){ return "success"; }
            else { return "fail"; }
        }else{
            return "fail";
        }
    }

    /**
     * 根据用户名查询数据
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/userurljumpInfo/selectUserurljumpInfoByName")
    public String selectUserurljumpInfoByName(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");

        String username = request.getParameter("username");
        String openid = request.getParameter("openid");
        if (username != null) {
            if (username.equals("")) {
                username = null;
            }
        }
        if (openid != null) {
            if (openid.equals("")) {
                openid = null;
            }
        }
        if (username == null && openid == null) return "fail";
        Map<String, Object> map = new HashMap<>();
        map.put("username",username);
        map.put("openid",openid);
        UserInfo userInfo = userInfoService.selectUserInfoByCondition(map);
        if(userInfo != null) {
            long userid = userInfo.getId();
            ArrayList<UserurljumpInfo> userurljumpInfos = userurljumpInfoService.selectUserurljumpInfoById(userid);
            return new GsonBuilder().create().toJson(userurljumpInfos);
        }else{
            return "fail";
        }
    }

}
