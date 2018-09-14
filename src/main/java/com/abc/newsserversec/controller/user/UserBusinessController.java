package com.abc.newsserversec.controller.user;

import com.abc.newsserversec.common.HttpHandler;
import com.abc.newsserversec.common.StaticVariable;
import com.abc.newsserversec.model.info.ESResultRoot;
import com.abc.newsserversec.model.info.Hit;
import com.abc.newsserversec.model.info.SourceSet;
import com.abc.newsserversec.model.user.UserBusiness;
import com.abc.newsserversec.service.user.UserBusinessService;
import com.abc.newsserversec.service.wechat.WxcardInfoService;
import com.google.gson.GsonBuilder;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 用户的业务信息，负责的产品
 */
@RestController
public class UserBusinessController {
    @Autowired
    private UserBusinessService userBusinessService;

    @Autowired
    private WxcardInfoService wxcardInfoService;

    @RequestMapping("/method/operBusiness")
    public int insertInfo(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        String userid = request.getParameter("userid");
        String productids = request.getParameter("productids");
        String companyname = request.getParameter("companyname");
        if(userid == null || productids == null){return -1;}
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("userid",userid);
        ArrayList<UserBusiness> userBusiness = userBusinessService.selectUserBusinessByCondition(dataMap);
        dataMap.put("companyname",companyname);
        dataMap.put("productids",productids);
        dataMap.put("createtime",queryCurrentTime());
        if(userBusiness.size() == 0){
            return userBusinessService.insertUserBusiness(dataMap);
        }else{
            return userBusinessService.updateUserBusiness(dataMap);
        }
    }

    /**
     * 更新负责的区域
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/method/updateBusiness")
    public int updateInfo(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        String userid = request.getParameter("userid");
        String areaids = request.getParameter("areaids");
        if(userid == null || areaids == null){return -1;}
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("userid",userid);
        dataMap.put("areaids",areaids);
        return userBusinessService.updateUserBusiness(dataMap);
    }
    @RequestMapping("method/selectInfoById")
    public String selectInfoById(HttpServletRequest request, HttpServletResponse response){
        response.setHeader("Access-Control-Allow-Origin", "*");
        String userid = request.getParameter("userid");
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("userid",userid);
        ArrayList<UserBusiness> userBusinesses = userBusinessService.selectUserBusinessByCondition(dataMap);
        return new GsonBuilder().create().toJson(userBusinesses);
    }
    /**
     * 查找负责的产品
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping("/method/selectInfo")
    public String selectInfo(HttpServletRequest request, HttpServletResponse response) throws IOException{
        response.setHeader("Access-Control-Allow-Origin", "*");
        ArrayList<Object> array = new ArrayList<>();
        String userid = request.getParameter("userid");
        String size = request.getParameter("size");//前两条,没有就是全部
        if(userid == null){return "fail";}
        if(size == null){size ="";}
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("userid",userid);
        ArrayList<UserBusiness> userBusinesses = userBusinessService.selectUserBusinessByCondition(dataMap);
        if(userBusinesses.size() > 0){
            String productids = userBusinesses.get(0).getProductids();
            String[] productidArr = productids.split(",");
            int count = 0;
            if(!size.equals("")){
                if(Integer.parseInt(size) < productidArr.length){
                    count = Integer.parseInt(size);
                }else{
                    count = productidArr.length;
                }
            }else{
                count = productidArr.length;
            }
            for(int i = 0;i < count;i++){
                JSONObject json = JSONObject.fromObject(this.searchProductById(productidArr[i]));
                array.add(JSONArray.fromObject(json.get("datas")).get(0));
            }
        }
        return new GsonBuilder().create().toJson(array);
    }

    /**
     * 查找用户的负责区域
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping("/method/selectArea")
    public String selectArea(HttpServletRequest request, HttpServletResponse response) throws IOException{
        response.setHeader("Access-Control-Allow-Origin", "*");
        JSONArray arr = new JSONArray();
        String userid = request.getParameter("userid");
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("userid",userid);
        ArrayList<UserBusiness> userBusinesses = userBusinessService.selectUserBusinessByCondition(dataMap);
        if(userBusinesses.size() > 0){
            String areaids = userBusinesses.get(0).getAreaids();
            if(areaids == null || "".equals(areaids)){
                return "fail";
            }
            System.out.println("地区："+areaids);
            String[] areaidArr = areaids.split(",");
            for(int i = 0;i < areaidArr.length;i++){
                arr.add(wxcardInfoService.selectRegionById(Integer.parseInt(areaidArr[i])));
            }
        }
        return arr.toString();
    }


    /**
     *根据ID找产品
     * @return
     * @throws IOException
     */
    public String searchProductById(String id) throws IOException {
        String esRequest = StaticVariable.esRequest;
        SourceSet productSet = new SourceSet();
        Calendar current = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date current_date = current.getTime();

        esRequest = esRequest.replaceFirst("\"#from\"",String.valueOf(0));
        esRequest = esRequest.replaceFirst("\"#size\"","10");
        esRequest = esRequest.replaceFirst("\"#includes\"","");
        esRequest = esRequest.replaceFirst("\"#excludes\"",StaticVariable.searchProductExcludeFields);
        esRequest = esRequest.replaceFirst("\"#aggs\"","{}");
        esRequest = esRequest.replaceFirst("\"#filter\"","");
        String condition = "id:\\\\\""+id+"\\\\\"";
        String postbody = esRequest.replaceFirst("#query",condition);
        System.out.println("postbody="+postbody);

        String ret = HttpHandler.httpPostCall("http://localhost:9200/product/_search", postbody);
        ESResultRoot retObj = new GsonBuilder().create().fromJson(ret, ESResultRoot.class);
        productSet.setMatchCount(retObj.hits.hits.size());
        for(Hit hit:retObj.hits.hits){
            Map<String, Object> source = (Map<String, Object>) hit._source;
            try {
                Date end_date = df.parse(String.valueOf(source.get("end_date")));
                if (current_date.getTime() > end_date.getTime()){
                    source.put("product_state", "无效");
                }else{
                    source.put("product_state", "有效");
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            productSet.add(source);
        }
        return new GsonBuilder().create().toJson(productSet);
    }

    public String queryCurrentTime(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(new Date());
    }
}
