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
    @RequestMapping("/product/search_product_name")
    public String searchProductByName(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");

        String num = request.getParameter("num");
        String keyword = request.getParameter("keyword");
        if(num == null || keyword == null) return "fail";
        if(num.equals("") || keyword.equals("")) return "fail";

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
        esRequest = esRequest.replaceFirst("\"#fields\"",StaticVariable.searchProductFields);
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
    @RequestMapping("/product/search_product_filter_condition")
    public String searchProductFilterByCondition(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");

        String keyword = request.getParameter("keyword");
        String src_loc = request.getParameter("src_loc");
        String product_state = request.getParameter("product_state");
        String main_class = request.getParameter("main_class");
        String num = request.getParameter("num");
        String product_name = request.getParameter("product_name");
        String company_name = request.getParameter("company_name");
        String class_code = request.getParameter("class_code");

        if(keyword == null || num == null) return "fail";
        if(keyword.equals("") || num.equals("")) return "fail";
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
        esRequest = esRequest.replaceFirst("\"#fields\"",StaticVariable.searchProductFields);
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
        //return ret;
        return new GsonBuilder().create().toJson(productSet);
    }

    /**
     * 根据id来查询产品信息
     * @param id
     * @return
     * @throws IOException
     */
    @RequestMapping("/product/search_product_id")
    public String searchProductById(String id, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");

        System.out.println("id="+id);
        String esRequest = StaticVariable.esRequest;
        esRequest = esRequest.replaceFirst("\"#from\"",String.valueOf(0));
        esRequest = esRequest.replaceFirst("#fields","*");
        esRequest = esRequest.replaceFirst("\"#aggs\"","{}");
        String condition = "id:\\\\\""+id+"\\\\\"";
        String postbody = esRequest.replaceFirst("#query",condition);
        System.out.println("postbody="+postbody);
        String ret = HttpHandler.httpPostCall("http://localhost:9200/second_product/_search", postbody);
        System.out.println("es return:"+ret);
        ESResultRoot retObj = new GsonBuilder().create().fromJson(ret, ESResultRoot.class);
        System.out.println("retObj:"+retObj.hits.hits.size());
        SourceSet productSet = new SourceSet();
        productSet.setMatchCount(retObj.hits.hits.size());
        for(Hit hit:retObj.hits.hits){
            productSet.add(hit._source);
        }
        return new GsonBuilder().create().toJson(productSet);
    }


    /**
     * 根据企业名称查出该企业的产品
     * @param keyword
     * @return
     * @throws IOException
     */
    @RequestMapping("/product/search_product_company_name")
    public String searchProductByCompanyName(String keyword, HttpServletResponse response) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        System.out.println("keyword="+keyword);
        String esRequest = StaticVariable.esRequest;
        String condition = "(maker_name_ch:\\\\\""+keyword+"\\\\\") OR (agent:\\\\\""+keyword+"\\\\\")";
        String postbody = esRequest.replaceFirst("#query",condition);
        System.out.println("postbody="+postbody);
        String ret = HttpHandler.httpPostCall("http://localhost:9200/second_product/_search", postbody);
        ESResultRoot retObj = new GsonBuilder().create().fromJson(ret, ESResultRoot.class);
        SourceSet productSet = new SourceSet();
        for(Hit hit:retObj.hits.hits){
            Object source = hit._source;
            if(((Map) source).get("maker_name_ch").toString().equals(keyword) || ((Map) source).get("agent").toString().equals(keyword)) {
                productSet.add(source);
            }
        }
        productSet.setMatchCount(productSet.getDatas().size());
        return new GsonBuilder().create().toJson(productSet);
    }

    /**
     * 根据产品名进行聚合
     * @param list
     * @return
     */
    private Map assistAgg(ArrayList<Object> list){
        Map<String,Object> map = new HashMap<>();
        map.put("product_count",list.size());
        boolean f = true;
        Map<String,Object> company = new HashMap<>();
        Map<String,Object> class_code = new HashMap<>();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date new_date = new Date();
        Date old_date = new Date();

        for(Object object:list){
            Map t = (Map)object;
            if(f) {
                map.put("product_name", t.get("product_name_ch"));
                String vacancy_mark = String.valueOf(t.get("vacancy_mark"));
                String approval_complete_mark = String.valueOf(t.get("approval_complete_mark"));
                if(vacancy_mark.equals("0") || vacancy_mark.equals("2")){
                    if(approval_complete_mark.equals("0")){
                        try {
                            new_date = df.parse(String.valueOf(t.get("approval_date")));
                            old_date = df.parse(String.valueOf(t.get("approval_date")));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
                f = false;
            }

            String company_name = String.valueOf(t.get("maker_name_ch"));
            if(company_name.equals("")) company_name = String.valueOf(t.get("agent"));
            if(company.get(company_name) == null && !company_name.equals("")){ company.put(company_name,1); }

            String code = String.valueOf(t.get("class_code"));
            if(class_code.get(code) == null && !code.equals("")){ class_code.put(code,1); }

            if(f == false){
                String vacancy_mark = String.valueOf(t.get("vacancy_mark"));
                String approval_complete_mark = String.valueOf(t.get("approval_complete_mark"));
                if(vacancy_mark.equals("0") || vacancy_mark.equals("2")){
                    if(approval_complete_mark.equals("0")){
                        try {
                            Date dt = df.parse(String.valueOf(t.get("approval_date")));
                            if(dt.getTime() > new_date.getTime()) new_date = dt;
                            if(dt.getTime() < old_date.getTime()) old_date = dt;
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        map.put("company_count",company.size());
        map.put("code_count",class_code.size());
        map.put("new_date",df.format(new_date));
        map.put("old_date",df.format(old_date));
        return map;
    }

    /**
     * 根据企业名进行聚合
     * @param list
     * @return
     */
    private Map assistAggByCompanyName(ArrayList<Object> list){
        Map<String,Object> map = new HashMap<>();
        map.put("product_count",list.size());
        boolean f = true;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date new_date = new Date();
        Date old_date = new Date();

        for(Object object:list){
            Map t = (Map)object;
            if(f) {
                String company_name = String.valueOf(t.get("maker_name_ch"));
                if(company_name.equals("")) company_name = String.valueOf(t.get("agent"));
                map.put("company_name", company_name);
                String vacancy_mark = String.valueOf(t.get("vacancy_mark"));
                String approval_complete_mark = String.valueOf(t.get("approval_complete_mark"));
                if(vacancy_mark.equals("0") || vacancy_mark.equals("2")){
                    if(approval_complete_mark.equals("0")){
                        try {
                            new_date = df.parse(String.valueOf(t.get("approval_date")));
                            old_date = df.parse(String.valueOf(t.get("approval_date")));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
                f = false;
            }

            if(f == false){
                String vacancy_mark = String.valueOf(t.get("vacancy_mark"));
                String approval_complete_mark = String.valueOf(t.get("approval_complete_mark"));
                if(vacancy_mark.equals("0") || vacancy_mark.equals("2")){
                    if(approval_complete_mark.equals("0")){
                        try {
                            Date dt = df.parse(String.valueOf(t.get("approval_date")));
                            if(dt.getTime() > new_date.getTime()) new_date = dt;
                            if(dt.getTime() < old_date.getTime()) old_date = dt;
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        map.put("new_date",df.format(new_date));
        map.put("old_date",df.format(old_date));
        return map;
    }

    /**
     * 根据分类目录进行聚合
     * @param list
     * @return
     */
    private Map assistAggByClassCode(ArrayList<Object> list){
        Map<String,Object> map = new HashMap<>();
        map.put("product_count",list.size());
        boolean f = true;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date new_date = new Date();
        Date old_date = new Date();

        for(Object object:list){
            Map t = (Map)object;
            if(f) {
                String code_name = classCodeChange(String.valueOf(t.get("class_code")));
                map.put("code_name", code_name);
                map.put("class_code",t.get("class_code"));
                String vacancy_mark = String.valueOf(t.get("vacancy_mark"));
                String approval_complete_mark = String.valueOf(t.get("approval_complete_mark"));
                if(vacancy_mark.equals("0") || vacancy_mark.equals("2")){
                    if(approval_complete_mark.equals("0")){
                        try {
                            new_date = df.parse(String.valueOf(t.get("approval_date")));
                            old_date = df.parse(String.valueOf(t.get("approval_date")));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
                f = false;
            }

            if(f == false){
                String vacancy_mark = String.valueOf(t.get("vacancy_mark"));
                String approval_complete_mark = String.valueOf(t.get("approval_complete_mark"));
                if(vacancy_mark.equals("0") || vacancy_mark.equals("2")){
                    if(approval_complete_mark.equals("0")){
                        try {
                            Date dt = df.parse(String.valueOf(t.get("approval_date")));
                            if(dt.getTime() > new_date.getTime()) new_date = dt;
                            if(dt.getTime() < old_date.getTime()) old_date = dt;
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        map.put("new_date",df.format(new_date));
        map.put("old_date",df.format(old_date));
        return map;
    }

    private String classCodeChange(String name){
        if(name.equals("01")){return "基础外科手术器械";}
        if(name.equals("02")){return "显微外科手术器械";}
        if(name.equals("03")){return "神经外科手术器械";}
        if(name.equals("04")){return "眼科手术器械";}
        if(name.equals("05")){return "耳鼻喉科手术器械";}
        if(name.equals("06")){return "口腔科手术器械";}
        if(name.equals("07")){return "胸腔心血管外科手术器械";}
        if(name.equals("08")){return "腹部外科手术器械";}
        if(name.equals("09")){return "泌尿肛肠外科手术器械";}
        if(name.equals("10")){return "矫形外科（骨科）手术器械";}
        if(name.equals("12")){return "妇产科用手术器械";}
        if(name.equals("13")){return "计划生育手术器械";}
        if(name.equals("15")){return "注射穿刺器械";}
        if(name.equals("16")){return "烧伤（整形）科手术器械";}
        if(name.equals("20")){return "普通诊察器械";}
        if(name.equals("21")){return "医用电子仪器设备";}
        if(name.equals("22")){return "医用光学器具、仪器及内窥镜设备";}
        if(name.equals("23")){return "医用超声仪器及有关设备";}
        if(name.equals("24")){return "医用激光仪器设备";}
        if(name.equals("25")){return "医用高频仪器设备";}
        if(name.equals("26")){return "物理治疗及康复设备";}
        if(name.equals("27")){return "中医器械";}
        if(name.equals("28")){return "医用磁共振设备";}
        if(name.equals("30")){return "医用X射线设备";}
        if(name.equals("31")){return "医用X射线附属设备及部件";}
        if(name.equals("32")){return "医用高能射线设备";}
        if(name.equals("33")){return "医用核素设备";}
        if(name.equals("34")){return "医用射线防护用品、装置";}
        if(name.equals("40")){return "临床检验分析仪器";}
        if(name.equals("41")){return "医用化验和基础设备器具";}
        if(name.equals("45")){return "体外循环及血液处理设备";}
        if(name.equals("46")){return "植入材料和人工器官";}
        if(name.equals("54")){return "手术室、急救室、诊疗室设备及器具";}
        if(name.equals("55")){return "口腔科设备及器具";}
        if(name.equals("56")){return "病房护理设备及器具";}
        if(name.equals("57")){return "消毒和灭菌设备及器具";}
        if(name.equals("58")){return "医用冷疗、低温、冷藏设备及器具";}
        if(name.equals("63")){return "口腔科材料";}
        if(name.equals("64")){return "医用卫生材料及敷料";}
        if(name.equals("65")){return "医用缝合材料及粘合剂";}
        if(name.equals("66")){return "医用高分子材料及制品";}
        if(name.equals("70")){return "医用软件";}
        if(name.equals("77")){return "介入器材";}
        return "未知";
    }
}
