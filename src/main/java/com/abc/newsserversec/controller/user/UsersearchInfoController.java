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

    /**
     * 新增用户查询信息
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/method/insertUsersearchInfo")
    public String insertUsersearchInfo(HttpServletRequest request, HttpServletResponse response){
        response.setHeader("Access-Control-Allow-Origin", "*");

        String userid_string = request.getParameter("userid");
        if(userid_string == null) return "fail";
        if(userid_string.equals("")) return "fail";

        String classtype = request.getParameter("classtype");
        String keyword = request.getParameter("keyword");
        String resultcount = request.getParameter("resultcount");

        long userid = Long.valueOf(userid_string);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = df.format(new Date());
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("userid",userid);
        dataMap.put("classtype",classtype);
        dataMap.put("keyword",keyword);
        dataMap.put("resultcount",resultcount);
        dataMap.put("createdate",date);

        int count = usersearchInfoService.insertUsersearchInfo(dataMap);
        if(count == 1){ return "success"; }
        else{ return "fail"; }

    }

}
