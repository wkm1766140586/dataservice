package com.abc.newsserversec.controller.wechat;

import com.abc.newsserversec.common.AesCbcUtil;
import com.abc.newsserversec.common.HttpHandler;
import com.abc.newsserversec.model.user.UserInfo;
import com.abc.newsserversec.model.wechat.WxaccessToken;
import com.abc.newsserversec.model.wechat.WxspUserInfo;
import com.abc.newsserversec.service.user.UserInfoService;
import com.abc.newsserversec.service.wechat.WxspInfoService;
import com.google.gson.GsonBuilder;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 微信小程序请求的方法
 */
@RestController
public class WxSmallprogramController {

    public static String APPID = "wxfa5b20c93499db27";
    public static String APPSECRET = "26bf61eee9181d83f03e4640de01c6bc";
    @Autowired
    private WxspInfoService wxspInfoService;

    @Autowired
    private UserInfoService userInfoService;

    /**
     * 获取unionid
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("/method/decodeUserInfo")
    public String decodeUserInfo(HttpServletRequest request, HttpServletResponse response)throws Exception{
        response.setHeader("Access-Control-Allow-Origin", "*");
        String code = request.getParameter("code");
        String encryptedData = request.getParameter("encryptedData");
        String iv = request.getParameter("iv");
        if(code.equals("") || code == null){return "failed";}
        if(encryptedData.equals("") || encryptedData == null){return "failed";}
        if(iv.equals("") || iv == null){return "failed";}

        //////////////// 1、向微信服务器 使用登录凭证 code 获取 session_key 和 openid ////////////////
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid="+APPID+"&secret="+APPSECRET+"&js_code="+code+"&grant_type=authorization_code";
        String result = HttpHandler.sendGet(url);
        //解析相应内容（转换成json对象）
        JSONObject json = JSONObject.fromObject(result);
        //获取会话密钥（session_key）
        String session_key = json.get("session_key").toString();
        //用户的唯一标识（openid）
        String openid = json.get("openid").toString();
        System.out.println("session_key："+session_key);
        System.out.println("openid："+openid);

        //////////////// 2、对encryptedData加密数据进行AES解密其中包含这openid和unionid ////////////////
        String data = AesCbcUtil.decrypt(encryptedData,session_key,iv,"UTF-8");
        System.out.println("data:"+data.toString());
        return data;
    }
    /*
      微信小程序登录
   */
    @RequestMapping("/method/wx_smallprogram_login")
    public String wxSmallprogramLogin(HttpServletRequest request, HttpServletResponse response){
        response.setHeader("Access-Control-Allow-Origin", "*");
        String result="";//访问返回结果
        BufferedReader read=null;//读取访问结果
        WxaccessToken wxaccessToken = null;

        String code = request.getParameter("code");
        System.out.println(code);
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid="+APPID+"&secret="+APPSECRET+"&js_code="+code+"&grant_type=authorization_code";

        try {
            //创建url
            URL realurl=new URL(url);
            //打开连接
            URLConnection connection=realurl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            //建立连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段，获取到cookies等
            for (String key : map.keySet()) {
                //System.out.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            read = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(),"UTF-8"));
            String line;//循环读取
            while ((line = read.readLine()) != null) {
                result += line;
            }
            System.out.println(result);
            wxaccessToken = new GsonBuilder().create().fromJson(result, WxaccessToken.class);
            if(wxaccessToken != null) {
                if (wxaccessToken.getAccess_token() != null && wxaccessToken.getOpenid() != null) {
                    System.out.println(wxaccessToken.getAccess_token() + "," + wxaccessToken.getOpenid());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if(read!=null){//关闭流
                try {
                    read.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return new GsonBuilder().create().toJson(wxaccessToken);
    }

    /*
     微信小程序--新增用户信息
  */
    @RequestMapping("/method/wxsp_insert_user")
    public void wxspInsertUser(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        String openid = request.getParameter("openid");
        String unionid = request.getParameter("unionid");
        String headimg = request.getParameter("headimg");
        String nickname = request.getParameter("nickname");
        String sex = request.getParameter("sex");
        if(sex.equals("0")) sex = "未知";
        else if(sex.equals("1")) sex = "男";
        else sex = "女";

        Map<String,Object> dataMap = new HashMap<>();
        dataMap.put("unionid",unionid);
        UserInfo userInfo = userInfoService.selectUserInfoByCondition(dataMap);
        if(userInfo == null){
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = df.format(new Date());
            dataMap.put("headimg",headimg);
            dataMap.put("nickname",nickname);
            dataMap.put("sex",sex);
            dataMap.put("createdate",date);
            dataMap.put("openidCard",openid);
            dataMap.put("usertype","WXSP");
            dataMap.put("integral",10);
            userInfoService.insertUserInfo(dataMap);
        }else{//已微信登录的用户
            Map<String,Object> map = new HashMap<>();
            map.put("nickname",nickname);
            map.put("sex",sex);
            map.put("headimg",headimg);
            map.put("openidCard",openid);
            map.put("id",userInfo.getId());
            if(null == userInfo.getUsertype() || userInfo.getUsertype().equals("")){
                map.put("usertype","WXSP");
            }
            userInfoService.updateUserInfo(map);
        }
    }
    @RequestMapping("/method/wxsp/wxsp_update_user")
    public void wxspUpdateuser(HttpServletRequest request, HttpServletResponse response){
        response.setHeader("Access-Control-Allow-Origin", "*");
        String openid = request.getParameter("openid");
        String unionid = request.getParameter("unionid");

        ArrayList<WxspUserInfo> users = wxspInfoService.selectWxspUserByOpenid(openid);
        if(users.size() > 0){
            if(users.get(0).getUnionid() == null || "".equals(users.get(0).getUnionid())){
                Map<String,String> dataMap = new HashMap<>();
                dataMap.put("openid",openid);
                dataMap.put("unionid",unionid);
                wxspInfoService.updateWxspUserByMap(dataMap);
            }
        }
    }

    @RequestMapping("/method/insertwxUserInfo")
    public String queryUserInfo(HttpServletRequest request, HttpServletResponse response){
        response.setHeader("Access-Control-Allow-Origin", "*");
        String unionid = request.getParameter("unionid");
        String openid = request.getParameter("openid");
        String headimg = request.getParameter("headimg");
        String nickname = request.getParameter("nickname");
        String sex = request.getParameter("sex");
        if(sex.equals("0")) sex = "未知";
        else if(sex.equals("1")) sex = "男";
        else sex = "女";
        if(unionid == null){return "fail";}
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("unionid", unionid);
        UserInfo userInfo = userInfoService.selectUserInfoByCondition(dataMap);
        if (userInfo != null){
            Map<String,Object> map = new HashMap<>();
            map.put("nickname",nickname);
            map.put("sex",sex);
            map.put("headimg",headimg);
            map.put("openidCard",openid);
            if(null == userInfo.getUsertype() || userInfo.getUsertype().equals("")){
                map.put("usertype","WXSP");
            }
            map.put("id",userInfo.getId());
            userInfoService.updateUserInfo(map);
            return new GsonBuilder().create().toJson(userInfoService.selectUserInfoByCondition(dataMap));
        }else{
            Map<String, Object> map = new HashMap<>();
            map.put("openidCard", openid);
            UserInfo user = userInfoService.selectUserInfoByCondition(map);
            if(user != null){
                map.put("nickname",nickname);
                map.put("sex",sex);
                map.put("headimg",headimg);
                map.put("unionid",unionid);
                if(null == user.getUsertype() || user.getUsertype().equals("")){
                    map.put("usertype","WXSP");
                }
                map.put("id",user.getId());
                userInfoService.updateUserInfo(map);
                Map<String, Object> map1 = new HashMap<>();
                map1.put("openidCard", openid);
                UserInfo user1 = userInfoService.selectUserInfoByCondition(map1);
                return new GsonBuilder().create().toJson(user1);

            }else{
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String date = df.format(new Date());
                dataMap.put("headimg",headimg);
                dataMap.put("nickname",nickname);
                dataMap.put("sex",sex);
                dataMap.put("openidCard",openid);
                dataMap.put("createdate",date);
                dataMap.put("usertype","WXSP");
                dataMap.put("integral",10);
                userInfoService.insertUserInfo(dataMap);
                Map<String, Object> map1 = new HashMap<>();
                map1.put("unionid", unionid);
                UserInfo user1 = userInfoService.selectUserInfoByCondition(map1);
                return new GsonBuilder().create().toJson(user1);
            }

        }
    }



    /*
     微信小程序--新增查询信息
  */
    @RequestMapping("/method/wxsp_insert_usersearch")
    public void wxspInsertUserSearch(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        String openid = request.getParameter("openid");
        String classtype = request.getParameter("classtype");
        String keyword = request.getParameter("keyword");
        String resultcount = request.getParameter("resultcount");

        ArrayList<WxspUserInfo> users = wxspInfoService.selectWxspUserByOpenid(openid);
        if(users.size() > 0){
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = df.format(new Date());
            Map<String,Object> map = new HashMap<>();
            map.put("userid", users.get(0).getId());
            map.put("classtype",classtype);
            map.put("keyword",keyword);
            map.put("resultcount",resultcount);
            map.put("createdate",date);
            wxspInfoService.insertWxspUserSearchByMap(map);
        }

    }

}
