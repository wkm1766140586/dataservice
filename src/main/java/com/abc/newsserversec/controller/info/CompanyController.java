package com.abc.newsserversec.controller.info;

import com.abc.newsserversec.common.StaticVariable;
import com.abc.newsserversec.model.company.CompanyInfo;
import com.abc.newsserversec.model.info.ESCount;
import com.abc.newsserversec.model.info.ESResultRoot;
import com.abc.newsserversec.model.info.Hit;
import com.abc.newsserversec.common.HttpHandler;
import com.abc.newsserversec.model.info.SourceSet;
import com.abc.newsserversec.service.company.CompanyInfoService;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 企业数据查询控制器
 */
@RestController
public class CompanyController {

    @Autowired
    private CompanyInfoService companyInfoService;

    /**
     * 根据企业名称获取企业列表信息
     */
    @RequestMapping("/method/search_company_name")
    public String searchCompanyByName(HttpServletRequest request, HttpServletResponse response){
        response.setHeader("Access-Control-Allow-Origin", "*");

        String keyword = request.getParameter("keyword");
        String num_string = request.getParameter("num");
        if(keyword == null || num_string == null) return "fail";
        if(keyword.equals("") || num_string.equals("")) return "fail";

        Map<String,Object> dataMap = new HashMap<>();
        Map<String,Object> map = new HashMap<>();
        int num = Integer.valueOf(num_string)*10;

        keyword = "%"+keyword+"%";
        map.put("company_name",keyword);
        map.put("num",num);
        if(num == 0) {
            int count = companyInfoService.selectCompanyInfoCountByCondition(map);
            dataMap.put("matchCount",count);
        }
        ArrayList<CompanyInfo> companyInfos = companyInfoService.selectCompanyInfoByCondition(map);
        dataMap.put("datas",companyInfos);

        return new GsonBuilder().create().toJson(dataMap);
    }

    /**
     * 根据条件筛选企业列表信息
     */
    @RequestMapping("/method/search_company_filter_condition")
    public String searchCompanyFilterCondition(HttpServletRequest request, HttpServletResponse response){
        response.setHeader("Access-Control-Allow-Origin", "*");

        String keyword = request.getParameter("keyword");
        String num_string = request.getParameter("num");

        if(keyword == null || num_string == null) return "fail";
        if(keyword.equals("") || num_string.equals("")) return "fail";

        Map<String,Object> dataMap = new HashMap<>();
        Map<String,Object> map = new HashMap<>();
        int num = Integer.valueOf(num_string)*10;

        String production_type = request.getParameter("production_type");
        String manage_type = request.getParameter("manage_type");
        String web_type = request.getParameter("web_type");

        keyword = "%"+keyword+"%";
        if(production_type != null){ production_type = "%"+production_type+"%"; }
        if(manage_type != null){ manage_type = "%"+manage_type+"%"; }
        if(web_type != null){ web_type = "%"+web_type+"%"; }

        map.put("company_name",keyword);
        map.put("num",num);
        map.put("production_type",production_type);
        map.put("manage_type",manage_type);
        map.put("web_type",web_type);

        if(num == 0) {
            int count = companyInfoService.selectCompanyInfoCountByCondition(map);
            dataMap.put("matchCount",count);
        }
        ArrayList<CompanyInfo> companyInfos = companyInfoService.selectCompanyInfoByCondition(map);
        dataMap.put("datas",companyInfos);

        return new GsonBuilder().create().toJson(dataMap);
    }

    /**
     * 根据具体企业名称查询该企业的证书
     * @param
     * @return
     * @throws IOException
     */
    @RequestMapping("/method/search_company_specific_name")
    public String searchCompanyBySpecificName(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");

        String keyword = request.getParameter("keyword");
        if(keyword == null) return "fail";
        if(keyword.equals("")) return "fail";

        String esRequest = StaticVariable.esRequest;
        SourceSet productSet = new SourceSet();
        String condition = "company_name_agg:\\\\\""+keyword+"\\\\\"";

        esRequest = esRequest.replaceFirst("\"#from\"",String.valueOf(0));
        esRequest = esRequest.replaceFirst("\"#size\"","10");
        esRequest = esRequest.replaceFirst("#includes","*");
        esRequest = esRequest.replaceFirst("\"#excludes\"",StaticVariable.ExcludeFields+","+StaticVariable.searchProAndComExcludeFields+","+StaticVariable.searchCompanyExcludeFields);
        esRequest = esRequest.replaceFirst("\"#filter\"","");
        esRequest = esRequest.replaceFirst("\"#aggs\"","{}");
        String postbody = esRequest.replaceFirst("#query",condition);
        System.out.println("postbody="+postbody);

        String ret = HttpHandler.httpPostCall("http://localhost:9200/second_company/_search", postbody);
        ESResultRoot retObj = new GsonBuilder().create().fromJson(ret, ESResultRoot.class);
        for(Hit hit:retObj.hits.hits){
            Map map = (Map)hit._source;
            map.put("_id",hit._id);
            productSet.add(map);
        }
        return new GsonBuilder().create().toJson(productSet);
    }

