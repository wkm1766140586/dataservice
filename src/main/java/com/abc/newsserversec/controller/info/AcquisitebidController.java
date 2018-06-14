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
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 集采中标数据查询控制器
 */
@RestController
public class AcquisitebidController {

    /**
     * 根据条件来筛选集采中标数据
     * @param
     * @return
     * @throws IOException
     */
    @RequestMapping("/method/search_acquisitebid_filter_condition")
    public String searchAcquisitebidFilterCondition(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");

        String keyword = request.getParameter("keyword");
        String num = request.getParameter("num");
        if(num == null || keyword == null) return "fail";
        if(num.equals("") || keyword.equals("")) return "fail";

        String declare_company = request.getParameter("declare_company");
        String start_time = request.getParameter("start_time");
        String end_time = request.getParameter("end_time");
        String province = request.getParameter("province");
        String product_name = request.getParameter("product_name");
        String company_name = request.getParameter("company_name");
        String size = request.getParameter("size");

        if(declare_company == null) declare_company = "";
        if(start_time == null) start_time = "";
        if(end_time == null) end_time = "";
        if(province == null) province = "";
        if(product_name == null) product_name = "";
        if(company_name == null) company_name = "";
        int from = Integer.valueOf(num);
        if(size != null) { from = from * Integer.valueOf(size); }
        else{ from = from * 10; }

        String esRequest = StaticVariable.esRequest;
        ArrayList<Object> aggList = new ArrayList<>();
        String condition = "";
        String postbody = "";
        String filter = "";

        if(!keyword.equals("")) condition = "product_name:\\\\\""+keyword+"\\\\\"";
        if(!declare_company.equals("")) condition += " AND declare_company:\\\\\""+ declare_company + "\\\\\"";
        if(!province.equals("")) condition += " AND province:\\\\\""+ province + "\\\\\"";
        if(!product_name.equals("")) condition += " AND product_name_agg:\\\\\"" + product_name + "\\\\\"";
        if(!company_name.equals("") && !company_name.equals("yes")) condition = condition+ " AND company_name_agg:\\\\\""+company_name+"\\\\\"";

        if(!start_time.equals("") && !end_time.equals("")) filter = "{\"range\":{\"publish_date\":{\"gte\":\""+start_time+"\",\"lte\":\""+end_time+"\"}}}";
        else if(!start_time.equals("")) filter = "{\"range\":{\"publish_date\":{\"gte\":\""+start_time+"\"}}}";
        else if(!end_time.equals("")) filter = "{\"range\":{\"publish_date\":{\"lte\":\""+end_time+"\"}}}";

        esRequest = esRequest.replaceFirst("\"#from\"",String.valueOf(from));
        if(size == null){ esRequest = esRequest.replaceFirst("\"#size\"","10"); }
        else{ esRequest = esRequest.replaceFirst("\"#size\"",size); }
        esRequest = esRequest.replaceFirst("#includes","*");
        esRequest = esRequest.replaceFirst("\"#excludes\"",StaticVariable.ExcludeFields);
        esRequest = esRequest.replaceFirst("\"#filter\"",filter);
        esRequest = esRequest.replaceFirst("#query",condition);

        if(company_name.equals("")){
            postbody = esRequest.replaceFirst("\"#aggs\"",StaticVariable.acquisitebidAggsProductName);
        }
        if(!product_name.equals("") && !company_name.equals("")) {
            postbody = esRequest.replaceFirst("\"#aggs\"", StaticVariable.acquisitebidAggsCompanyName);
        }
        System.out.println("postbody="+postbody);

        String ret = HttpHandler.httpPostCall("http://localhost:9200/acquisitebid/_search", postbody);
        ESResultRoot retObj = new GsonBuilder().create().fromJson(ret, ESResultRoot.class);
        SourceSet productSet = new SourceSet();
        for(Hit hit:retObj.hits.hits){
            Map map = (Map)hit._source;
            map.put("_id",hit._id);
            productSet.add(map);
        }
        if(from == 0) {
            //计数
            String esCount = StaticVariable.esCount;
            esCount = esCount.replaceFirst("#query", condition);
            esCount = esCount.replaceFirst("\"#filter\"", filter);
            String countRet = HttpHandler.httpPostCall("http://localhost:9200/acquisitebid/_count", esCount);
            ESCount esCt = new GsonBuilder().create().fromJson(countRet, ESCount.class);
            productSet.setMatchCount(esCt.count);
            if(!product_name.equals("")) {
                //图表数据
//                List<Object> provinceList = new ArrayList<>();
//                List<Object> priceList = new ArrayList<>();
//
//                String acquisitebidChart = StaticVariable.acquisitebidChart;
//                acquisitebidChart = acquisitebidChart.replaceFirst("#query", condition);
//                acquisitebidChart = acquisitebidChart.replaceFirst("\"#filter\"", filter);
//                String retChart = HttpHandler.httpPostCall("http://localhost:9200/acquisitebid/_search", acquisitebidChart);
//                ESResultRoot retObjChart = new GsonBuilder().create().fromJson(retChart, ESResultRoot.class);
//
//                ArrayList<Map> provinceBuckets = (ArrayList<Map>) ((Map) retObjChart.aggregations.get("provinces")).get("buckets");
//                for (Map map : provinceBuckets) {
//                    String province_name = (String) map.get("key");
//                    int province_count = (new Double((Double) map.get("doc_count"))).intValue();
//                    Map<String,Object> temp = new HashMap<>();
//                    temp.put("province_name",province_name);
//                    temp.put("province_count",province_count);
//                    provinceList.add(temp);
//                }
//
//                ArrayList<Map> priceBuckets = (ArrayList<Map>) ((Map) retObjChart.aggregations.get("prices")).get("buckets");
//                for (Map map : priceBuckets) {
//                    String date = (String) map.get("key");
//                    Double max_price = (Double) ((Map) map.get("max_price")).get("value");
//                    Double min_price = (Double) ((Map) map.get("min_price")).get("value");
//                    Map<String,Object> temp = new HashMap<>();
//                    temp.put("date",date);
//                    temp.put("max_price",max_price);
//                    temp.put("min_price",min_price);
//                    priceList.add(temp);
//                }
//                productSet.setProvinceList(provinceList);
//                productSet.setPriceList(priceList);
            }

            //聚合
            ArrayList<Map> productBuckets = (ArrayList<Map>) ((Map) retObj.aggregations.get("tags")).get("buckets");
            DecimalFormat df = new DecimalFormat("#.000");
            if(company_name.equals("")) {
                for (Map map : productBuckets) {
                    Map<String, Object> aggMap = new HashMap<>();
                    String name = (String) map.get("key");
                    int product_count = (new Double((Double) map.get("doc_count"))).intValue();

                    Double max_price = (Double) ((Map) map.get("max_price")).get("value");
                    Double min_price = (Double) ((Map) map.get("min_price")).get("value");
                    Double avg_price = (Double) ((Map) map.get("avg_price")).get("value");

                    Map company_name_map = (Map) map.get("company_name");
                    ArrayList company_name_list = (ArrayList) company_name_map.get("buckets");
                    int company_count = company_name_list.size();

                    aggMap.put("product_name", name);
                    aggMap.put("product_count", product_count);
                    aggMap.put("company_count", company_count);
                    aggMap.put("max_price", max_price);
                    aggMap.put("min_price", min_price);
                    aggMap.put("avg_price", df.format(avg_price));

                    aggList.add(aggMap);
                }
            }
            if(!product_name.equals("") && !company_name.equals("")) {
                for (Map map : productBuckets) {
                    Map<String, Object> aggMap = new HashMap<>();
                    String company_agg_name = (String) map.get("key");
                    int product_count = (new Double((Double) map.get("doc_count"))).intValue();

                    Double max_price = (Double) ((Map) map.get("max_price")).get("value");
                    Double min_price = (Double) ((Map) map.get("min_price")).get("value");
                    Double avg_price = (Double) ((Map) map.get("avg_price")).get("value");

                    aggMap.put("company_name", company_agg_name);
                    aggMap.put("product_count", product_count);
                    aggMap.put("max_price", max_price);
                    aggMap.put("min_price", min_price);
                    aggMap.put("avg_price", df.format(avg_price));
                    aggList.add(aggMap);
                }
            }
            productSet.setAggList(aggList);
        }
        return new GsonBuilder().create().toJson(productSet);
    }

