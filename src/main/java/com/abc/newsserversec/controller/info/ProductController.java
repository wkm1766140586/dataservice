package com.abc.newsserversec.controller.info;

import com.abc.newsserversec.common.StaticVariable;
import com.abc.newsserversec.model.info.ESCount;
import com.abc.newsserversec.model.info.ESResultRoot;
import com.abc.newsserversec.model.info.Hit;
import com.abc.newsserversec.common.HttpHandler;
import com.abc.newsserversec.model.info.SourceSet;
import com.google.gson.GsonBuilder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 产品数据查询控制器
 */
@RestController
public class ProductController {

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
            condition = "(product_name_ch:(\\\\\""+lkeyword+"\\\\\" OR \\\\\""+rkeyword+"\\\\\")) AND (maker_name_ch:(\\\\\""+lkeyword+"\\\\\" OR \\\\\""+rkeyword+"\\\\\"))";
        }else{
            condition = "(product_name_ch:\\\\\""+keyword+"\\\\\") OR (product_mode:\\\\\""+keyword+"\\\\\")";
        }
        int from = Integer.valueOf(num);
        from = from*10;

        esRequest = esRequest.replaceFirst("\"#from\"",String.valueOf(from));
        if(size == null){ esRequest = esRequest.replaceFirst("\"#size\"","10"); }
        else{ esRequest = esRequest.replaceFirst("\"#size\"",size); }
        esRequest = esRequest.replaceFirst("\"#includes\"",StaticVariable.searchProductIncludeFields);
        esRequest = esRequest.replaceFirst("\"#excludes\"","");
        String postbody = esRequest.replaceFirst("#query",condition);
        postbody = postbody.replaceFirst("\"#aggs\"",StaticVariable.productAggsProductName);
        System.out.println(postbody);

        String ret = HttpHandler.httpPostCall("http://localhost:9200/second_product/_search", postbody);
        ESResultRoot retObj = new GsonBuilder().create().fromJson(ret, ESResultRoot.class);
        for(Hit hit:retObj.hits.hits){
            productSet.add(hit._source);
        }
        if(from == 0) {
            //计数
            String esCount = StaticVariable.esCount;
            esCount = esCount.replaceFirst("#query",condition);
            String countRet = HttpHandler.httpPostCall("http://localhost:9200/second_product/_count", esCount);
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
        if(product_state == null || product_state.equals("")) product_state = "*";
        if(main_class == null || main_class.equals("")) main_class = "*";
        if(!product_state.equals("*")) product_state = "\\\\\""+product_state+"\\\\\"";
        if(company_name == null) company_name = "";
        if(product_name == null) product_name = "";
        if(class_code == null) class_code = "";

        String esRequest = StaticVariable.esRequest;
        SourceSet productSet = new SourceSet();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        ArrayList<Object> aggList = new ArrayList<>();
        String condition = "";
        String postbody = "";
        String ret = "";
        if(keyword.indexOf(" ")>0 && !keyword.endsWith(" ")){
            String lkeyword = keyword.split(" ")[0];
            String rkeyword = keyword.split(" ")[1];
            condition =  "(maker_name_ch:(\\\\\"" + lkeyword + "\\\\\" OR \\\\\"" + rkeyword + "\\\\\")))" +
                    " AND (src_loc:" + src_loc + ") AND (product_state:" + product_state + ") AND (main_class:" + main_class + ")";
            if(!product_name.equals("")){
                condition = condition + " AND ((product_name_agg:(\\\\\"" + lkeyword + "\\\\\" OR \\\\\"" + rkeyword + "\\\\\"))";
            }else {
                condition = condition + " AND ((product_name_ch:(\\\\\"" + lkeyword + "\\\\\" OR \\\\\"" + rkeyword + "\\\\\"))";
            }
        }else{
            condition = "(src_loc:" + src_loc + ") AND (product_state:" + product_state + ") AND (main_class:" + main_class + ")";
            if(!product_name.equals("")) {
                condition = condition + " AND (product_name_agg:\\\\\"" + product_name + "\\\\\")";
            }else {
                condition = condition + " AND ((product_name_ch:\\\\\"" + keyword + "\\\\\") OR (product_mode:\\\\\"" + keyword + "\\\\\"))";
            }
        }
        if(!company_name.equals("") && !company_name.equals("yes")) condition = condition+ " AND company_name_agg:\\\\\""+company_name+"\\\\\"";
        if(!class_code.equals("") && !class_code.equals("yes")) condition = condition+ " AND class_code:\\\\\""+class_code+"\\\\\"";

        int from = Integer.valueOf(num);
        from = from * 10;
        esRequest = esRequest.replaceFirst("\"#from\"",String.valueOf(from));
        if(size == null){ esRequest = esRequest.replaceFirst("\"#size\"","10"); }
        else{ esRequest = esRequest.replaceFirst("\"#size\"",size); }
        esRequest = esRequest.replaceFirst("\"#includes\"",StaticVariable.searchProductIncludeFields);
        esRequest = esRequest.replaceFirst("\"#excludes\"","");
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
        ret = HttpHandler.httpPostCall("http://localhost:9200/second_product/_search", postbody);
        System.out.println(ret);
        ESResultRoot retObj = new GsonBuilder().create().fromJson(ret, ESResultRoot.class);
        for(Hit hit:retObj.hits.hits){
            productSet.add(hit._source);
        }

        if(from == 0){
            //计数
            String esCount = StaticVariable.esCount;
            esCount = esCount.replaceFirst("#query",condition);
            String countRet = HttpHandler.httpPostCall("http://localhost:9200/second_product/_count", esCount);
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

        esRequest = esRequest.replaceFirst("\"#from\"",String.valueOf(0));
        esRequest = esRequest.replaceFirst("\"#size\"","10");
        esRequest = esRequest.replaceFirst("#includes","*");
        esRequest = esRequest.replaceFirst("\"#excludes\"",StaticVariable.ExcludeFields+","+StaticVariable.searchProAndComExcludeFields+","+StaticVariable.searchProductExcludeFields);
        esRequest = esRequest.replaceFirst("\"#aggs\"","{}");
        String condition = "id:\\\\\""+id+"\\\\\"";
        String postbody = esRequest.replaceFirst("#query",condition);
        System.out.println("postbody="+postbody);

        String ret = HttpHandler.httpPostCall("http://localhost:9200/second_product/_search", postbody);
        ESResultRoot retObj = new GsonBuilder().create().fromJson(ret, ESResultRoot.class);
        productSet.setMatchCount(retObj.hits.hits.size());
        for(Hit hit:retObj.hits.hits){
            Map map = (Map)hit._source;
            map.put("_id",hit._id);
            productSet.add(map);
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
        if(num == null || keyword == null) return "fail";
        if(num.equals("") || keyword.equals("")) return "fail";

        String size = request.getParameter("size");
        String esRequest = StaticVariable.esRequest;
        int from = Integer.valueOf(num);
        from = from*10;

        String condition = "company_name_agg:\\\\\""+keyword+"\\\\\"";
        esRequest = esRequest.replaceFirst("\"#from\"",String.valueOf(from));
        if(size == null){ esRequest = esRequest.replaceFirst("\"#size\"","10"); }
        else{ esRequest = esRequest.replaceFirst("\"#size\"",size); }
        esRequest = esRequest.replaceFirst("\"#includes\"",StaticVariable.searchProductIncludeFields);
        esRequest = esRequest.replaceFirst("\"#excludes\"","");
        String postbody = esRequest.replaceFirst("#query",condition);
        postbody = postbody.replaceFirst("\"#aggs\"","{}");
        System.out.println("postbody="+postbody);

        String ret = HttpHandler.httpPostCall("http://localhost:9200/second_product/_search", postbody);
        ESResultRoot retObj = new GsonBuilder().create().fromJson(ret, ESResultRoot.class);
        SourceSet productSet = new SourceSet();
        for(Hit hit:retObj.hits.hits){
            productSet.add(hit._source);
        }
        if(from == 0) {
            //计数
            String esCount = StaticVariable.esCount;
            esCount = esCount.replaceFirst("#query", condition);
            String countRet = HttpHandler.httpPostCall("http://localhost:9200/second_product/_count", esCount);
            ESCount esCt = new GsonBuilder().create().fromJson(countRet, ESCount.class);
            productSet.setMatchCount(esCt.count);
        }
        return new GsonBuilder().create().toJson(productSet);
    }
}
