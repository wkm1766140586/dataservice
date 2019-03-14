package com.abc.newsserversec.controller.info;

import com.abc.newsserversec.common.HttpHandler;
import com.abc.newsserversec.model.info.ESCount;
import com.abc.newsserversec.model.info.ESResultRoot;
import com.google.gson.GsonBuilder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/method")
@CrossOrigin
public class StatisticsController {

    /**
     * 年份
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping("/search_product_count")
    public String search_product_count(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String number = request.getParameter("number");//编码
        String[] nums = number.split("-");
        String esRequest = "";
        if(nums.length == 3){
            esRequest = "{\n" +
                    "  \"version\": true,\n" +
                    "  \"size\": 0,\n" +
                    "  \"query\": {\n" +
                    "    \"bool\": {\n" +
                    "      \"must\": [\n" +
                    "        {\n" +
                    "          \"query_string\": {\n" +
                    "            \"analyze_wildcard\": true,\n" +
                    "            \"query\": \"class_code_new:\\\""+nums[0]+"\\\" AND class_code_2:\\\""+nums[1]+"\\\" AND class_code_3:\\\""+nums[2]+"\\\"\"\n" +
                    "          }\n" +
                    "        }\n" +
                    "      ],\n" +
                    "      \"filter\": {\n" +
                    "        \"range\": {\n" +
                    "          \"approval_date\": {\n" +
                    "            \"gte\": \"2014-01-01\",\n" +
                    "            \"lte\": \"2019-12-31\"\n" +
                    "          }\n" +
                    "        }\n" +
                    "      }\n" +
                    "    }\n" +
                    "  },\n" +
                    "  \"aggs\": {\n" +
                    "    \"tags\": {\n" +
                    "      \"terms\": {\n" +
                    "        \"field\": \"zhixie_catalog\"\n" +
                    "      },\n" +
                    "      \"aggs\": {\n" +
                    "        \"date_time\":{\n" +
                    "           \"date_histogram\": {\n" +
                    "              \"field\": \"approval_date\",\n" +
                    "              \"interval\": \"year\",\n" +
                    "              \"format\": \"yyyy\",\n" +
                    "              \"min_doc_count\": 0\n" +
                    "            }\n" +
                    "        }\n" +
                    "      }\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";
        }else if(nums.length == 2){
            esRequest = "{\n" +
                    "  \"version\": true,\n" +
                    "  \"size\": 0,\n" +
                    "  \"query\": {\n" +
                    "    \"bool\": {\n" +
                    "      \"must\": [\n" +
                    "        {\n" +
                    "          \"query_string\": {\n" +
                    "            \"analyze_wildcard\": true,\n" +
                    "            \"query\": \"class_code_new:\\\""+nums[0]+"\\\" AND class_code_2:\\\""+nums[1]+"\\\"\"\n" +
                    "          }\n" +
                    "        }\n" +
                    "      ],\n" +
                    "      \"filter\": {\n" +
                    "        \"range\": {\n" +
                    "          \"approval_date\": {\n" +
                    "            \"gte\": \"2014-01-01\",\n" +
                    "            \"lte\": \"2019-12-31\"\n" +
                    "          }\n" +
                    "        }\n" +
                    "      }\n" +
                    "    }\n" +
                    "  },\n" +
                    "  \"aggs\": {\n" +
                    "    \"tags\": {\n" +
                    "      \"terms\": {\n" +
                    "        \"field\": \"zhixie_catalog\"\n" +
                    "      },\n" +
                    "      \"aggs\": {\n" +
                    "        \"date_time\":{\n" +
                    "           \"date_histogram\": {\n" +
                    "              \"field\": \"approval_date\",\n" +
                    "              \"interval\": \"year\",\n" +
                    "              \"format\": \"yyyy\",\n" +
                    "              \"min_doc_count\": 0\n" +
                    "            }\n" +
                    "        }\n" +
                    "      }\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";
        }
        String ret = HttpHandler.httpPostCall("http://localhost:9200/product/_search", esRequest);
        ESResultRoot retObj = new GsonBuilder().create().fromJson(ret, ESResultRoot.class);
        ArrayList<Map> productBuckets = (ArrayList<Map>) ((Map) retObj.aggregations.get("tags")).get("buckets");
        return new GsonBuilder().create().toJson(productBuckets);
    }

    /**
     * 竞争格局
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping("/search_company_count")
    public String search_company_count(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String number = request.getParameter("number");//编码
        String[] nums = number.split("-");
        String esRequest = "";
        if (nums.length == 3) {
            esRequest = "{\n" +
                    "  \"version\":true,\n" +
                    "  \"size\": 0,\n" +
                    "  \"query\": {\n" +
                    "    \"bool\": {\n" +
                    "      \"must\": [\n" +
                    "        {\n" +
                    "          \"query_string\": {\n" +
                    "            \"analyze_wildcard\": true,\n" +
                    "            \"query\": \"class_code_new:\\\"" + nums[0] + "\\\" AND class_code_2:\\\"" + nums[1] + "\\\" AND class_code_3:\\\"" + nums[2] + "\\\"\"\n" +
                    "          }\n" +
                    "        }\n" +
                    "      ]\n" +
                    "    }\n" +
                    "  },\n" +
                    "  \"aggs\": {\n" +
                    "    \"tags\": {\n" +
                    "      \"terms\": {\n" +
                    "        \"field\": \"zhixie_catalog\"\n" +
                    "      },\n" +
                    "      \"aggs\": {\n" +
                    "        \"company_agg\": {\n" +
                    "          \"terms\": {\n" +
                    "            \"field\": \"company_name_agg\"\n" +
                    "          }\n" +
                    "        }\n" +
                    "      }\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";
        }else if(nums.length == 2){
            esRequest = "{\n" +
                    "  \"version\":true,\n" +
                    "  \"size\": 0,\n" +
                    "  \"query\": {\n" +
                    "    \"bool\": {\n" +
                    "      \"must\": [\n" +
                    "        {\n" +
                    "          \"query_string\": {\n" +
                    "            \"analyze_wildcard\": true,\n" +
                    "            \"query\": \"class_code_new:\\\"" + nums[0] + "\\\" AND class_code_2:\\\"" + nums[1] + "\\\"\"\n" +
                    "          }\n" +
                    "        }\n" +
                    "      ]\n" +
                    "    }\n" +
                    "  },\n" +
                    "  \"aggs\": {\n" +
                    "    \"tags\": {\n" +
                    "      \"terms\": {\n" +
                    "        \"field\": \"zhixie_catalog\"\n" +
                    "      },\n" +
                    "      \"aggs\": {\n" +
                    "        \"company_agg\": {\n" +
                    "          \"terms\": {\n" +
                    "            \"field\": \"company_name_agg\"\n" +
                    "          }\n" +
                    "        }\n" +
                    "      }\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";
        }
        String ret = HttpHandler.httpPostCall("http://localhost:9200/product/_search", esRequest);
        ESResultRoot retObj = new GsonBuilder().create().fromJson(ret, ESResultRoot.class);
        ArrayList<Map> productBuckets = (ArrayList<Map>) ((Map) retObj.aggregations.get("tags")).get("buckets");

        ArrayList<String> productList = new ArrayList<>();
        Set companys = new HashSet();//存放公司名称，去重

        for (Map temp : productBuckets) {
            String name = (String) temp.get("key");//产品名称
            productList.add(name);

            Map main_class = (Map) temp.get("company_agg");
            ArrayList<Map> company_list = (ArrayList<Map>) main_class.get("buckets");
            for (Map companyListMap : company_list) {
                String companyName = (String)companyListMap.get("key");//公司名称
                companys.add(companyName);
            }
        }
        Object[] companyList = companys.toArray();

        ArrayList<List> data = new ArrayList<>();
        for(int i = 0;i < productList.size();i++){//6  产品
            for (int j = 0;j < companys.size();j++){//13  公司
                ArrayList<Integer> value = new ArrayList<>();
                ArrayList<Map> infos = (ArrayList<Map>)((Map)productBuckets.get(i).get("company_agg")).get("buckets");
                boolean flag = true;
                for(int a = 0;a<infos.size();a++){
                    if(infos.get(a).get("key").toString().equals(companyList[j])){
                        flag = false;
                        value.add(j);
                        value.add(i);
                        value.add(new Double((double)infos.get(a).get("doc_count")).intValue());
                        data.add(value);
                        break;

                    }
                }
                if(flag){
                    value.add(j);
                    value.add(i);
                    value.add(0);
                    data.add(value);
                }
                value = null;
            }
        }
        Map<String,Object> map = new HashMap<>();
        map.put("company",companys);
        map.put("product",productList);
        map.put("data",data);
        return new GsonBuilder().create().toJson(map);
    }

    /**
     * 产品概述 智械分类 zhixie_catalog  一类二类main_class 地图
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping("/search_base")
    public String search_base(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String number = request.getParameter("number");//编码
        String classify = request.getParameter("classify");//类别
        String[] nums = number.split("-");
        String esRequest = "";
        if (nums.length == 3) {

            esRequest = "{\n" +
                    "  \"version\":true,\n" +
                    "  \"size\": 0,\n" +
                    "  \"query\": {\n" +
                    "    \"bool\": {\n" +
                    "      \"must\": [\n" +
                    "        {\n" +
                    "          \"query_string\": {\n" +
                    "            \"analyze_wildcard\": true,\n" +
                    "            \"query\": \"class_code_new:\\\""+nums[0]+"\\\" AND class_code_2:\\\""+nums[1]+"\\\" AND class_code_3:\\\""+nums[2]+"\\\"\"\n" +
                    "          }\n" +
                    "        }\n" +
                    "      ]\n" +
                    "    }\n" +
                    "  },\n" +
                    "  \"aggs\": {\n" +
                    "    \"tags\": {\n" +
                    "      \"terms\": {\n" +
                    "        \"field\": \""+classify+"\"\n" +
                    "      }\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";
        }else if(nums.length == 2){
            esRequest = "{\n" +
                    "  \"version\":true,\n" +
                    "  \"size\": 0,\n" +
                    "  \"query\": {\n" +
                    "    \"bool\": {\n" +
                    "      \"must\": [\n" +
                    "        {\n" +
                    "          \"query_string\": {\n" +
                    "            \"analyze_wildcard\": true,\n" +
                    "            \"query\": \"class_code_new:\\\""+nums[0]+"\\\" AND class_code_2:\\\""+nums[1]+"\\\"\"\n" +
                    "          }\n" +
                    "        }\n" +
                    "      ]\n" +
                    "    }\n" +
                    "  },\n" +
                    "  \"aggs\": {\n" +
                    "    \"tags\": {\n" +
                    "      \"terms\": {\n" +
                    "        \"field\": \""+classify+"\"\n" +
                    "      }\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";
        }
        String ret = HttpHandler.httpPostCall("http://localhost:9200/product/_search", esRequest);
        ESResultRoot retObj = new GsonBuilder().create().fromJson(ret, ESResultRoot.class);
        ArrayList<Map> productBuckets = (ArrayList<Map>) ((Map) retObj.aggregations.get("tags")).get("buckets");

        ArrayList<String> productList = new ArrayList<>();
        ArrayList<Map<String,Object>> datas = new ArrayList<>();

        for (Map temp : productBuckets) {
            Map<String,Object> dataMap = new HashMap<>();

            String name = (String) temp.get("key");//产品名称
            productList.add(name);

            dataMap.put("value",temp.get("doc_count"));
            dataMap.put("name",name);

            datas.add(dataMap);
            dataMap = null;
        }
        Map<String,Object> map = new HashMap<>();
        map.put("title",productList);
        map.put("datas",datas);
        return new GsonBuilder().create().toJson(map);
    }

    /**
     * 有源无源
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping("/search_structure")
    public String search_structure(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String number = request.getParameter("number");//编码
        String[] nums = number.split("-");
        String[] params = {"有源","无源","体外诊断试剂"};

        ArrayList<String> productList = new ArrayList<>();
        ArrayList<Map<String,Object>> datas = new ArrayList<>();

            for(int i = 0;i<params.length;i++){
                Map<String,Object> dataMap = new HashMap<>();
                String esRequest = "";
                if (nums.length == 3) {
                   esRequest = "{\n" +
                            "  \"query\": {\n" +
                            "    \"bool\": {\n" +
                            "      \"must\": [\n" +
                            "        {\n" +
                            "          \"query_string\": {\n" +
                            "            \"analyze_wildcard\": true,\n" +
                            "            \"query\": \"class_code_new:\\\"" + nums[0] + "\\\" AND class_code_2:\\\"" + nums[1] + "\\\" AND class_code_3:\\\"" + nums[2] + "\\\" AND architectural_feature:\\\"" + params[i] + "\\\"\"\n" +
                            "          }\n" +
                            "        }\n" +
                            "      ]\n" +
                            "    }\n" +
                            "  }\n" +
                            "}";
                }else if(nums.length == 2){
                    esRequest = "{\n" +
                            "  \"query\": {\n" +
                            "    \"bool\": {\n" +
                            "      \"must\": [\n" +
                            "        {\n" +
                            "          \"query_string\": {\n" +
                            "            \"analyze_wildcard\": true,\n" +
                            "            \"query\": \"class_code_new:\\\"" + nums[0] + "\\\" AND class_code_2:\\\"" + nums[1] + "\\\" AND architectural_feature:\\\"" + params[i] + "\\\"\"\n" +
                            "          }\n" +
                            "        }\n" +
                            "      ]\n" +
                            "    }\n" +
                            "  }\n" +
                            "}";
                }
                String countRet = HttpHandler.httpPostCall("http://localhost:9200/product/_count", esRequest);
                ESCount esCt = new GsonBuilder().create().fromJson(countRet, ESCount.class);

                productList.add(params[i]);
                dataMap.put("name",params[i]);
                dataMap.put("value",esCt.count);

                datas.add(dataMap);
                dataMap = null;
            }
        Map<String,Object> map = new HashMap<>();
        map.put("title",productList);
        map.put("datas",datas);

        return new GsonBuilder().create().toJson(map);
    }

    /**
     * 制造商属地
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping("/search_src_loc")
    public String search_src_loc(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String number = request.getParameter("number");//编码
        String[] nums = number.split("-");
        String[] params = {"0","1","2"};
        String[] params2 = {"国产","进口","港澳台"};

        ArrayList<String> productList = new ArrayList<>();
        ArrayList<Map<String,Object>> datas = new ArrayList<>();
        for(int i = 0;i<params.length;i++){
            Map<String,Object> dataMap = new HashMap<>();
            String esRequest = "";
            if (nums.length == 3) {
                esRequest = "{\n" +
                        "  \"query\": {\n" +
                        "    \"bool\": {\n" +
                        "      \"must\": [\n" +
                        "        {\n" +
                        "          \"query_string\": {\n" +
                        "            \"analyze_wildcard\": true,\n" +
                        "            \"query\": \"class_code_new:\\\"" + nums[0] + "\\\" AND class_code_2:\\\"" + nums[1] + "\\\" AND class_code_3:\\\"" + nums[2] + "\\\" AND src_loc:\\\"" + params[i] + "\\\"\"\n" +
                        "          }\n" +
                        "        }\n" +
                        "      ]\n" +
                        "    }\n" +
                        "  }\n" +
                        "}";
            }else if(nums.length == 2){
                esRequest = "{\n" +
                        "  \"query\": {\n" +
                        "    \"bool\": {\n" +
                        "      \"must\": [\n" +
                        "        {\n" +
                        "          \"query_string\": {\n" +
                        "            \"analyze_wildcard\": true,\n" +
                        "            \"query\": \"class_code_new:\\\"" + nums[0] + "\\\" AND class_code_2:\\\"" + nums[1] + "\\\" AND src_loc:\\\"" + params[i] + "\\\"\"\n" +
                        "          }\n" +
                        "        }\n" +
                        "      ]\n" +
                        "    }\n" +
                        "  }\n" +
                        "}";
            }
            String countRet = HttpHandler.httpPostCall("http://localhost:9200/product/_count", esRequest);
            ESCount esCt = new GsonBuilder().create().fromJson(countRet, ESCount.class);

            productList.add(params2[i]);
            dataMap.put("name",params2[i]);
            dataMap.put("value",esCt.count);

            datas.add(dataMap);
            dataMap = null;

        }
        Map<String,Object> map = new HashMap<>();
        map.put("title",productList);
        map.put("datas",datas);
        return new GsonBuilder().create().toJson(map);

    }

    /**
     * 管理类别
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping("/search_main_class")
    public String search_main_class(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String number = request.getParameter("number");//编码
        String[] nums = number.split("-");
        String[] params = {"1","2","3"};
        String[] params2 = {"I类","II类","III类"};

        ArrayList<String> productList = new ArrayList<>();
        ArrayList<Map<String,Object>> datas = new ArrayList<>();
        for(int i = 0;i<params.length;i++){
            Map<String,Object> dataMap = new HashMap<>();
            String esRequest ="";
            if (nums.length == 3) {
                esRequest = "{\n" +
                        "  \"query\": {\n" +
                        "    \"bool\": {\n" +
                        "      \"must\": [\n" +
                        "        {\n" +
                        "          \"query_string\": {\n" +
                        "            \"analyze_wildcard\": true,\n" +
                        "            \"query\": \"class_code_new:\\\"" + nums[0] + "\\\" AND class_code_2:\\\"" + nums[1] + "\\\" AND class_code_3:\\\"" + nums[2] + "\\\" AND main_class:\\\"" + params[i] + "\\\"\"\n" +
                        "          }\n" +
                        "        }\n" +
                        "      ]\n" +
                        "    }\n" +
                        "  }\n" +
                        "}";
            }else if(nums.length == 2){
                esRequest = "{\n" +
                        "  \"query\": {\n" +
                        "    \"bool\": {\n" +
                        "      \"must\": [\n" +
                        "        {\n" +
                        "          \"query_string\": {\n" +
                        "            \"analyze_wildcard\": true,\n" +
                        "            \"query\": \"class_code_new:\\\"" + nums[0] + "\\\" AND class_code_2:\\\"" + nums[1] + "\\\" AND main_class:\\\"" + params[i] + "\\\"\"\n" +
                        "          }\n" +
                        "        }\n" +
                        "      ]\n" +
                        "    }\n" +
                        "  }\n" +
                        "}";
            }
            String countRet = HttpHandler.httpPostCall("http://localhost:9200/product/_count", esRequest);
            ESCount esCt = new GsonBuilder().create().fromJson(countRet, ESCount.class);

            productList.add(params2[i]);
            dataMap.put("name",params2[i]);
            dataMap.put("value",esCt.count);

            datas.add(dataMap);
            dataMap = null;

        }
        Map<String,Object> map = new HashMap<>();
        map.put("title",productList);
        map.put("datas",datas);
        return new GsonBuilder().create().toJson(map);

    }

}
