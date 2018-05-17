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

    /**
     * 新增用户跳转页面信息
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/method/insertUserurljumpInfo")
    public String insertUserurljumpInfo(HttpServletRequest request, HttpServletResponse response){
        response.setHeader("Access-Control-Allow-Origin", "*");

        String userid_string = request.getParameter("userid");
        if(userid_string == null) return "fail";
        if(userid_string.equals("")) return "fail";

        String beforeurl = request.getParameter("beforeurl");
        String afterurl = request.getParameter("afterurl");
        String clickname = request.getParameter("clickname");

        long userid = Long.valueOf(userid_string);
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

    }

    /**
     * 根据用户id查询数据
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/method/selectUserurljumpInfoById")
    public String selectUserurljumpInfoById(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");

        String userid_string = request.getParameter("userid");
        if(userid_string == null) return "fail";
        if(userid_string.equals("")) return "fail";

        long userid = Long.valueOf(userid_string);
        ArrayList<UserurljumpInfo> userurljumpInfos = userurljumpInfoService.selectUserurljumpInfoById(userid);
        return new GsonBuilder().create().toJson(userurljumpInfos);

    }

}
