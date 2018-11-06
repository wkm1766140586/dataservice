package com.abc.newsserversec.controller.user;

import com.abc.newsserversec.model.user.UploadCompanyPicture;
import com.abc.newsserversec.model.user.UserUploadPicture;
import com.abc.newsserversec.service.user.UserUploadPictureService;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
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

    /**
     * 根据条件撤回审核信息
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("/method/recallAuditById")
    public String recallAuditById(HttpServletRequest request,HttpServletResponse response) throws Exception{
        response.setHeader("Access-Control-Allow-Origin", "*");

        String id = request.getParameter("id");
        String classtype = request.getParameter("classtype");
        Map<String,Object> temp = new HashMap<>();
        temp.put("id",id);
        temp.put("state","2");
        if(classtype.equals("pro")) userUploadPictureService.updateProductAuditByCondition(temp);
        else if(classtype.equals("com")) userUploadPictureService.updateCompanyAuditByCondition(temp);

        return "";
    }

    /**
     * 根据条件删除审核信息
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("/method/deleteAuditById")
    public String deleteAuditById(HttpServletRequest request,HttpServletResponse response) throws Exception{
        response.setHeader("Access-Control-Allow-Origin", "*");

        String ids = request.getParameter("ids");
        String classtype = request.getParameter("classtype");
        String[] idSize = ids.split(",");
        Map<String,Object> temp = new HashMap<>();
        temp.put("delflag","1");
        if(classtype.equals("pro")){
            for(int i = 0; i < idSize.length; i++){
                String id = idSize[i];
                temp.put("id",id);
                userUploadPictureService.updateProductAuditByCondition(temp);
            }
        }
        else if(classtype.equals("com")){
            for(int i = 0; i < idSize.length; i++){
                String id = idSize[i];
                temp.put("id",id);
                userUploadPictureService.updateCompanyAuditByCondition(temp);
            }
        }

        return "";
    }

    /**
     * 重新上传
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("/method/againUploadByCondition")
    public String againUploadByCondition(HttpServletRequest request,HttpServletResponse response) throws Exception{
        response.setHeader("Access-Control-Allow-Origin", "*");

        String id = request.getParameter("id");
        String classtype = request.getParameter("classtype");
        Map<String,Object> temp = new HashMap<>();
        temp.put("id",id);

        if(classtype.equals("pro")){
            ArrayList<UserUploadPicture> datas = userUploadPictureService.selectProductAuditByCondition(temp);
            if(datas.size() > 0){
                UserUploadPicture userUploadPicture = datas.get(0);
                String picturename = userUploadPicture.getObjectid()+"/"+userUploadPicture.getPicturename();
                String basePath="/var/www/html/yixiecha/upload/product/"+picturename;
                File file = new File(basePath);
                if (file.exists()) file.delete();
            }
            userUploadPictureService.deleteProductAuditById(Long.parseLong(id));
        }
        else if(classtype.equals("com")){
            ArrayList<UploadCompanyPicture> datas = userUploadPictureService.selectCompanyAuditByCondition(temp);
            if(datas.size() > 0){
                UploadCompanyPicture uploadCompanyPicture = datas.get(0);
                String picturename = uploadCompanyPicture.getUserid()+"/"+uploadCompanyPicture.getPicturename();
                String basePath="/var/www/html/yixiecha/upload/company/"+picturename;
                File file = new File(basePath);
                if (file.exists()) file.delete();
            }
            userUploadPictureService.deleteCompanyAuditById(Long.parseLong(id));
        }


        return "";
    }
}
