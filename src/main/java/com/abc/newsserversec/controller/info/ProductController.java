package com.abc.newsserversec.controller.info;

import com.abc.newsserversec.common.StaticVariable;
import com.abc.newsserversec.model.info.ESCount;
import com.abc.newsserversec.model.info.ESResultRoot;
import com.abc.newsserversec.model.info.Hit;
import com.abc.newsserversec.common.HttpHandler;
import com.abc.newsserversec.model.info.SourceSet;
import com.abc.newsserversec.model.user.UserBusiness;
import com.abc.newsserversec.service.user.UserBusinessService;
import com.abc.newsserversec.service.user.UserUploadPictureService;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Base64;
import java.util.Base64.Decoder;

/**
 * 产品数据查询控制器
 */
@RestController
public class ProductController {

    @Autowired
    private UserBusinessService userBusinessService;

    @Autowired
    private UserUploadPictureService userUploadPictureService;

    /**
     * 根据产品名称查找产品信息
     * 1.单关键词，在产品名称和型号中查找该关键词
     * 2.输入的关键词有空格的话，拆成两个关键字在产品名称和型号中查找
     * @param
     * @return
     * @throws IOException
     */
    @RequestMapping("/method/search_product_name")
    public String searchProductByName(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");

        String num = request.getParameter("num");
        String keyword = request.getParameter("keyword");
        if(num == null || keyword == null) return "fail";
        if(num.equals("") || keyword.equals("")) return "fail";

        String size = request.getParameter("size");
        String condition = "";
        String esRequest = StaticVariable.esRequest;
        SourceSet productSet = new SourceSet();
        ArrayList<Object> aggList = new ArrayList<>();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        if(keyword.indexOf(" ")>0 && !keyword.endsWith(" ")){
            String lkeyword = keyword.split(" ")[0];
            String rkeyword = keyword.split(" ")[1];
            condition = "(product_name_ch:(\\\\\""+lkeyword+"\\\\\" OR \\\\\""+rkeyword+"\\\\\")) AND (maker_name_ch:(\\\\\""+lkeyword+"\\\\\" OR \\\\\""+rkeyword+"\\\\\") OR agent:(\\\\\""+lkeyword+"\\\\\" OR \\\\\""+rkeyword+"\\\\\"))";
        }else{
            condition = "(product_name_ch:\\\\\""+keyword+"\\\\\") OR (product_mode:\\\\\""+keyword+"\\\\\") OR (register_code:\\\\\\\\\\\"\"+keyword+\"\\\\\\\\\\\")";
        }
        int from = Integer.valueOf(num);
        if(size != null) { from = from * Integer.valueOf(size); }
        else{ from = from * 10; }

        esRequest = esRequest.replaceFirst("\"#from\"",String.valueOf(from));
        if(size == null){ esRequest = esRequest.replaceFirst("\"#size\"","10"); }
        else{ esRequest = esRequest.replaceFirst("\"#size\"",size); }
        esRequest = esRequest.replaceFirst("approval_date","end_date");
        esRequest = esRequest.replaceFirst("\"#includes\"",StaticVariable.searchProductIncludeFields);
        esRequest = esRequest.replaceFirst("\"#excludes\"","");
        esRequest = esRequest.replaceFirst("\"#filter\"","");
        String postbody = esRequest.replaceFirst("#query",condition);
        postbody = postbody.replaceFirst("\"#aggs\"",StaticVariable.productAggsProductName);
        System.out.println(postbody);

        String ret = HttpHandler.httpPostCall("http://localhost:9200/product/_search", postbody);
        ESResultRoot retObj = new GsonBuilder().create().fromJson(ret, ESResultRoot.class);
        for(Hit hit:retObj.hits.hits){
            productSet.add(hit._source);
        }
        if(from == 0) {
            //计数
            String esCount = StaticVariable.esCount;
            esCount = esCount.replaceFirst("#query",condition);
            esCount = esCount.replaceFirst("\"#filter\"","");
            String countRet = HttpHandler.httpPostCall("http://localhost:9200/product/_count", esCount);
            ESCount esCt = new GsonBuilder().create().fromJson(countRet, ESCount.class);
            productSet.setMatchCount(esCt.count);

            //聚合
            ArrayList<Map> productBuckets = (ArrayList<Map>) ((Map) retObj.aggregations.get("tags")).get("buckets");
            for (Map map : productBuckets) {
                Map<String, Object> aggMap = new HashMap<>();
                String product_name = (String) map.get("key");
                int product_count = (new Double((Double) map.get("doc_count"))).intValue();

                Map max_date_map = (Map) map.get("max_date");
                Map min_date_map = (Map) map.get("min_date");
                Date max_date = null;
                Date min_date = null;
                Calendar max_c = Calendar.getInstance();
                Calendar min_c = Calendar.getInstance();
                try {
                    max_date = df.parse(String.valueOf(max_date_map.get("value_as_string")));
                    min_date = df.parse(String.valueOf(min_date_map.get("value_as_string")));
                    max_c.setTime(max_date);
                    min_c.setTime(min_date);
                    max_c.add(Calendar.DAY_OF_MONTH, 1);
                    min_c.add(Calendar.DAY_OF_MONTH, 1);
                    max_date = max_c.getTime();
                    min_date = min_c.getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Map company_name_map = (Map) map.get("company_name");
                ArrayList company_name_list = (ArrayList) company_name_map.get("buckets");
                int company_count = company_name_list.size();

                Map class_code_map = (Map) map.get("class_code");
                ArrayList class_code_list = (ArrayList) class_code_map.get("buckets");
                int code_count = class_code_list.size();

                aggMap.put("product_name", product_name);
                aggMap.put("product_count", product_count);
                aggMap.put("company_count", company_count);
                aggMap.put("code_count", code_count);
                aggMap.put("new_date", df.format(max_date));
                aggMap.put("old_date", df.format(min_date));

                aggList.add(aggMap);
            }
            productSet.setAggList(aggList);
        }
        return new GsonBuilder().create().toJson(productSet);
    }

    /**
     * 对产品信息进行筛选查询
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping("/method/search_product_filter_condition")
    public String searchProductFilterByCondition(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");

        String keyword = request.getParameter("keyword");
        String num = request.getParameter("num");
        if(keyword == null || num == null) return "fail";
        if(keyword.equals("") || num.equals("")) return "fail";

        String src_loc = request.getParameter("src_loc");
        String product_state = request.getParameter("product_state");
        String main_class = request.getParameter("main_class");
        String product_name = request.getParameter("product_name");
        String company_name = request.getParameter("company_name");
        String class_code = request.getParameter("class_code");
        String size = request.getParameter("size");

        if(src_loc == null || src_loc.equals("")) src_loc = "*";
        if(product_state == null) product_state = "";
        if(main_class == null || main_class.equals("")) main_class = "*";
        if(company_name == null) company_name = "";
        if(product_name == null) product_name = "";
        if(class_code == null) class_code = "";

        String esRequest = StaticVariable.esRequest;
        SourceSet productSet = new SourceSet();
        Calendar current = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date current_date = current.getTime();

        ArrayList<Object> aggList = new ArrayList<>();
        String condition = "";
        String postbody = "";
        String ret = "";
        if(keyword.indexOf(" ")>0 && !keyword.endsWith(" ")){
            String lkeyword = keyword.split(" ")[0];
            String rkeyword = keyword.split(" ")[1];
            condition =  "(maker_name_ch:(\\\\\""+lkeyword+"\\\\\" OR \\\\\""+rkeyword+"\\\\\") OR agent:(\\\\\""+lkeyword+"\\\\\" OR \\\\\""+rkeyword+"\\\\\"))" +
                    " AND (src_loc:" + src_loc + ") AND (main_class:" + main_class + ")";
            if(!product_name.equals("")){
                condition = condition + " AND (product_name_agg:(\\\\\"" + lkeyword + "\\\\\" OR \\\\\"" + rkeyword + "\\\\\"))";
            }else {
                condition = condition + " AND (product_name_ch:(\\\\\"" + lkeyword + "\\\\\" OR \\\\\"" + rkeyword + "\\\\\"))";
            }
        }else{
            condition = "(src_loc:" + src_loc + ") AND (main_class:" + main_class + ")";
            if(!product_name.equals("")) {
                condition = condition + " AND (product_name_agg:\\\\\"" + product_name + "\\\\\")";
            }else {
                condition = condition + " AND ((product_name_ch:\\\\\"" + keyword + "\\\\\") OR (product_mode:\\\\\"" + keyword + "\\\\\") OR (register_code:\\\\\""+keyword+"\\\\\"))";
            }
        }

        if(!company_name.equals("") && !company_name.equals("yes")) condition = condition+ " AND company_name_agg:\\\\\""+company_name+"\\\\\"";
        if(!class_code.equals("") && !class_code.equals("yes")) condition = condition+ " AND class_code:\\\\\""+class_code+"\\\\\"";
//        String filter = "{\"range\":{\"approval_date\":{\"gt\":\"1990-10-01\",\"lt\":\"2190-01-01\"}}}";;
        String filter = "";
        if(product_state.equals("有效")) filter += "{\"range\":{\"end_date\":{\"gte\":\""+df.format(current_date)+"\"}}}";
        else if(product_state.equals("无效")) filter += "{\"range\":{\"end_date\":{\"lte\":\""+df.format(current_date)+"\"}}}";

        int from = Integer.valueOf(num);
        if(size != null) { from = from * Integer.valueOf(size); }
        else{ from = from * 10; }
        esRequest = esRequest.replaceFirst("\"#from\"",String.valueOf(from));
        if(size == null){ esRequest = esRequest.replaceFirst("\"#size\"","10"); }
        else{ esRequest = esRequest.replaceFirst("\"#size\"",size); }
        esRequest = esRequest.replaceFirst("approval_date","end_date");
        esRequest = esRequest.replaceFirst("\"#includes\"",StaticVariable.searchProductIncludeFields);
        esRequest = esRequest.replaceFirst("\"#excludes\"","");
        esRequest = esRequest.replaceFirst("\"#filter\"",filter);
        esRequest = esRequest.replaceFirst("#query",condition);

        if(company_name.equals("") && class_code.equals("")){
            postbody = esRequest.replaceFirst("\"#aggs\"",StaticVariable.productAggsProductName);
        }
        if(!product_name.equals("") && !company_name.equals("") && class_code.equals("")) {
            postbody = esRequest.replaceFirst("\"#aggs\"", StaticVariable.productAggsCompanyName);
        }
        if(!product_name.equals("") && !class_code.equals("") && company_name.equals("")) {
            postbody = esRequest.replaceFirst("\"#aggs\"", StaticVariable.productAggsClassCode);
        }
        System.out.println(postbody);
        ret = HttpHandler.httpPostCall("http://localhost:9200/product/_search", postbody);
        ESResultRoot retObj = new GsonBuilder().create().fromJson(ret, ESResultRoot.class);
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
            //负责产品的名片用户头像
            Map<String,Object> map = new HashMap<>();
            String productId = (String) source.get("id");
            map.put("productids","%"+productId+"%");
            ArrayList<Map<String,Object>> list = userBusinessService.selectUserheadimgByProductId(map);
            source.put("headimgList",list);

            productSet.add(source);
        }

        if(from == 0){
            //计数
            String esCount = StaticVariable.esCount;
            esCount = esCount.replaceFirst("#query",condition);
            esCount = esCount.replaceFirst("\"#filter\"",filter);
            String countRet = HttpHandler.httpPostCall("http://localhost:9200/product/_count", esCount);
            ESCount esCt = new GsonBuilder().create().fromJson(countRet, ESCount.class);
            productSet.setMatchCount(esCt.count);
            //聚合
            ArrayList<Map> productBuckets = (ArrayList<Map>) ((Map) retObj.aggregations.get("tags")).get("buckets");
            Date max_date = null;
            Date min_date = null;
            Calendar max_c = Calendar.getInstance();
            Calendar min_c = Calendar.getInstance();
            if(company_name.equals("") && class_code.equals("")) {
                for (Map map : productBuckets) {
                    Map<String, Object> aggMap = new HashMap<>();
                    String name = (String) map.get("key");
                    int product_count = (new Double((Double) map.get("doc_count"))).intValue();

                    Map max_date_map = (Map) map.get("max_date");
                    Map min_date_map = (Map) map.get("min_date");
                    try {
                        max_date = df.parse(String.valueOf(max_date_map.get("value_as_string")));
                        min_date = df.parse(String.valueOf(min_date_map.get("value_as_string")));
                        max_c.setTime(max_date);
                        min_c.setTime(min_date);
                        max_c.add(Calendar.DAY_OF_MONTH, 1);
                        min_c.add(Calendar.DAY_OF_MONTH, 1);
                        max_date = max_c.getTime();
                        min_date = min_c.getTime();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    Map company_name_map = (Map) map.get("company_name");
                    ArrayList company_name_list = (ArrayList) company_name_map.get("buckets");
                    int company_count = company_name_list.size();

                    Map class_code_map = (Map) map.get("class_code");
                    ArrayList class_code_list = (ArrayList) class_code_map.get("buckets");
                    int code_count = class_code_list.size();

                    aggMap.put("product_name", name);
                    aggMap.put("product_count", product_count);
                    aggMap.put("company_count", company_count);
                    aggMap.put("code_count", code_count);
                    aggMap.put("new_date", df.format(max_date));
                    aggMap.put("old_date", df.format(min_date));

                    aggList.add(aggMap);
                }
            }
            if(!product_name.equals("") && !company_name.equals("") && class_code.equals("")) {
                for (Map map : productBuckets) {
                    Map<String, Object> aggMap = new HashMap<>();
                    String company_agg_name = (String) map.get("key");
                    int product_count = (new Double((Double) map.get("doc_count"))).intValue();

                    Map max_date_map = (Map) map.get("max_date");
                    Map min_date_map = (Map) map.get("min_date");
                    try {
                        max_date = df.parse(String.valueOf(max_date_map.get("value_as_string")));
                        min_date = df.parse(String.valueOf(min_date_map.get("value_as_string")));
                        max_c.setTime(max_date);
                        min_c.setTime(min_date);
                        max_c.add(Calendar.DAY_OF_MONTH, 1);
                        min_c.add(Calendar.DAY_OF_MONTH, 1);
                        max_date = max_c.getTime();
                        min_date = min_c.getTime();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    aggMap.put("company_name", company_agg_name);
                    aggMap.put("product_count", product_count);
                    aggMap.put("new_date", df.format(max_date));
                    aggMap.put("old_date", df.format(min_date));
                    aggList.add(aggMap);
                }
            }
            if(!product_name.equals("") && !class_code.equals("") && company_name.equals("")) {
                for (Map map : productBuckets) {
                    Map<String, Object> aggMap = new HashMap<>();
                    String code_name = (String) map.get("key");
                    int product_count = (new Double((Double) map.get("doc_count"))).intValue();

                    Map max_date_map = (Map) map.get("max_date");
                    Map min_date_map = (Map) map.get("min_date");
                    try {
                        max_date = df.parse(String.valueOf(max_date_map.get("value_as_string")));
                        min_date = df.parse(String.valueOf(min_date_map.get("value_as_string")));
                        max_c.setTime(max_date);
                        min_c.setTime(min_date);
                        max_c.add(Calendar.DAY_OF_MONTH, 1);
                        min_c.add(Calendar.DAY_OF_MONTH, 1);
                        max_date = max_c.getTime();
                        min_date = min_c.getTime();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    aggMap.put("code_name", code_name);
                    aggMap.put("product_count", product_count);
                    aggMap.put("new_date", df.format(max_date));
                    aggMap.put("old_date", df.format(min_date));
                    aggList.add(aggMap);
                }
            }
            productSet.setAggList(aggList);
        }
        return new GsonBuilder().create().toJson(productSet);
    }

    /**
     * 根据id来查询产品信息
     * @param
     * @return
     * @throws IOException
     */
    @RequestMapping("/method/search_product_id")
    public String searchProductById(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");

        String id = request.getParameter("id");
        if(id == null) return "fail";
        if(id.equals("")) return "fail";

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


    /**
     * 根据企业名称查出该企业的产品
     * @param
     * @return
     * @throws IOException
     */
    @RequestMapping("/method/search_product_company_name")
    public String searchProductByCompanyName(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");

        String num = request.getParameter("num");
        String keyword = request.getParameter("keyword");
        String product_state = request.getParameter("product_state");
        String size = request.getParameter("size");
        String userid = request.getParameter("userid");//名片使用

        if(product_state == null) product_state = "";
        if(num == null || keyword == null) return "fail";
        if(num.equals("") || keyword.equals("")) return "fail";

        Calendar current = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date current_date = current.getTime();
        String userServicePro = "";
        SourceSet productSet = new SourceSet();

        String esRequest = StaticVariable.esRequest;
        int from = Integer.valueOf(num);
        if(size != null) { from = from * Integer.valueOf(size); }
        else{ from = from * 10; }

        String filter = "";//名片中选择公司有效的产品
        if(product_state.equals("有效")) filter = "{\"range\":{\"end_date\":{\"gte\":\""+df.format(current_date)+"\"}}}";
        else if(product_state.equals("无效")) filter = "{\"range\":{\"end_date\":{\"lte\":\""+df.format(current_date)+"\"}}}";
        String condition = "company_name_agg:\\\\\""+keyword+"\\\\\" OR agent_agg:\\\\\""+keyword+"\\\\\"";
        esRequest = esRequest.replaceFirst("\"#from\"",String.valueOf(from));
        if(size == null){ esRequest = esRequest.replaceFirst("\"#size\"","10"); }
        else{ esRequest = esRequest.replaceFirst("\"#size\"",size); }
        esRequest = esRequest.replaceFirst("approval_date","end_date");
        esRequest = esRequest.replaceFirst("\"#includes\"",StaticVariable.searchProductIncludeFields);
        esRequest = esRequest.replaceFirst("\"#excludes\"","");
        esRequest = esRequest.replaceFirst("\"#filter\"",filter);

        String postbody = esRequest.replaceFirst("#query",condition);
        postbody = postbody.replaceFirst("\"#aggs\"","{}");
        System.out.println("postbody="+postbody);
        //根据用户id获得用户负责产品的信息
        if(userid != null && !userid.equals("")){
            /*ArrayList<Map<String,Object>> serviceList = userBusinessService.selectProductInfosByUserid(Long.parseLong(userid));
            if(serviceList.size() > 0){
                Map<String,Object> userService = serviceList.get(0);
                userServicePro = (String) userService.get("productids");
                userServicePro = userServicePro.substring(0,userServicePro.length()-1);
                productSet.setSelectedDatas(Arrays.asList(userServicePro.split(",")));
            }*/
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("userid",userid);
            dataMap.put("companyname",keyword);
            ArrayList<UserBusiness> serviceList = userBusinessService.selectUserBusinessByCondition(dataMap);
            if(serviceList.size() > 0){
               UserBusiness userService = serviceList.get(0);
                userServicePro = userService.getProductids();
                userServicePro = userServicePro.substring(0,userServicePro.length()-1);
                productSet.setSelectedDatas(Arrays.asList(userServicePro.split(",")));
                productSet.setSelectedDatasName(Arrays.asList(userService.getProductnames().substring(0,userService.getProductnames().length()-1).split(",")));
            }
        }

        String ret = HttpHandler.httpPostCall("http://localhost:9200/product/_search", postbody);
        ESResultRoot retObj = new GsonBuilder().create().fromJson(ret, ESResultRoot.class);
        for(Hit hit:retObj.hits.hits){
            Map<String, Object> source = (Map<String, Object>) hit._source;
            if(userid != null && !userid.equals("")) {
                if (userServicePro.contains((String) source.get("id"))) source.put("selected", "true");
                else source.put("selected", "false");
            }
            try {
                Date end_date = df.parse(String.valueOf(source.get("end_date")));
                if (current_date.getTime() > end_date.getTime()){ source.put("product_state", "无效"); }
                else{ source.put("product_state", "有效"); }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            productSet.add(source);
        }
        if(from == 0) {
            //计数
            String esCount = StaticVariable.esCount;
            esCount = esCount.replaceFirst("#query", condition);
            esCount = esCount.replaceFirst("\"#filter\"",filter);

            String countRet = HttpHandler.httpPostCall("http://localhost:9200/product/_count", esCount);
            ESCount esCt = new GsonBuilder().create().fromJson(countRet, ESCount.class);
            productSet.setMatchCount(esCt.count);
        }
        return new GsonBuilder().create().toJson(productSet);
    }

    @RequestMapping("/method/queryMLByProduct_name")
    public String queryMLByProduct_name(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");

        String num = request.getParameter("num");
        String keyword = request.getParameter("keyword");

        String product_state = request.getParameter("product_state");
        if(product_state == null) product_state = "";
        if(num == null || keyword == null) return "fail";
        if(num.equals("") || keyword.equals("")) return "fail";
        Calendar current = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date current_date = current.getTime();

        String size = request.getParameter("size");
        String esRequest = StaticVariable.esRequest;
        int from = Integer.valueOf(num);
        if(size != null) { from = from * Integer.valueOf(size); }
        else{ from = from * 10; }

        String filter = "";//名片中选择公司有效的产品
        if(product_state.equals("有效")) filter = "{\"range\":{\"end_date\":{\"gte\":\""+df.format(current_date)+"\"}}}";
        else if(product_state.equals("无效")) filter = "{\"range\":{\"end_date\":{\"lte\":\""+df.format(current_date)+"\"}}}";

        String condition = "company_name_agg:\\\\\""+keyword+"\\\\\"";
        esRequest = esRequest.replaceFirst("\"#from\"",String.valueOf(from));
        if(size == null){ esRequest = esRequest.replaceFirst("\"#size\"","10"); }
        else{ esRequest = esRequest.replaceFirst("\"#size\"",size); }
        esRequest = esRequest.replaceFirst("approval_date","end_date");
        esRequest = esRequest.replaceFirst("\"#includes\"",StaticVariable.searchProductIncludeFields);
        esRequest = esRequest.replaceFirst("\"#excludes\"","");
        esRequest = esRequest.replaceFirst("\"#filter\"",filter);

        String postbody = esRequest.replaceFirst("#query",condition);
        postbody = postbody.replaceFirst("\"#aggs\"","{}");
        System.out.println("postbody="+postbody);

        String ret = HttpHandler.httpPostCall("http://localhost:9200/product/_search", postbody);
        ESResultRoot retObj = new GsonBuilder().create().fromJson(ret, ESResultRoot.class);
        SourceSet productSet = new SourceSet();
        for(Hit hit:retObj.hits.hits){
            Map<String, Object> source = (Map<String, Object>) hit._source;
            try {
                Date end_date = df.parse(String.valueOf(source.get("end_date")));
                if (current_date.getTime() > end_date.getTime()){
                    source.put("product_state", "无效");
                }else{
                    source.put("product_state", "有效");
                }
                List<Object> mlList = queryMLByProduct_name(source.get("product_name_ch").toString());
                source.put("mlList",mlList);
            } catch (Exception e) {
                e.printStackTrace();
            }
            productSet.add(source);
        }
        if(from == 0) {
            //计数
            String esCount = StaticVariable.esCount;
            esCount = esCount.replaceFirst("#query", condition);
            esCount = esCount.replaceFirst("\"#filter\"",filter);

            String countRet = HttpHandler.httpPostCall("http://localhost:9200/product/_count", esCount);
            ESCount esCt = new GsonBuilder().create().fromJson(countRet, ESCount.class);
            productSet.setMatchCount(esCt.count);
        }
        return new GsonBuilder().create().toJson(productSet);
    }

    /**
     * 通过产品名称查看分类目录的六位编号
     * @return
     * @throws Exception
     */
    public List<Object> queryMLByProduct_name(String name) throws  Exception{
        String postbody = "{\"size\":100,\"sort\":[{\"flow_number\":{\"order\":\"asc\"}}],\"query\":{\"bool\":{\"must\":[{\"query_string\":{" +
                "\"analyze_wildcard\":true,\"query\":\"product_example:\\\""+name+"\\\"\",\"phrase_slop\":0}}]}},\"_source\":{\"include\":[\"directory_number\"," +
                "\"first_product_number\",\"second_product_number\"]}}";
        String ret = HttpHandler.httpPostCall("http://localhost:9200/catalogs/catalog/_search", postbody);
        ESResultRoot retObj = new GsonBuilder().create().fromJson(ret, ESResultRoot.class);
        List<Object> ll = new ArrayList<>();
        for(Hit hit:retObj.hits.hits){
            Map<String, Object> source = (Map<String, Object>) hit._source;
            ll.add(source);
        }

        return ll;
    }

    /**
     * 根据产品名称查找同名产品信息
     * @param
     * @return
     * @throws IOException
     */
    @RequestMapping("/method/search_product_same_name")
    public String searchProductBySameName(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");

        String num = request.getParameter("num");
        String keyword = request.getParameter("keyword");
        String id = request.getParameter("id");
        if(num == null || keyword == null) return "fail";
        if(num.equals("") || keyword.equals("")) return "fail";

        String size = request.getParameter("size");
        String condition = "";
        String esRequest = StaticVariable.esRequest;
        SourceSet productSet = new SourceSet();
        Calendar current = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date current_date = current.getTime();

        condition = "(product_name_agg:\\\\\""+keyword+"\\\\\")";

        int from = Integer.valueOf(num);
        if(size != null) { from = from * Integer.valueOf(size); }
        else{ from = from * 5; }

        esRequest = esRequest.replaceFirst("\"#from\"",String.valueOf(from));
        if(size == null){ esRequest = esRequest.replaceFirst("\"#size\"","5"); }
        else{ esRequest = esRequest.replaceFirst("\"#size\"",size); }
        esRequest = esRequest.replaceFirst("approval_date","end_date");
        esRequest = esRequest.replaceFirst("\"#includes\"",StaticVariable.searchProductIncludeFields);
        esRequest = esRequest.replaceFirst("\"#excludes\"","");
        esRequest = esRequest.replaceFirst("\"#filter\"","");
        String postbody = esRequest.replaceFirst("#query",condition);
        postbody = postbody.replaceFirst("\"#aggs\"","{}");

        if(id != null && !id.equals("")){
            String must_not = "{\"term\": { \"id\":    \"" + id + "\"}}";
            int index = postbody.indexOf("must_not");
            postbody = postbody.substring(0,index+10) + must_not + postbody.substring(index+13);
        }
        System.out.println(postbody);

        String ret = HttpHandler.httpPostCall("http://localhost:9200/product/_search", postbody);
        ESResultRoot retObj = new GsonBuilder().create().fromJson(ret, ESResultRoot.class);
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
        if(from == 0) {
            //计数
            String esCount = StaticVariable.esCount;
            esCount = esCount.replaceFirst("#query",condition);
            esCount = esCount.replaceFirst("\"#filter\"","");
            String countRet = HttpHandler.httpPostCall("http://localhost:9200/product/_count", esCount);
            ESCount esCt = new GsonBuilder().create().fromJson(countRet, ESCount.class);
            if(id !=null && !id.equals("")) esCt.count--;
            productSet.setMatchCount(esCt.count);
        }
        return new GsonBuilder().create().toJson(productSet);
    }

    /**
     * 根据年份和管理类别聚合产品数据
     * @param
     * @return
     * @throws IOException
     */
    @RequestMapping("/method/agg_product")
    public String AggProduct(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        String keyword = request.getParameter("keyword");
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        c.setTime(new Date());
        c.add(Calendar.YEAR, 1);
        Date y = c.getTime();
        String year = df.format(y);
        String esRequest = "{" +
                "\"version\": true," +
                "\"size\": 0," +
                "\"query\": {" +
                "\"bool\": {" +
                "\"must\": [{" +
                "\"query_string\":{"+
                "\"analyze_wildcard\":true,"+
                "\"query\":"+"\"product_name_ch:\\\""+keyword+"\\\" OR product_mode:\\\""+keyword+"\\\" OR register_code: \\\""+keyword+ "\\\"\","+
                "\"phrase_slop\":5"+
                "}"+
                "}]," +
                "\"filter\": [{" +
                "\"range\": {" +
                "\"approval_date\": {" +
                "\"gte\": \"2008-01-01\"," +
                "\"lte\":\""+ year + "\"" +
                "}" +
                "}" +
                "}]" +
                "}" +
                "}," +
                "\"aggs\": {" +
                "\"tags\": {" +
                "\"date_histogram\": {" +
                "\"field\": \"approval_date\"," +
                "\"interval\": \"year\"," +
                "\"format\": \"yyyy\"" +
                "}," +
                "\"aggs\":{" +
                "\"main_class\":{" +
                "\"terms\":{" +
                "\"field\": \"main_class\"" +
                "}" +
                "}" +
                "}" +
                "}" +
                "}" +
                "}";
        HashMap<String,Object> map = new HashMap<>();
        ArrayList<String> yearList = new ArrayList<>();
        ArrayList<Integer> oneClassList = new ArrayList<>();
        ArrayList<Integer> twoClassList = new ArrayList<>();
        ArrayList<Integer> threeClassList = new ArrayList<>();

        String ret = HttpHandler.httpPostCall("http://localhost:9200/product/_search", esRequest);
        ESResultRoot retObj = new GsonBuilder().create().fromJson(ret, ESResultRoot.class);
        ArrayList<Map> productBuckets = (ArrayList<Map>) ((Map) retObj.aggregations.get("tags")).get("buckets");
        int i = 1;
        for (Map temp : productBuckets) {
            String name = (String) temp.get("key_as_string");
            int product_count = (new Double((Double) temp.get("doc_count"))).intValue();
            yearList.add(name);
            if (product_count != 0) {
                Map main_class = (Map) temp.get("main_class");
                ArrayList<Map> main_class_list = (ArrayList<Map>) main_class.get("buckets");
                for (Map mainClassMap : main_class_list) {
                    String mainName = (String) mainClassMap.get("key");
                    int count = (new Double((Double) mainClassMap.get("doc_count"))).intValue();
                    if (mainName.equals("1")) oneClassList.add(count);
                    else if (mainName.equals("2")) twoClassList.add(count);
                    else if (mainName.equals("3")) threeClassList.add(count);
                }
            }

            if(oneClassList.size() != i) oneClassList.add(0);
            if(twoClassList.size() != i) twoClassList.add(0);
            if(threeClassList.size() != i) threeClassList.add(0);
            i++;
        }
        map.put("yearList",yearList);
        map.put("oneClassList",oneClassList);
        map.put("twoClassList",twoClassList);
        map.put("threeClassList",threeClassList);
        System.out.println("year:"+yearList.size()+",one:"+oneClassList.size()+",two:"+twoClassList.size()+",three:"+threeClassList.size());
        return new GsonBuilder().create().toJson(map);
    }

    /**
     * 为产品上传图片
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/method/uploadPicture")
    public String uploadPicture(@RequestParam("file") String file, HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        String userid = request.getParameter("userid");
        String name = request.getParameter("name");
        Decoder decoder = Base64.getDecoder();// 去掉base64编码的头部 如："data:image/jpeg;base64," 如果不去，转换的图片不可以查看
        file = file.substring(23);
        //解码
        byte[] imgByte = decoder.decode(file);
        String picturename = userid + getFileName();
        String path ="/var/www/html/yixiecha/upload/product/" + id + File.separator + picturename;
        File dir = new File(path);
        if(!dir.getParentFile().exists()){
            dir.getParentFile().mkdirs();
        }

        FileOutputStream out = null; // 输出文件路径
        try {
            out = new FileOutputStream(path);
            out.write(imgByte);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = df.format(new Date());
        Map<String, Object> temp = new HashMap<>();
        temp.put("userid", userid);
        temp.put("objectname", name);
        temp.put("objectid", id);
        temp.put("picturename", picturename);
        temp.put("state", "1");
        temp.put("createdate", date);
        userUploadPictureService.insertUserUploadPicture(temp);

        return "success";
    }

    /** 创建文件名称 内容：时间戳+随机数 */
    private String getFileName() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String timeStr = sdf.format(new Date());
        String str = RandomStringUtils.random(5,"abcdefghijklmnopqrstuvwxyz1234567890");
        String name = timeStr + str + ".jpg";
        return name;
    }

}
