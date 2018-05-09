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
        String company_name = request.getParameter("company_name");
        String num_string = request.getParameter("num");
        if(company_name == null || num_string == null) return "fail";
        if(company_name.equals("") || num_string.equals("")) return "fail";

        Map<String,Object> dataMap = new HashMap<>();
        Map<String,Object> map = new HashMap<>();
        int num = Integer.valueOf(num_string);
        company_name = "%"+company_name+"%";
        map.put("company_name",company_name);
        map.put("num",10*num);
        int count = 0;
        if(num == 0) {
            count = companyInfoService.selectCompanyInfoCountByCondition(company_name);
        }
        ArrayList<CompanyInfo> companyInfos = companyInfoService.selectCompanyInfoByCondition(map);
        dataMap.put("matchCount",count);
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
        esRequest = esRequest.replaceFirst("#includes","*");
        esRequest = esRequest.replaceFirst("\"#excludes\"",StaticVariable.searchExcludeFields+","+StaticVariable.searchCompanyExcludeFields);
        esRequest = esRequest.replaceFirst("\"#aggs\"","{}");
        String postbody = esRequest.replaceFirst("#query",condition);
        System.out.println("postbody="+postbody);
        String ret = HttpHandler.httpPostCall("http://localhost:9200/second_company/_search", postbody);
        ESResultRoot retObj = new GsonBuilder().create().fromJson(ret, ESResultRoot.class);
        for(Hit hit:retObj.hits.hits){
            productSet.add(hit._source);
        }
        return new GsonBuilder().create().toJson(productSet);
    }


}
