package com.abc.newsserversec;

import com.abc.newsserversec.model.user.UserInfo;
import com.abc.newsserversec.model.wechat.WxspUserInfo;
import com.abc.newsserversec.service.user.UserCardService;
import com.abc.newsserversec.service.user.UserInfoService;
import com.abc.newsserversec.service.user.UsersearchInfoService;
import com.abc.newsserversec.service.wechat.WxcardInfoService;
import com.abc.newsserversec.service.wechat.WxspInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@RestController
public class InfoJoinController {
    @Autowired
    private WxspInfoService wxspInfoService;
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private UsersearchInfoService usersearchInfoService;
    @Autowired
    private UserCardService userCardService;

    @RequestMapping("/method/joinInfo")
    public void UserInbfoJoin(HttpServletRequest request, HttpServletResponse response){
        response.setHeader("Access-Control-Allow-Origin", "*");
        try{
            ArrayList<WxspUserInfo> wxspUserInfos = wxspInfoService.selectAllUser();
            for(int i = 0;i<wxspUserInfos.size();i++){
                WxspUserInfo wxspUserInfo = wxspUserInfos.get(i);
                System.out.println("当前用户---"+wxspUserInfo.getId());
                if(wxspUserInfo.getUnionid() == null || wxspUserInfo.getUnionid().equals("")){//直接插入数据
                    Map<String,Object> map = new HashMap<>();
                    map.put("nickname",wxspUserInfo.getNickname());
                    map.put("sex",wxspUserInfo.getSex());
                    map.put("openidCard",wxspUserInfo.getOpenid());
                    userInfoService.insertUserInfo(map);
                    Map<String, Object> dataMap = new HashMap<>();
                    dataMap.put("openidCard",wxspUserInfo.getOpenid());
                    UserInfo userInfo = userInfoService.selectUserInfoByCondition(dataMap);
                    updatesearchid(wxspUserInfo.getId(),userInfo.getId());//修改用户ID
                    ArrayList<Map<String,Object>> infos = wxspInfoService.selectSearchInfoByUserid(userInfo.getId());
                    for(int j = 0;j < infos.size();j++){
                        Map<String, Object> dataMap1 = new HashMap<>();
                        dataMap1.put("userid",userInfo.getId());
                        dataMap1.put("classtype",infos.get(j).get("classtype"));
                        dataMap1.put("keyword",infos.get(j).get("keyword"));
                        dataMap1.put("resultcount",infos.get(j).get("resultcount"));
                        dataMap1.put("searchtype","YXC_WX");
                        dataMap1.put("createdate",infos.get(j).get("createdate"));
                        usersearchInfoService.insertUsersearchInfo(dataMap1);
                    }
                }else{//不插入，代表

                    Map<String,Object> map = new HashMap<>();
                    map.put("unionid",wxspUserInfo.getUnionid());
                    UserInfo userInfo = userInfoService.selectUserInfoByCondition(map);
                    Map<String,Object> dataMap = new HashMap<>();
                    dataMap.put("openidCard",wxspUserInfo.getOpenid());
                    dataMap.put("id",wxspUserInfo.getId());
                    userInfoService.updateUserInfo(dataMap);

                    Map<String, Object> dataMap2 = new HashMap<>();
                    dataMap2.put("unionid",wxspUserInfo.getUnionid());
                    UserInfo userInfo1 = userInfoService.selectUserInfoByCondition(dataMap2);
                    updatesearchid(wxspUserInfo.getId(),userInfo1.getId());//修改用户ID
                    ArrayList<Map<String,Object>> infos = wxspInfoService.selectSearchInfoByUserid(userInfo1.getId());
                    for(int j = 0;j < infos.size();j++){
                        Map<String, Object> dataMap1 = new HashMap<>();
                        dataMap1.put("userid",userInfo.getId());
                        dataMap1.put("classtype",infos.get(j).get("classtype"));
                        dataMap1.put("keyword",infos.get(j).get("keyword"));
                        dataMap1.put("resultcount",infos.get(j).get("resultcount"));
                        dataMap1.put("searchtype","YXC_WX");
                        dataMap1.put("createdate",infos.get(j).get("createdate"));
                        usersearchInfoService.insertUsersearchInfo(dataMap1);
                    }
                }
                //wxspInfoService.selectSearchInfoByUserid(wxspUserInfos.get(i).getId());
            }
        }catch(Exception e){

        }


    }
    public void updatesearchid(long oldid,long newid){
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("val",newid);
        dataMap.put("userid",oldid);
        wxspInfoService.updateUserIds(dataMap);
    }
    @RequestMapping("/method/eeee")
    public void updatesearchid1(HttpServletRequest request, HttpServletResponse response){
        String newid = request.getParameter("newid");
        String oldid = request.getParameter("oldid");
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("val",Long.parseLong(newid));
        dataMap.put("userid",Long.parseLong(oldid));
        wxspInfoService.updateUserIds(dataMap);
    }
    @RequestMapping("/method/zzz")
    public void aa(HttpServletRequest request, HttpServletResponse response){
        for(int i = 0;i <962;i++){
            /*Map<String, Object> dataMap = new HashMap<>();
            userInfoService.selectUserInfoByCondition(dataMap);*/
            //ArrayList<Map<String,Object>> arrayList = wxspInfoService.selectSearchInfoByUserid(i);//小程序用户
            ArrayList<Map<String,Object>> arrayList = usersearchInfoService.selectUserSearch(i);
            if(arrayList.size() > 0){
                System.out.println("当前ID为："+i);
                Set<String> set = new HashSet();
                for(int j = 0;j < arrayList.size();j++){
                /*String type = arrayList.get(j).get("searchtype").toString();
                System.out.print(type+"||");*/
                    set.add(arrayList.get(j).get("searchtype").toString());
                }
                for (String str : set) {
                    System.out.print(str+"||");
                }
                if((set.contains("YXC_PC") && set.contains("YXC_WXSP")) || (set.contains("YXC_PC") && set.contains("FLML_WXSP"))){
                    System.out.println("ZH_WX");
                    update(i,"ZH_WX");
                }else if(set.contains("YXC_PC")){
                    System.out.println("ZH");
                    update(i,"ZH");
                }else if(set.contains("YXC_WXSP") || set.contains("FLML_WXSP")){
                    System.out.println("WXSP");
                    update(i,"WXSP");
                }else{
                    Map<String, Object> dataMap = new HashMap<>();
                    dataMap.put("id",i);
                    UserInfo userInfo = userInfoService.selectUserInfoByCondition(dataMap);
                    if(userInfo.getOpenid().equals("") || userInfo.getOpenid()==null){
                        System.out.println("WXSP");
                        update(i,"WXSP");
                    }else{
                        System.out.println("ZH");
                        update(i,"ZH");
                    }

                }
                System.out.println();
            }else{
                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("id",i);
                UserInfo userInfo = userInfoService.selectUserInfoByCondition(dataMap);
                System.out.println(userInfo);
                if(userInfo != null){
                    if(  null == userInfo.getOpenid() || userInfo.getOpenid().equals("")){
                        System.out.println("WXSP");
                        update(i,"WXSP");
                    }else{
                        System.out.println("ZH");
                        update(i,"ZH");
                    }
                }

            }
            System.out.println();
        }
    }
    @RequestMapping("/method/123")
    public void bb(HttpServletRequest request, HttpServletResponse response){
        for(int i =0;i <4617;i++){
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("id",i);
            UserInfo userInfo = userInfoService.selectUserInfoByCondition(dataMap);
            if(userInfo != null){
                /*if(((null != userInfo.getUsername() && userInfo.getUsername() != "") || (null != userInfo.getEmail() && userInfo.getEmail() != "")) && (null != userInfo.getOpenid() || userInfo.getOpenid() != "")){//绑定用户
                    System.out.println("用户ID=="+i+"--ZH_WX");
                    update(i,"ZH_WX");
                }else if((null != userInfo.getUsername() || null != userInfo.getEmail() || null != userInfo.getOpenid()) && null == userInfo.getUnionid()){//账号
                    System.out.println("用户ID=="+i+"--ZH");
                    update(i,"ZH");
                }else if((null == userInfo.getUsername() && null == userInfo.getEmail() && null == userInfo.getOpenid()) && (null != userInfo.getUnionid() || null != userInfo.getOpenidCard())){//小程序
                    System.out.println("用户ID=="+i+"--WXSP");
                    update(i,"WXSP");
                }*/
                if((null != userInfo.getUsername() && !userInfo.getUsername().equals("")) || (null != userInfo.getEmail() && !userInfo.getEmail().equals("")) || (null != userInfo.getOpenid() && !userInfo.getOpenid().equals(""))){
                    /*System.out.println("用户ID=="+i+"--ZH");
                    update(i,"ZH");*/
                    //if(((null != userInfo.getUsername() && !userInfo.getUsername().equals("")) && (null != userInfo.getOpenid() && !userInfo.getOpenid().equals(""))) || ){}
                    int count = 0;
                    if(null != userInfo.getUsername() && !userInfo.getUsername().equals("")){count++;}
                    if(null != userInfo.getEmail() && !userInfo.getEmail().equals("")){count++;}
                    if(null != userInfo.getOpenid() && !userInfo.getOpenid().equals("")){count++;}
                    if(count>1){//
                        System.out.println("用户ID=="+i+"--ZH_WX");
                        update(i,"ZH_WX");
                    }else{
                        System.out.println("用户ID=="+i+"--ZH");
                        update(i,"ZH");
                    }
                }else if((null == userInfo.getUsername() || userInfo.getUsername().equals("")) && (null == userInfo.getEmail() || userInfo.getEmail().equals("")) && (null == userInfo.getOpenid() || userInfo.getOpenid().equals(""))){
                    System.out.println("用户ID=="+i+"--WXSP");
                    update(i,"WXSP");
                }else{
                    System.out.println("用户ID=="+i+"--未知");
                    update(i,"未知");
                }
            }
        }
        System.out.println();
    }

    public void update(long userid,String state) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("id",userid);
        dataMap.put("usertype",state);
        userInfoService.updateUserInfo(dataMap);
    }

    @RequestMapping("/method/abcba")
    public String updateUserCard(HttpServletRequest request, HttpServletResponse response){
        String userid = request.getParameter("id");
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("id",Integer.parseInt(userid));
        UserInfo userInfo = userInfoService.selectUserInfoByCondition(dataMap);
        Map<String, Object> map = new HashMap<>();
        map.put("realname", userInfo.getRealname());
        map.put("mobilephone", userInfo.getMobilephone());
        map.put("wechatnum",userInfo.getWechatnum());
        map.put("companyname", userInfo.getCompanyname());
        map.put("companyaddress", userInfo.getCompanyaddress());
        map.put("department", userInfo.getDepartment());
        map.put("job",userInfo.getJob());
        map.put("email",userInfo.getEmail());
        map.put("userid",userid);
        map.put("createtime",userInfo.getCreatedate());
        userCardService.insertUserCard(map);
        return "success";
    }
}
