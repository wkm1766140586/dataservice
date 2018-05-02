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

/**
 * 集采中标数据查询控制器
 */
@RestController
public class AcquisitebidController {

    /**
     * 根据名称来查询集采中标数据
     * @param keyword
     * @return
     * @throws IOException
     */
    @RequestMapping("/acquisitebid/search_acquisitebid_name")
    public String searchAcquisitebidByname(String keyword, HttpServletResponse response) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        String esRequest = StaticVariable.esRequest;
        String condition = "product_name:\\\\\""+keyword+"\\\\\"";
        String postbody = esRequest.replaceFirst("#query",condition);
        System.out.println("postbody="+postbody);
        String ret = HttpHandler.httpPostCall("http://localhost:9200/bid/_search", postbody);
        System.out.println("es return:"+ret);
        ESResultRoot retObj = new GsonBuilder().create().fromJson(ret, ESResultRoot.class);
        System.out.println("retObj:"+retObj.hits.hits.size());
        SourceSet productSet = new SourceSet();
        for(Hit hit:retObj.hits.hits){
            productSet.add(hit._source);
        }
        productSet.setMatchCount(productSet.getDatas().size());
        return new GsonBuilder().create().toJson(productSet);
    }

    /**
     * 根据id查询集采中标数据
     * @param id
     * @return
     * @throws IOException
     */
    @RequestMapping("/acquisitebid/search_acquisitebid_id")
    public String searchAcquisitebidById(String id, HttpServletResponse response) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        String esRequest = StaticVariable.esRequest;
        String condition = "id:\\\\\""+id+"\\\\\"";
        String postbody = esRequest.replaceFirst("#query",condition);
        System.out.println("postbody="+postbody);
        String ret = HttpHandler.httpPostCall("http://localhost:9200/bid/_search", postbody);
        System.out.println("es return:"+ret);
        ESResultRoot retObj = new GsonBuilder().create().fromJson(ret, ESResultRoot.class);
        System.out.println("retObj:"+retObj.hits.hits.size());
        SourceSet productSet = new SourceSet();
        for(Hit hit:retObj.hits.hits){
            productSet.add(hit._source);
        }
        productSet.setMatchCount(productSet.getDatas().size());
        return new GsonBuilder().create().toJson(productSet);
    }

    /**
     * 根据公司名称来查询集采中标数据
     * @param keyword
     * @return
     * @throws IOException
     */
    @RequestMapping("/acquisitebid/search_acquisitebid_companyname")
    public String searchAcquisitebidByCompanyname(String keyword, HttpServletResponse response) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        String esRequest = StaticVariable.esRequest;
        String condition = "production_company:\\\\\""+keyword+"\\\\\"";
        String postbody = esRequest.replaceFirst("#query",condition);
        System.out.println("postbody="+postbody);
        String ret = HttpHandler.httpPostCall("http://localhost:9200/bid/_search", postbody);
        System.out.println("es return:"+ret);
        ESResultRoot retObj = new GsonBuilder().create().fromJson(ret, ESResultRoot.class);
        System.out.println("retObj:"+retObj.hits.hits.size());
        SourceSet productSet = new SourceSet();
        for(Hit hit:retObj.hits.hits){
            productSet.add(hit._source);
        }
        productSet.setMatchCount(productSet.getDatas().size());
        return new GsonBuilder().create().toJson(productSet);
    }
}