    /**
     * 根据id查询集采中标数据
     * @param
     * @return
     * @throws IOException
     */
    @RequestMapping("/method/search_acquisitebid_id")
    public String searchAcquisitebidById(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");

        String id = request.getParameter("id");
        if(id == null) return "fail";
        if(id.equals("")) return "fail";

        String esRequest = StaticVariable.esRequest;
        String condition = "id:\\\\\""+id+"\\\\\"";
        esRequest = esRequest.replaceFirst("\"#from\"",String.valueOf(0));
        esRequest = esRequest.replaceFirst("\"#size\"","10");
        esRequest = esRequest.replaceFirst("\"#includes\"","");
        esRequest = esRequest.replaceFirst("\"#excludes\"",StaticVariable.ExcludeFields);
        esRequest = esRequest.replaceFirst("\"#filter\"","");
        String postbody = esRequest.replaceFirst("#query",condition);
        postbody = postbody.replaceFirst("\"#aggs\"","{}");
        System.out.println("postbody="+postbody);

        String ret = HttpHandler.httpPostCall("http://localhost:9200/acquisitebid/_search", postbody);
        ESResultRoot retObj = new GsonBuilder().create().fromJson(ret, ESResultRoot.class);
        SourceSet productSet = new SourceSet();
        for(Hit hit:retObj.hits.hits){
            Map map = (Map)hit._source;
            map.put("_id",hit._id);
            productSet.add(map);
        }
        return new GsonBuilder().create().toJson(productSet);
    }

}
