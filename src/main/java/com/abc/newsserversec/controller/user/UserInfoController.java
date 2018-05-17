package com.abc.newsserversec.controller.user;

import com.abc.newsserversec.common.CusAccessObjectUtil;
import com.abc.newsserversec.model.common.IpGeograAddress;
import com.abc.newsserversec.model.user.UserInfo;
import com.abc.newsserversec.service.user.UserInfoService;
import com.abc.newsserversec.service.user.UserloginInfoService;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.websocket.Session;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private UserloginInfoService userloginInfoService;

    /**
     * 普通方式：用户注册方法
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/method/commonUserRegister")
    public String commonUserRegister(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if(username == null || password == null){ return "fail"; }
        if(username.equals("") || password.equals("")){ return "fail"; }

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("username",username);
        UserInfo userInfo = userInfoService.selectUserInfoByCondition(dataMap);
        if(userInfo == null){
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = df.format(new Date());
            dataMap.put("password", password);
            dataMap.put("createdate", date);

            int count = userInfoService.insertUserInfo(dataMap);
            if(count != 1){ return "fail"; }
            else{
                Map<String, Object> map = new HashMap<>();
                map.put("username",username);
                UserInfo userInfo1 = userInfoService.selectUserInfoByCondition(map);
                if(userInfo1 != null){
                    long userid = userInfo1.getId();
                    insertUserloginInfo(request, userid);

                    Map<String,Object> temp = new HashMap<>();
                    temp.put("userid",userInfo1.getId());
                    temp.put("username",userInfo1.getUsername());
                    temp.put("nickname",userInfo1.getNickname());
                    return new GsonBuilder().create().toJson(temp);
                }else {
                    return "fail";
                }
            }

        }else{
            return "exist";
        }

    }

    /**
     * 普通方式：用户登录方法
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/method/commonUserLogin")
    public String commonUserLogin(HttpServletRequest request, HttpServletResponse response){
        response.setHeader("Access-Control-Allow-Origin", "*");
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if(username == null || password == null){ return "fail"; }
        if(username.equals("") || password.equals("")){ return "fail"; }

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("username", username);
        dataMap.put("password", password);
        UserInfo userInfo = userInfoService.selectUserInfoByCondition(dataMap);

        if(userInfo != null){
            long userid = userInfo.getId();
            userInfoService.updateLoginCountById(userid);
            insertUserloginInfo(request, userid);

            Map<String,Object> temp = new HashMap<>();
            temp.put("userid",userInfo.getId());
            temp.put("username",userInfo.getUsername());
            temp.put("nickname",userInfo.getNickname());
            return new GsonBuilder().create().toJson(temp);
        }else{
            return "fail";
        }
    }

    /**
     * 微信方式：登录
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/method/wechatUserLogin")
    public String wechatUserLogin(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");

        String openid = request.getParameter("openid");
        String nickname = request.getParameter("nickname");
        String sex = request.getParameter("sex");
        if(openid == null || nickname == null || sex == null){ return "fail"; }
        if(openid.equals("")){ return "fail"; }

        if(sex.equals("0")){ sex = "未知"; }
        else if(sex.equals("1")){ sex = "男"; }
        else { sex = "女"; }

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("openid",openid);
        UserInfo userInfo = userInfoService.selectUserInfoByCondition(dataMap);
        if(userInfo == null){
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = df.format(new Date());
            dataMap.put("nickname",nickname);
            dataMap.put("sex",sex);
            dataMap.put("createdate",date);
            int count = userInfoService.insertUserInfo(dataMap);
            if(count == 1){
                Map<String, Object> map = new HashMap<>();
                map.put("openid",openid);
                UserInfo userInfo1 = userInfoService.selectUserInfoByCondition(map);
                if(userInfo1 != null){
                    long userid = userInfo1.getId();
                    insertUserloginInfo(request, userid);

                    Map<String,Object> temp = new HashMap<>();
                    temp.put("userid",userInfo1.getId());
                    temp.put("username",userInfo1.getUsername());
                    temp.put("nickname",userInfo1.getNickname());
                    return new GsonBuilder().create().toJson(temp);
                }else {
                    return "fail";
                }
            }else{
                return "fail";
            }
        }else{
            long userid = userInfo.getId();
            userInfoService.updateLoginCountById(userid);
            insertUserloginInfo(request,userid);

            Map<String,Object> temp = new HashMap<>();
            temp.put("userid",userInfo.getId());
            temp.put("username",userInfo.getUsername());
            temp.put("nickname",userInfo.getNickname());
            return new GsonBuilder().create().toJson(temp);
        }
    }

    private int insertUserloginInfo(HttpServletRequest request,long userid){
        String ip = CusAccessObjectUtil.getIpAddress(request);
        String json_result = "";
        try {
            json_result = CusAccessObjectUtil.getAddresses("ip=" + ip, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        IpGeograAddress ipGeograAddress = new GsonBuilder().create().fromJson(json_result, IpGeograAddress.class);
        String region = String.valueOf(ipGeograAddress.data.get("region"));
        String city = String.valueOf(ipGeograAddress.data.get("city"));
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = df.format(new Date());
        Map<String, Object> map = new HashMap<>();
        map.put("userid", userid);
        map.put("userip", ip);
        map.put("userregion", region);
        map.put("usercity", city);
        map.put("createdate",date);
        return userloginInfoService.insertUserloginInfo(map);
    }
}
