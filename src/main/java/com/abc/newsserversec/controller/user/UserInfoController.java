package com.abc.newsserversec.controller.user;

import com.abc.newsserversec.common.CusAccessObjectUtil;
import com.abc.newsserversec.model.common.IpGeograAddress;
import com.abc.newsserversec.model.user.UserCard;
import com.abc.newsserversec.model.user.UserInfo;
import com.abc.newsserversec.service.user.*;
import com.abc.newsserversec.service.wechat.WxOperCardService;
import com.google.gson.GsonBuilder;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import sun.rmi.runtime.Log;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private WxOperCardService wxOperCardService;

    @Autowired
    private UserloginInfoService userloginInfoService;

    @Autowired
    private UsersearchInfoService usersearchInfoService;

    @Autowired
    private UserurljumpInfoService userurljumpInfoService;

    @Autowired
    private UserBusinessService userBusinessService;
    @Autowired
    private UserCardService userCardService;

    @Autowired
    private UserUploadPictureService userUploadPictureService;

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
            dataMap.put("usertype", "ZH");

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
            dataMap.put("usertype", "ZH");

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
    public long checkUserExist(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        String username = request.getParameter("username");
        if(username == null){ return 0; }
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("email",username);
        UserInfo userInfo = userInfoService.selectUserInfoByCondition(dataMap);
        if(userInfo != null){
            return userInfo.getId();
        }else{
            return 0;
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
            temp.put("openid",userInfo.getOpenid());
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
            dataMap.put("usertype", "ZH");
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
        Map<String, Object> datamap1 = new HashMap<>();
        datamap1.put("userid", Long.parseLong(userid));
        UserCard userCard = userCardService.selectUserCardByCondition(datamap1);
        if (userInfo != null){
            Map<String,Object> map = new HashMap<>();
            map.put("userinfo",userInfo);
            map.put("userCard",userCard);
            return new GsonBuilder().create().toJson(map);
        }else{
            return "fail";
        }
    }

    /**
     * 根据用户ID 查找名片信息
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/method/userCardByUserId")
    public String userCardInfo(HttpServletRequest request, HttpServletResponse response){
        response.setHeader("Access-Control-Allow-Origin", "*");
        String userid = request.getParameter("userid");
        Map<String,Object> dataMap = new HashMap<>();
        dataMap.put("userid", Long.parseLong(userid));
        UserCard userCard = userCardService.selectUserCardByCondition(dataMap);
        if(userCard != null){
            return new GsonBuilder().create().toJson(userCard);
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
        dataMap.put("userid", Integer.parseInt(userid));
        UserCard userCard = userCardService.selectUserCardByCondition(dataMap);
        dataMap.put("realname", realname);
        dataMap.put("mobilephone", mobile);
        dataMap.put("wechatnum",wechatnum);
        dataMap.put("companyname", companyname);
        dataMap.put("companyaddress", companyaddress);
        dataMap.put("department", department);
        dataMap.put("job", job);
        dataMap.put("email",email);
        if(userCard != null){
            userCardService.updateUserCard(dataMap);
        }else{
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = df.format(new Date());
            dataMap.put("createtime",date);
            userCardService.insertUserCard(dataMap);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("iscard",1);
        userInfoService.updateUserInfo(map);
        return "success";
    }

    /**
     * 用于微信绑定账户时，更新信息。删除账号信息
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/method/updateInfo")
    public String updateInfo(HttpServletRequest request, HttpServletResponse response){
        response.setHeader("Access-Control-Allow-Origin", "*");
        String binduserid = request.getParameter("binduserid");
        String wxuserid = request.getParameter("wxuserid");//需要修改
        Map<String, Object> map = new HashMap<>();
        map.put("id",binduserid);
        UserInfo userInfo = userInfoService.selectUserInfoByCondition(map);

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("id", Integer.parseInt(wxuserid));
        dataMap.put("email",userInfo.getEmail());
        dataMap.put("username",userInfo.getUsername());
        dataMap.put("password",userInfo.getPassword());
        dataMap.put("logincount",userInfo.getLogincount());
        dataMap.put("usertype","ZH_WX");
        userInfoService.updateUserInfo(dataMap);

        userInfoService.deleteUserById(Long.parseLong(binduserid));

        Map<String, Object> map1 = new HashMap<>();
        map1.put("userid", binduserid);
        map1.put("val",wxuserid);
        userCardService.updateidById(map1);
        usersearchInfoService.updateidById(map1);
        userloginInfoService.updateIdById(map1);
        userurljumpInfoService.updateIdById(map1);


        Map<String,Object> temp = new HashMap<>();
        temp.put("userid",wxuserid);
        temp.put("username",userInfo.getUsername());
        temp.put("nickname",userInfo.getNickname());
        temp.put("email",userInfo.getEmail());
        return new GsonBuilder().create().toJson(temp);
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
                dataMap.put("usertype","ZH");
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
                        temp.put("email",userInfo1.getEmail());
                        return new GsonBuilder().create().toJson(temp);
                    }else {
                        return "fail";
                    }
                }else{
                    return "fail";
                }
            }else{//老的微信用户、先微信登录，未授权小程序
                long userid = userInfoO.getId();
                userInfoService.updateLoginCountById(userid);
                updateUserInfo(nickname,sex,headimgurl,unionid,openid,userid,"ZH");
                insertUserloginInfo(request,userid);
                Map<String,Object> temp = new HashMap<>();
                temp.put("userid",userInfoO.getId());
                temp.put("username",userInfoO.getUsername());
                temp.put("nickname",userInfoO.getNickname());
                temp.put("email",userInfoO.getEmail());
                return new GsonBuilder().create().toJson(temp);
            }
        }else{
            if(userInfoO == null){//仅是小程序用户，未微信登录过
                long userid = userInfoU.getId();
                userInfoService.updateLoginCountById(userid);
                updateUserInfo(nickname,sex,headimgurl,unionid,openid,userid,"ZH");
                insertUserloginInfo(request,userid);
                Map<String,Object> temp = new HashMap<>();
                temp.put("userid",userInfoU.getId());
                temp.put("username",userInfoU.getUsername());
                temp.put("nickname",userInfoU.getNickname());
                temp.put("email",userInfoU.getEmail());
                return new GsonBuilder().create().toJson(temp);
            }else{//
                long useridO = userInfoO.getId();
                long useridU = userInfoU.getId();
                if(useridO == useridU){//既是小程序用户也是微信登录用户
                    userInfoService.updateLoginCountById(useridO);
                    updateUserInfo(nickname,sex,headimgurl,unionid,openid,useridO,"ZH");
                    insertUserloginInfo(request,useridO);
                    Map<String,Object> temp = new HashMap<>();
                    temp.put("userid",userInfoU.getId());
                    temp.put("username",userInfoU.getUsername());
                    temp.put("nickname",userInfoU.getNickname());
                    temp.put("email",userInfoU.getEmail());
                    return new GsonBuilder().create().toJson(temp);
                }else{
                    userInfoService.updateLoginCountById(useridO);
                    updateUserAllInfo(nickname,sex,headimgurl,unionid,openid,useridO,userInfoU,"ZH");
                    insertUserloginInfo(request,useridO);
                    updateCardRecord(useridU,useridO);
                    userInfoService.deleteUserById(useridU);
                    Map<String,Object> temp = new HashMap<>();
                    temp.put("userid",userInfoO.getId());
                    temp.put("username",userInfoO.getUsername());
                    temp.put("nickname",userInfoO.getNickname());
                    temp.put("email",userInfoO.getEmail());
                    return new GsonBuilder().create().toJson(temp);
                }
            }
        }
    }

    /**
     * 没有微信登录过医械查时，账号绑定微信，信息的整合
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("/method/wxBindUser")
    public int updateWXUser(HttpServletRequest request,HttpServletResponse response) throws Exception{
        response.setHeader("Access-Control-Allow-Origin", "*");
        String openid = request.getParameter("openid");
        String unionid = request.getParameter("unionid");
        String nickname = request.getParameter("nickname");
        String sex = request.getParameter("sex");
        String headimgurl = request.getParameter("headimgurl");
        String state = request.getParameter("state");
        BASE64Decoder decoder = new BASE64Decoder();
        Map<String,Object> dataMap = new HashMap<>();
        dataMap.put("unionid",unionid);
        dataMap.put("openid",openid);
        dataMap.put("nickname",nickname);
        dataMap.put("headimg",headimgurl);
        dataMap.put("usertype","ZH_WX");
        dataMap.put("sex",sex);

        dataMap.put("id",new String(decoder.decodeBuffer(state)));
        return userInfoService.updateUserInfo(dataMap);
    }

    /**
     * 账号绑定微信的信息整合
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("/method/wxBindUserInfo")
    public String updateWXUserInfo(HttpServletRequest request,HttpServletResponse response) throws Exception{
        response.setHeader("Access-Control-Allow-Origin", "*");
        String wxid = request.getParameter("wxid");//微信ID
        String userid = request.getParameter("state");//用户账号ID
        String openid = request.getParameter("openid");
        if(wxid == null || userid == null){return "failed";}
        BASE64Decoder decoder = new BASE64Decoder();
        Map<String, Object> map = new HashMap<>();
        map.put("id", Long.parseLong(wxid));
        UserInfo userInfo = userInfoService.selectUserInfoByCondition(map);
        Map<String,Object> dataMap = new HashMap<>();
        dataMap.put("unionid",userInfo.getUnionid());
        dataMap.put("openid",openid);
        dataMap.put("openidCard",userInfo.getOpenidCard());
        dataMap.put("nickname",userInfo.getNickname());
        dataMap.put("headimg",userInfo.getHeadimg());
        dataMap.put("sex",userInfo.getSex());
        dataMap.put("logincount",userInfo.getLogincount());
        dataMap.put("iscard",userInfo.getIscard());
        dataMap.put("job",userInfo.getJob());
        dataMap.put("usertype","ZH_WX");
        dataMap.put("id",Long.parseLong(new String(decoder.decodeBuffer(userid))));
        userInfoService.updateUserInfo(dataMap);
        userInfoService.deleteUserById(Long.parseLong(wxid));
        Map<String,Object> map1 = new HashMap<>();
        map1.put("val",new String(decoder.decodeBuffer(userid)));
        map1.put("userid",wxid);
        userBusinessService.updateidById(map1);
        updateCardRecord(Long.parseLong(wxid),Long.parseLong(new String(decoder.decodeBuffer(userid))));
        return "";
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
    private void updateUserInfo(String nickname,String sex,String headimgurl,String unionid,String openid,long userid,String usertype){
        Map<String,Object> dataMap = new HashMap<>();
        dataMap.put("unionid",unionid);
        dataMap.put("openid",openid);
        dataMap.put("nickname",nickname);
        dataMap.put("headimg",headimgurl);
        dataMap.put("sex",sex);
        dataMap.put("usertype",usertype);
        dataMap.put("id",userid);
        userInfoService.updateUserInfo(dataMap);
    }

    private void updateUserAllInfo(String nickname,String sex,String headimgurl,String unionid,String openid,long userid,UserInfo userInfo,String usertype){
        Map<String,Object> dataMap = new HashMap<>();
        dataMap.put("unionid",unionid);
        dataMap.put("openid",openid);
        dataMap.put("nickname",nickname);
        dataMap.put("headimg",headimgurl);
        dataMap.put("sex",sex);
        dataMap.put("openidCard",userInfo.getOpenidCard());
        dataMap.put("job",userInfo.getJob());
        dataMap.put("usertype",usertype);
        dataMap.put("id",userid);
        userInfoService.updateUserInfo(dataMap);
    }

    private void updateCardRecord(long oldid,long newid){
        Map<String ,Object> map = new HashMap<String,Object>();
        map.put("viewid",oldid);
        map.put("val",newid);

        Map<String ,Object> map1 = new HashMap<String,Object>();
        map1.put("viewedid",oldid);
        map1.put("val",newid);

        Map<String, Object> map2 = new HashMap<>();
        map2.put("userid", oldid);
        map2.put("val",newid);
        userCardService.updateidById(map2);

        wxOperCardService.updateAllID(map);
        wxOperCardService.updateAllID(map1);
    }

    /**
     * 根据用户id获得用户提交的审核信息
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("/method/selectAuditByCondition")
    public String selectAuditByUserId(HttpServletRequest request,HttpServletResponse response) throws Exception{
        response.setHeader("Access-Control-Allow-Origin", "*");

        Map<String,Object> temp = new HashMap<>();
        Map<String,Object> map = new HashMap<>();

        String userid = request.getParameter("userid");
        String num_string = request.getParameter("num");
        int num = Integer.valueOf(num_string);

        temp.put("userid",userid);
        temp.put("num",num*10);

        map.put("datas",userUploadPictureService.selectAuditByCondition(temp));
        if(num == 0) map.put("count",userUploadPictureService.selectAuditCountByCondition(temp));
        return new GsonBuilder().create().toJson(map);
    }

    /**
     * 根据id撤回审核信息
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("/method/recallAuditById")
    public String recallAuditById(HttpServletRequest request,HttpServletResponse response) throws Exception{
        response.setHeader("Access-Control-Allow-Origin", "*");

        String id = request.getParameter("id");
        userUploadPictureService.recallAuditById(Long.valueOf(id));

        return "";
    }

    /**
     * 判断相同产品id的有没有正在审核中的信息
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("/method/selectAuditCountByCondition")
    public int selectAuditCountByCondition(HttpServletRequest request,HttpServletResponse response) throws Exception{
        response.setHeader("Access-Control-Allow-Origin", "*");

        String objectid = request.getParameter("objectid");
        String userid = request.getParameter("userid");

        Map<String,Object> temp = new HashMap<>();
        temp.put("objectid",objectid);
        temp.put("userid",userid);
        temp.put("state","1");

        return userUploadPictureService.selectAuditCountByCondition(temp);
    }
}
