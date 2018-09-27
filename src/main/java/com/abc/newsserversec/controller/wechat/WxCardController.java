package com.abc.newsserversec.controller.wechat;

import com.abc.newsserversec.model.user.UserBusiness;
import com.abc.newsserversec.model.user.UserCard;
import com.abc.newsserversec.model.user.UserInfo;
import com.abc.newsserversec.model.wechat.WxOperCard;
import com.abc.newsserversec.model.wechat.WxaccessToken;
import com.abc.newsserversec.model.wechat.WxspUserInfo;
import com.abc.newsserversec.service.user.UserBusinessService;
import com.abc.newsserversec.service.user.UserCardService;
import com.abc.newsserversec.service.user.UserInfoService;
import com.abc.newsserversec.service.wechat.WxOperCardService;
import com.abc.newsserversec.service.wechat.WxcardInfoService;
import com.google.gson.GsonBuilder;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 名片小程序的控制器
 */
@RestController
public class WxCardController {

    @Autowired
    private WxcardInfoService wxcardInfoService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private WxOperCardService wxOperCardService;

    @Autowired
    private UserBusinessService userBusinessService;

    @Autowired
    private UserCardService userCardService;

    /**
     * 登录
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/method/card_login")
    public static String wxSmallprogramLogin(HttpServletRequest request, HttpServletResponse response){
        response.setHeader("Access-Control-Allow-Origin", "*");
        String result="";//访问返回结果
        BufferedReader read=null;//读取访问结果
        WxaccessToken wxaccessToken = null;

        String appid = "wxfde0b3f48deb648f";
        String secret = "69a74760636be003853e0261e6afa328";
        String code = request.getParameter("code");
        System.out.println(code);
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid="+appid+"&secret="+secret+"&js_code="+code+"&grant_type=authorization_code";

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

    /**
     * 增加信息
     * @param request
     * @param response
     */
    @RequestMapping("/method/card_insert_user")
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

