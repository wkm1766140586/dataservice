package com.abc.newsserversec.controller.info;

import com.abc.newsserversec.common.StaticVariable;
import com.abc.newsserversec.model.info.ESResultRoot;
import com.abc.newsserversec.model.info.Hit;
import com.abc.newsserversec.common.HttpHandler;
import com.abc.newsserversec.model.info.SourceSet;
import com.google.gson.GsonBuilder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * 企业数据查询控制器
 */
@RestController
public class CompanyController {

    /**
     * 根据企业名称查找企业信息
     * @param
     * @return
     * @throws IOException
     */
    @RequestMapping("/company/search_company_name")
    public String searchCompanyByName(String keyword, HttpServletResponse response) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        System.out.println("keyword="+keyword);
        String esRequest = StaticVariable.esRequest;
        String condition = "company_name:\\\\\""+keyword+"\\\\\"";
        String postbody = esRequest.replaceFirst("#query",condition);
        System.out.println("postbody="+postbody);
        String ret = HttpHandler.httpPostCall("http://localhost:9200/second_company/_search", postbody);
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
     * 根据id查找企业
     * @param id
     * @return
     * @throws IOException
     */
    @RequestMapping("/company/search_company_id")
    public String searchCompanyById(String id, HttpServletResponse response) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        System.out.println("id="+id);
        String esRequest = StaticVariable.esRequest;
        String condition = "id:\\\\\""+id+"\\\\\"";
        String postbody = esRequest.replaceFirst("#query",condition);
        System.out.println("postbody="+postbody);
        String ret = HttpHandler.httpPostCall("http://localhost:9200/second_company/_search", postbody);
        System.out.println("es return:"+ret);
        ESResultRoot retObj = new GsonBuilder().create().fromJson(ret, ESResultRoot.class);
        System.out.println("retObj:"+retObj.hits.hits.size());
        SourceSet productSet = new SourceSet();
        productSet.setMatchCount(retObj.hits.hits.size());
        for(Hit hit:retObj.hits.hits){
            Object source = hit._source;
            productSet.add(source);
        }
        return new GsonBuilder().create().toJson(productSet);
    }

    /**
     * 根据企业名称查找同名企业
     * @param keyword
     * @return
     * @throws IOException
     */
    @RequestMapping("/company/search_company_same_name")
    public String searchCompanyBySameName(String keyword, HttpServletResponse response) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        System.out.println("keyword="+keyword);
        String esRequest = StaticVariable.esRequest;
        String condition = "company_name:\\\\\""+keyword+"\\\\\"";
        String postbody = esRequest.replaceFirst("#query",condition);
        System.out.println("postbody="+postbody);
        String ret = HttpHandler.httpPostCall("http://localhost:9200/second_company/_search", postbody);
        System.out.println("es return:"+ret);
        ESResultRoot retObj = new GsonBuilder().create().fromJson(ret, ESResultRoot.class);
        System.out.println("retObj:"+retObj.hits.hits.size());
        SourceSet productSet = new SourceSet();
        for(Hit hit:retObj.hits.hits){
            Object source = hit._source;
            if(((Map) source).get("company_name").toString().equals(keyword)) {
                productSet.add(source);
            }
        }
        productSet.setMatchCount(productSet.getDatas().size());
        return new GsonBuilder().create().toJson(productSet);
    }
}
