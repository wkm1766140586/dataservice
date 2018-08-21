package com.abc.newsserversec.controller.user;

import com.abc.newsserversec.common.CusAccessObjectUtil;
import com.abc.newsserversec.model.common.IpGeograAddress;
import com.abc.newsserversec.model.user.UserInfo;
import com.abc.newsserversec.service.user.UserInfoService;
import com.abc.newsserversec.service.user.UserloginInfoService;
import com.google.gson.GsonBuilder;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

    //@ModelAttribute("user") User user
    @InitBinder({"user"})
    public void initBinderUser(WebDataBinder  binder){
        binder.setFieldDefaultPrefix("user.");
    }
    /**
     * 普通方式：用户注册方法
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/method/commonUserRegisterOld")
    public String commonUserRegisterOld(HttpServletRequest request, HttpServletResponse response) {
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
                    temp.put("email",userInfo1.getEmail());
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
        dataMap.put("email",username);
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
                map.put("email",username);
                UserInfo userInfo1 = userInfoService.selectUserInfoByCondition(map);
                if(userInfo1 != null){
                    long userid = userInfo1.getId();
                    insertUserloginInfo(request, userid);

                    Map<String,Object> temp = new HashMap<>();
                    temp.put("userid",userInfo1.getId());
                    temp.put("username",userInfo1.getUsername());
                    temp.put("email",userInfo1.getEmail());
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
     * 检测注册的邮箱是否存在
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/method/checkUserExist")
    public String checkUserExist(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        String username = request.getParameter("username");
        if(username == null){ return "fail"; }
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("email",username);
        UserInfo userInfo = userInfoService.selectUserInfoByCondition(dataMap);
        if(userInfo != null){
            return "exist";
        }else{
            return "not exist";
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
        dataMap.put("email", username);
        dataMap.put("password", password);
        UserInfo userInfo = null;
        userInfo = userInfoService.selectUserInfoByCondition(dataMap);
        if(userInfo == null){
            Map<String, Object> map = new HashMap<>();
            map.put("username", username);
            map.put("password", password);
            userInfo = userInfoService.selectUserInfoByCondition(map);
        }

        if(userInfo != null){
            long userid = userInfo.getId();
            userInfoService.updateLoginCountById(userid);
            insertUserloginInfo(request, userid);

            Map<String,Object> temp = new HashMap<>();
            temp.put("userid",userInfo.getId());
            temp.put("username",userInfo.getUsername());
            temp.put("email",userInfo.getEmail());
            temp.put("nickname",userInfo.getNickname());
            return new GsonBuilder().create().toJson(temp);
        }else{
            return "fail";
        }
    }

    /**
     * 用户验证码快捷登录
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/method/fastUserLogin")
    public String fastUserLogin(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        String username = request.getParameter("username");
        if(username == null){ return "fail"; }
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("email", username);
        UserInfo userInfo = userInfoService.selectUserInfoByCondition(dataMap);
        if(userInfo == null){//新增用户
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = df.format(new Date());
            dataMap.put("createdate", date);
            int count = userInfoService.insertUserInfo(dataMap);
            if(count != 1){ return "fail"; }
            else{
                Map<String, Object> map = new HashMap<>();
                map.put("email",username);
                UserInfo userInfo1 = userInfoService.selectUserInfoByCondition(map);
                if(userInfo1 != null){
                    long userid = userInfo1.getId();
                    insertUserloginInfo(request, userid);

                    Map<String,Object> temp = new HashMap<>();
                    temp.put("userid",userInfo1.getId());
                    temp.put("username",userInfo1.getUsername());
                    temp.put("email",userInfo1.getEmail());
                    temp.put("nickname",userInfo1.getNickname());
                    return new GsonBuilder().create().toJson(temp);
                }else {
                    return "fail";
                }
            }
        }else{
            long userid = userInfo.getId();
            insertUserloginInfo(request, userid);

            Map<String,Object> temp = new HashMap<>();
            temp.put("userid",userInfo.getId());
            temp.put("username",userInfo.getUsername());
            temp.put("email",userInfo.getEmail());
            temp.put("nickname",userInfo.getNickname());
            return new GsonBuilder().create().toJson(temp);
        }
    }

    /**
     * 修改密码
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/method/updatePsw")
    public String updateUserPsw(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        if(username == null || password == null){return "fail";}
        if(username.equals("") || password.equals("")){return "fail";}

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("email", username);

        UserInfo userInfo = userInfoService.selectUserInfoByCondition(dataMap);
        if(userInfo == null){
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = df.format(new Date());
            dataMap.put("password",password);
            dataMap.put("createdate", date);
            int count = userInfoService.insertUserInfo(dataMap);
            if(count != 1){return "fail";}
        }else{
            dataMap.put("id",userInfo.getId());
            dataMap.put("password",password);
            userInfoService.updateUserInfo(dataMap);
        }
        return "success";
    }

    /**
     * 根据userID获的用户的信息
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/method/userCenterInfo")
    public String userCenterInfo(HttpServletRequest request, HttpServletResponse response){
        response.setHeader("Access-Control-Allow-Origin", "*");
        String userid = request.getParameter("userid");
        if(userid == null){return "fail";}
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("id", Long.parseLong(userid));
        UserInfo userInfo = userInfoService.selectUserInfoByCondition(dataMap);
        System.out.println(new GsonBuilder().create().toJson(userInfo));
        if (userInfo != null){
            return new GsonBuilder().create().toJson(userInfo);
        }else{
            return "fail";
        }
    }

    @RequestMapping("/method/queryUserInfo")
    public String queryUserInfo(HttpServletRequest request, HttpServletResponse response){
        response.setHeader("Access-Control-Allow-Origin", "*");
        String unionid = request.getParameter("unionid");
        if(unionid == null){return "fail";}
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("unionid", unionid);
        UserInfo userInfo = userInfoService.selectUserInfoByCondition(dataMap);
        System.out.println(new GsonBuilder().create().toJson(userInfo));
        if (userInfo != null){
            return new GsonBuilder().create().toJson(userInfo);
        }else{
            return "fail";
        }
    }
    /**
     * 修改用户的个人信息
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/method/updateUserInfo")
    public String updateUserInfo(HttpServletRequest request, HttpServletResponse response){
        response.setHeader("Access-Control-Allow-Origin", "*");
        String userid = request.getParameter("userid");
        String realname = request.getParameter("realname");
        String mobile = request.getParameter("mobile");
        String wechatnum = request.getParameter("wechatnum");
        String companyname = request.getParameter("companyname");
        String companyaddress = request.getParameter("companyaddress");
        String department = request.getParameter("department");
        String job = request.getParameter("job");
        String email = request.getParameter("email");
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("id", Integer.parseInt(userid));
        dataMap.put("realname", realname);
        dataMap.put("mobilephone", mobile);
        dataMap.put("wechatnum",wechatnum);
        dataMap.put("companyname", companyname);
        dataMap.put("companyaddress", companyaddress);
        dataMap.put("department", department);
        dataMap.put("job", job);
        dataMap.put("email",email);
        userInfoService.updateUserInfo(dataMap);
        return "success";
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
        String unionid = request.getParameter("unionid");
        String nickname = request.getParameter("nickname");
        String sex = request.getParameter("sex");
        String headimgurl = request.getParameter("headimgurl");
        if(openid == null || nickname == null || sex == null){ return "fail"; }
        if(openid.equals("")){ return "fail"; }

        if(sex.equals("0")){ sex = "未知"; }
        else if(sex.equals("1")){ sex = "男"; }
        else { sex = "女"; }

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("unionid",unionid);
        UserInfo userInfoU = userInfoService.selectUserInfoByCondition(dataMap);
        Map<String, Object> map = new HashMap<>();
        map.put("openid",openid);
        UserInfo userInfoO = userInfoService.selectUserInfoByCondition(map);
        if(userInfoU == null){
            if(userInfoO == null){//新用户
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String date = df.format(new Date());
                dataMap.put("openid",openid);
                dataMap.put("nickname",nickname);
                dataMap.put("headimg",headimgurl);
                dataMap.put("sex",sex);
                dataMap.put("createdate",date);
                int count = userInfoService.insertUserInfo(dataMap);
                if(count == 1){
                    Map<String, Object> map1 = new HashMap<>();
                    map1.put("unionid",unionid);
                    UserInfo userInfo1 = userInfoService.selectUserInfoByCondition(map1);
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
            }else{//老的微信用户
                long userid = userInfoO.getId();
                userInfoService.updateLoginCountById(userid);
                updateUserInfo(nickname,sex,headimgurl,unionid,openid,userid);
                insertUserloginInfo(request,userid);
                Map<String,Object> temp = new HashMap<>();
                temp.put("userid",userInfoO.getId());
                temp.put("username",userInfoO.getUsername());
                temp.put("nickname",userInfoO.getNickname());
                return new GsonBuilder().create().toJson(temp);
            }
        }else{
            if(userInfoO == null){//仅是小程序用户
                long userid = userInfoU.getId();
                //userInfoService.updateLoginCountById(userid);
                updateUserInfo(nickname,sex,headimgurl,unionid,openid,userid);
                insertUserloginInfo(request,userid);
                Map<String,Object> temp = new HashMap<>();
                temp.put("userid",userInfoU.getId());
                temp.put("username",userInfoU.getUsername());
                temp.put("nickname",userInfoU.getNickname());
                return new GsonBuilder().create().toJson(temp);
            }else{
                long useridO = userInfoO.getId();
                long useridU = userInfoU.getId();
                if(useridO == useridU){//既是小程序用户也是微信登录用户
                    userInfoService.updateLoginCountById(useridO);
                    updateUserInfo(nickname,sex,headimgurl,unionid,openid,useridO);
                    insertUserloginInfo(request,useridO);
                    Map<String,Object> temp = new HashMap<>();
                    temp.put("userid",userInfoU.getId());
                    temp.put("username",userInfoU.getUsername());
                    temp.put("nickname",userInfoU.getNickname());
                    return new GsonBuilder().create().toJson(temp);
                }else{
                    userInfoService.updateLoginCountById(useridO);
                    updateUserInfo(nickname,sex,headimgurl,unionid,openid,useridO);
                    insertUserloginInfo(request,useridO);
                    userInfoService.deleteUserById(useridU);
                    Map<String,Object> temp = new HashMap<>();
                    temp.put("userid",userInfoO.getId());
                    temp.put("username",userInfoO.getUsername());
                    temp.put("nickname",userInfoO.getNickname());
                    return new GsonBuilder().create().toJson(temp);
                }
            }
        }
        /*if(userInfo == null){
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = df.format(new Date());
            dataMap.put("openid",openid);
            dataMap.put("nickname",nickname);
            dataMap.put("headimg",headimgurl);
            dataMap.put("sex",sex);
            dataMap.put("createdate",date);
            int count = userInfoService.insertUserInfo(dataMap);
            if(count == 1){
                Map<String, Object> map = new HashMap<>();
                map.put("unionid",unionid);
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
            updateUserInfo(nickname,sex,headimgurl,unionid,userid);
            insertUserloginInfo(request,userid);
            Map<String,Object> temp = new HashMap<>();
            temp.put("userid",userInfo.getId());
            temp.put("username",userInfo.getUsername());
            temp.put("nickname",userInfo.getNickname());
            return new GsonBuilder().create().toJson(temp);
        }*/
    }

    private int insertUserloginInfo(HttpServletRequest request,long userid){
        String ip = CusAccessObjectUtil.getIpAddress(request);
        String json_result = "";
        try {
            json_result = CusAccessObjectUtil.getAddresses("ip=" + ip, "gb2312");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        JSONObject json = JSONObject.fromObject(json_result.substring(34,json_result.length()-3));
        String region = json.get("pro").toString();
        String city = json.get("city").toString();
        //IpGeograAddress ipGeograAddress = new GsonBuilder().create().fromJson(json_result, IpGeograAddress.class);
        //String region = String.valueOf(ipGeograAddress.data.get("region"));
        //String city = String.valueOf(ipGeograAddress.data.get("city"));
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

    //微信登录修改个人信息
    private void updateUserInfo(String nickname,String sex,String headimgurl,String unionid,String openid,long userid){
        Map<String,Object> dataMap = new HashMap<>();
        dataMap.put("unionid",unionid);
        dataMap.put("openid",openid);
        dataMap.put("nickname",nickname);
        dataMap.put("headimg",headimgurl);
        dataMap.put("sex",sex);
        dataMap.put("id",userid);
        userInfoService.updateUserInfo(dataMap);
    }
}