    /**
     * 根据企业名称查询该企业的证书
     * @param
     * @return
     * @throws IOException
     */
    @RequestMapping("/method/search_company_like_name")
    public String searchCompanyByLikeName(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");

        String keyword = request.getParameter("keyword");
        String num = request.getParameter("num");
        if(keyword == null) return "fail";
        if(keyword.equals("")) return "fail";

        String esRequest = StaticVariable.esRequest;
        SourceSet productSet = new SourceSet();
        String condition = "company_name:\\\\\""+keyword+"\\\\\"";
        int from = Integer.valueOf(num);

        esRequest = esRequest.replaceFirst("\"#from\"",String.valueOf(from));
        esRequest = esRequest.replaceFirst("\"#size\"","10");
        esRequest = esRequest.replaceFirst("\"#includes\"",StaticVariable.searchCompanyIncludeFields);
        esRequest = esRequest.replaceFirst("\"#excludes\"","");
        esRequest = esRequest.replaceFirst("\"#filter\"","");
        esRequest = esRequest.replaceFirst("\"#aggs\"","{}");
        String postbody = esRequest.replaceFirst("#query",condition);
        System.out.println("postbody="+postbody);

        String ret = HttpHandler.httpPostCall("http://localhost:9200/second_company/_search", postbody);
        ESResultRoot retObj = new GsonBuilder().create().fromJson(ret, ESResultRoot.class);
        for(Hit hit:retObj.hits.hits){
            Map map = (Map)hit._source;
            map.put("_id",hit._id);
            productSet.add(map);
        }
        if(from == 0) {
            //计数
            String esCount = StaticVariable.esCount;
            esCount = esCount.replaceFirst("#query", condition);
            esCount = esCount.replaceFirst("\"#filter\"","");
            String countRet = HttpHandler.httpPostCall("http://localhost:9200/second_company/_count", esCount);
            ESCount esCt = new GsonBuilder().create().fromJson(countRet, ESCount.class);
            productSet.setMatchCount(esCt.count);
        }
        return new GsonBuilder().create().toJson(productSet);
    }

    /**
     * 根据证书id查询证书
     * @param
     * @return
     * @throws IOException
     */
    @RequestMapping("/method/search_company_id")
    public String searchCompanyById(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");

        String id = request.getParameter("id");
        if(id == null) return "fail";
        if(id.equals("")) return "fail";

        String esRequest = StaticVariable.esRequest;
        SourceSet productSet = new SourceSet();
        String condition = "id:\\\\\""+id+"\\\\\"";

        esRequest = esRequest.replaceFirst("\"#from\"",String.valueOf(0));
        esRequest = esRequest.replaceFirst("\"#size\"","10");
        esRequest = esRequest.replaceFirst("#includes","*");
        esRequest = esRequest.replaceFirst("\"#excludes\"",StaticVariable.ExcludeFields+","+StaticVariable.searchProAndComExcludeFields);
        esRequest = esRequest.replaceFirst("\"#filter\"","");
        esRequest = esRequest.replaceFirst("\"#aggs\"","{}");
        String postbody = esRequest.replaceFirst("#query",condition);
        System.out.println("postbody="+postbody);

        String ret = HttpHandler.httpPostCall("http://localhost:9200/second_company/_search", postbody);
        ESResultRoot retObj = new GsonBuilder().create().fromJson(ret, ESResultRoot.class);
        for(Hit hit:retObj.hits.hits){
            Map map = (Map)hit._source;
            map.put("_id",hit._id);
            productSet.add(map);
        }
        return new GsonBuilder().create().toJson(productSet);
    }

}
