package com.abc.newsserversec.controller.user;

import com.abc.newsserversec.service.user.UserUploadPictureService;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
public class UploadInfoController {

    @Autowired
    private UserUploadPictureService userUploadPictureService;

    /**
     * 根据条件获得用户提交的审核信息
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("/method/selectAuditByCondition")
    public String selectAuditByCondition(HttpServletRequest request, HttpServletResponse response) throws Exception{
        response.setHeader("Access-Control-Allow-Origin", "*");

        Map<String,Object> temp = new HashMap<>();
        Map<String,Object> map = new HashMap<>();

        String userid = request.getParameter("userid");
        String num_string = request.getParameter("num");
        String classtype = request.getParameter("classtype");
        String keyword = request.getParameter("keyword");
        String size_string = request.getParameter("size");
        int num = Integer.valueOf(num_string);
        int size = 5;
        if(size_string != null && !size_string.equals("")) size = Integer.parseInt(size_string);

        temp.put("userid",userid);
        temp.put("num",num*size);
        temp.put("size",size);
        if(keyword != null && !keyword.equals("")){
            String regex = "(.{1})";
            keyword = keyword.replaceAll (regex, "$1%");
            temp.put("keyword","%"+keyword);
        }
        if(classtype.equals("pro")){
            map.put("datas",userUploadPictureService.selectProductAuditByCondition(temp));
            if(num == 0) map.put("count",userUploadPictureService.selectProductAuditCountByCondition(temp));
        }else if(classtype.equals("com")){
            map.put("datas",userUploadPictureService.selectCompanyAuditByCondition(temp));
            if(num == 0) map.put("count",userUploadPictureService.selectCompanyAuditCountByCondition(temp));
        }else if(classtype.equals("all")){
            map.put("datas",userUploadPictureService.selectAuditByCondition(temp));
            if(num == 0) map.put("count",userUploadPictureService.selectAuditCountByCondition(temp));
        }

        return new GsonBuilder().create().toJson(map);
    }
}
