package com.abc.newsserversec.controller.user;

import com.abc.newsserversec.model.user.UserInfo;
import com.abc.newsserversec.service.user.UserInfoService;
import com.abc.newsserversec.service.user.UsersearchInfoService;
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
public class UsersearchInfoController {

    @Autowired
    private UsersearchInfoService usersearchInfoService;

    @Autowired
    private UserInfoService userInfoService;

    /**
     * 新增用户查询信息数据
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/usersearchInfo/insertUsersearchInfo")
    public String insertUsersearchInfo(HttpServletRequest request, HttpServletResponse response){
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
        String classtype = request.getParameter("classtype");
        String keyword = request.getParameter("keyword");
        String resultcount = request.getParameter("resultcount");

        Map<String, Object> map = new HashMap<>();
        map.put("username",username);
        map.put("openid",openid);
        UserInfo userInfo = userInfoService.selectUserInfoByCondition(map);
        if(userInfo != null) {
            long userid = userInfo.getId();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = df.format(new Date());
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("userid",userid);
            dataMap.put("classtype",classtype);
            dataMap.put("keyword",keyword);
            dataMap.put("resultcount",resultcount);
            dataMap.put("createdate",date);

            int count = usersearchInfoService.insertUsersearchInfo(dataMap);
            if(count == 1){
                return "success";
            }else{
                return "fail";
            }
        }else{
            return "fail";
        }

    }

}