        WxspUserInfo user = wxcardInfoService.selectUserByOpenid(openid);
        if(user == null){
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = df.format(new Date());
            Map<String,String> map = new HashMap<>();
            map.put("openid",openid);
            map.put("unionid",unionid);
            map.put("nickname",nickname);
            map.put("sex",sex);
            map.put("createdate",date);
            wxcardInfoService.insertUserByMap(map);
        }
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
           userInfoService.insertUserInfo(dataMap);
       }else{//已微信登录的用户
           Map<String,Object> map = new HashMap<>();
           map.put("nickname",nickname);
           map.put("sex",sex);
           map.put("headimg",headimg);
           map.put("id",userInfo.getId());
           userInfoService.updateUserInfo(map);
       }
    }

    /**
     * 小程序中“我查看的人”
     * @param request
     * @param response
     */
    @RequestMapping("/method/queryByViewId")
    public String viewCardUser(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        String userid = request.getParameter("userid");
        String opertype = request.getParameter("opertype");//操作类型：点赞、查看、转发
        String num = request.getParameter("num");
        String size = request.getParameter("size");
        if(userid == null || userid == ""){return "fail";}
        if(opertype == null || opertype == ""){return "fail";}
        return queryUserByviewId(Long.parseLong(userid),"viewid",Integer.parseInt(opertype),num,size);
    }

    /**
     * 小程序中“谁看了我”
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/method/queryByViewedId")
    public String viewedCardUser(HttpServletRequest request, HttpServletResponse response){
        response.setHeader("Access-Control-Allow-Origin", "*");
        String userid = request.getParameter("userid");
        String opertype = request.getParameter("opertype");//操作类型：点赞、查看、转发
        String num = request.getParameter("num");
        String size = request.getParameter("size");
        if(userid == null || userid == ""){return "fail";}
        if(opertype == null || opertype == ""){return "fail";}
        return queryUserByviewId(Long.parseLong(userid),"viewedid",Integer.parseInt(opertype),num,size);
    }

    /**
     * 小程序中“谁看了我”、“我查看名片”
     * @param userid  用户的ID
     * @param viewType  viewedid：“操作者”  viewid：“被操作者”
     * @param opertype  0.代表三者 1.点赞 2.查看 3.转发
     * @param num 第几页
     * @param size 每页几条
     * @return
     */
    private String queryUserByviewId(long userid,String viewType,int opertype,String num,String size){
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put(viewType,userid);
        if(opertype == 0){//返回三个的数量
            Map<String, Object> map = new HashMap<>();
            String[] tips = {"dz","ck","zf"};
            for(int i = 1;i < 4;i++){
                dataMap.put("opertype",i);
                ArrayList<WxOperCard> cards = wxOperCardService.selectOperCardsById(dataMap);
                int count = 0;
                for(int j = 0;j < cards.size();j++){
                    count += cards.get(j).getOpercount();
                }
                map.put(tips[i-1],count);
            }
            return new GsonBuilder().create().toJson(map);
        }
        dataMap.put("opertype",opertype);
        dataMap.put("num",Integer.parseInt(num)*Integer.parseInt(size));
        dataMap.put("size",Integer.parseInt(size));
        ArrayList<WxOperCard> viewCardArray = wxOperCardService.selectOperCardsById(dataMap);
        for(int i = 0;i < viewCardArray.size();i++){
            Map<String, Object> map = new HashMap<>();
            if(viewType.equals("viewid")){
                map.put("id",viewCardArray.get(i).getViewedid());
            }else if(viewType.equals("viewedid")){
                map.put("id",viewCardArray.get(i).getViewid());
            }

            UserInfo userInfo = userInfoService.selectUserInfoByCondition(map);
            viewCardArray.get(i).setUserInfo(userInfo);
        }
        Map<String,Object> map1 = new HashMap<>();
        map1.put("data",viewCardArray);
        System.out.println("数量num："+num);
        if(Integer.parseInt(num) == 0){
            map1.put("matchCount",wxOperCardService.selectCountById(dataMap));
            System.out.println("数量："+wxOperCardService.selectCountById(dataMap));
        }

        return new GsonBuilder().create().toJson(map1);
    }

    /*判断是否点赞*/
    @RequestMapping("/method/existRecord")
    public int existRecord(HttpServletRequest request, HttpServletResponse response){
        response.setHeader("Access-Control-Allow-Origin", "*");
        String viewid = request.getParameter("viewid");
        String viewedid = request.getParameter("viewedid");
        String opertype = request.getParameter("opertype");
        if(viewid == null || viewid == ""){return -1;}
        if(viewedid == null || viewedid == ""){return -1;}
        if(opertype == null || opertype == ""){return -1;}
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("viewid",viewid);
        dataMap.put("viewedid",viewedid);
        dataMap.put("opertype",opertype);
        return wxOperCardService.selectCountById(dataMap);
    }

    /**
     * 添加点赞、转发、查看的信息
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/method/insertViewCard")
    public int insertViewedCard(HttpServletRequest request, HttpServletResponse response){
        response.setHeader("Access-Control-Allow-Origin", "*");
        String viewid = request.getParameter("viewid");
        String viewedid = request.getParameter("viewedid");
        String opertype = request.getParameter("opertype");
        if(viewid == null || viewid == ""){return -1;}
        if(viewedid == null || viewedid == ""){return -1;}
        if(opertype == null || opertype == ""){return -1;}

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("viewid",viewid);
        dataMap.put("viewedid",viewedid);
        dataMap.put("opertype",opertype);
        dataMap.put("createtime",queryCurrentTime());
        int count = wxOperCardService.selectCountById(dataMap);
        if(count == 0){
            dataMap.put("opercount",1);
            return  wxOperCardService.insertWxOperCardByMap(dataMap);
        }else{
            return  wxOperCardService.updateWxOperCardByMap(dataMap);
        }
    }

    /**
     * 删除信息：取消点赞
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/method/deleteViewCard")
    public int deleteViewedCard(HttpServletRequest request, HttpServletResponse response){
        response.setHeader("Access-Control-Allow-Origin", "*");
        String viewid = request.getParameter("viewid");
        String viewedid = request.getParameter("viewedid");
        String opertype = request.getParameter("opertype");
        if(viewid == null || viewid == ""){return -1;}
        if(viewedid == null || viewedid == ""){return -1;}
        if(opertype == null || opertype == ""){return -1;}

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("viewid",viewid);
        dataMap.put("viewedid",viewedid);
        dataMap.put("opertype",opertype);
        return wxOperCardService.deleteWxOperCardByMap(dataMap);
    }

    /**
     * 根据产品ID查询出产品的负责人
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/method/selectUsersByProductId")
    public String getUseByProductId(HttpServletRequest request, HttpServletResponse response){
        response.setHeader("Access-Control-Allow-Origin", "*");
        String productid = request.getParameter("productid");
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("productids",productid);
        ArrayList<UserBusiness> userBusinesses = userBusinessService.selectUserBusinessByCondition(dataMap);
        ArrayList<Object> users = new ArrayList<>();
        if(userBusinesses.size() > 0){
            for (UserBusiness userBusiness : userBusinesses){
                Map<String, Object> map = new HashMap<>();
                map.put("id",userBusiness.getUserid());
                map.put("userid",userBusiness.getUserid());
                UserInfo userInfo = userInfoService.selectUserInfoByCondition(map);
                UserCard userCard = userCardService.selectUserCardByCondition(map);
                Map<String, Object> map1 = new HashMap<>();
                if(userInfo != null){
                    map1.put("userinfo",userInfo);
                    map1.put("userCard",userCard);
                    users.add(map1);
                }
            }
        }
        return new GsonBuilder().create().toJson(users);
    }

    @RequestMapping("/method/selectAllRegions")
    public String selectAllRegions(HttpServletRequest request, HttpServletResponse response){
        response.setHeader("Access-Control-Allow-Origin", "*");
        ArrayList<Map<String,Object>> datas = wxcardInfoService.selectAllRegion(0);
        JSONArray arr = new JSONArray();
        for(int i = 0;i<datas.size();i++){
            JSONObject json = new JSONObject();
            json.put("title",datas.get(i));
            json.put("content",wxcardInfoService.selectAllRegion((int)(datas.get(i).get("id"))));
            arr.add(json);
        }
        return JSONArray.fromObject(arr).toString();
    }

    public String queryCurrentTime(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(new Date());
    }
}
