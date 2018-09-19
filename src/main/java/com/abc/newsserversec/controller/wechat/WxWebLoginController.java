package com.abc.newsserversec.controller.wechat;

import com.abc.newsserversec.model.wechat.WxUserInfo;
import com.abc.newsserversec.model.wechat.WxaccessToken;
import com.google.gson.GsonBuilder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * 网页微信登录接口
 */
@RestController
public class WxWebLoginController {

    public static String APPID = "wx188b08f403793197";
    public static String APPSECRET = "81ae587e89025ec5c7d5f89a1d6deb96";
    public static String LOGIN_TO_URL = "http://www.yixiecha.cn";
    public static String BIND_USER_URL = "http://www.yixiecha.cn/usercenter.html";
    @RequestMapping("/method/wxopenCode")
    public static String wxopenCode(HttpServletRequest request, HttpServletResponse response){
        response.setHeader("Access-Control-Allow-Origin", "*");
        String url = "https://open.weixin.qq.com/connect/qrconnect?appid="+APPID+"&redirect_uri="+ URLEncoder.encode(LOGIN_TO_URL)+"&response_type=code&scope=snsapi_login&state=STATE#wechat_redirect";
        return url;
    }

    @RequestMapping("/method/wxbindopenCode")
    public static String wxbindopenCode(HttpServletRequest request, HttpServletResponse response){
        response.setHeader("Access-Control-Allow-Origin", "*");
        String userid = request.getParameter("userid");
        BASE64Encoder base64Encoder = new BASE64Encoder();
        String url = "https://open.weixin.qq.com/connect/qrconnect?appid="+APPID+"&redirect_uri="+ URLEncoder.encode(BIND_USER_URL)+"&response_type=code&scope=snsapi_login&state="+base64Encoder.encode(userid.getBytes())+"#wechat_redirect";
        return url;
    }

    @RequestMapping("/method/wxtoken")
    public static String wxtoken(HttpServletRequest request, HttpServletResponse response){
        response.setHeader("Access-Control-Allow-Origin", "*");
        String result="";//访问返回结果
        BufferedReader read=null;//读取访问结果
        WxUserInfo wxUserInfo = null;

        String code = request.getParameter("code");
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="+APPID+"&secret="+APPSECRET+"&code="+code+"&grant_type=authorization_code";

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
            WxaccessToken wxaccessToken = new GsonBuilder().create().fromJson(result, WxaccessToken.class);
            if(wxaccessToken != null) {
                if (wxaccessToken.getAccess_token() != null && wxaccessToken.getOpenid() != null) {
                    wxUserInfo = searchWxUserInfo(wxaccessToken.getAccess_token(), wxaccessToken.getOpenid());
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

        return new GsonBuilder().create().toJson(wxUserInfo);
    }

    private static WxUserInfo searchWxUserInfo(String access_token, String openid){
        String result="";//访问返回结果
        BufferedReader read=null;//读取访问结果
        WxUserInfo wxUserInfo = null;

        String url = "https://api.weixin.qq.com/sns/userinfo?access_token="+access_token+"&openid="+openid;

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
            wxUserInfo = new GsonBuilder().create().fromJson(result, WxUserInfo.class);

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

        return wxUserInfo;
    }
}
