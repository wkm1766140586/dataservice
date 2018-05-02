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
 * 医院数据查询控制器
 */
@RestController
public class HospitalController {

    /**
     * 根据医院名称查找医院信息
     * @param keyword
     * @return
     * @throws IOException
     */
    @RequestMapping("/hospital/search_hospital_name")
    public String searchHospitalByName(String keyword, HttpServletResponse response) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        System.out.println("keyword="+keyword);
        String esRequest = StaticVariable.esRequest;
        String condition = "hospital_name:\\\\\""+keyword+"\\\\\"";
        String postbody = esRequest.replaceFirst("#query",condition);
        System.out.println("postbody="+postbody);
        String ret = HttpHandler.httpPostCall("http://localhost:9200/new_hospital/_search", postbody);
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
     * 根据id查找医院信息
     * @param id
     * @return
     * @throws IOException
     */
    @RequestMapping("/hospital/search_hospital_id")
    public String searchHospitalById(String id, HttpServletResponse response) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        String esRequest = StaticVariable.esRequest;
        String condition = "id:\\\\\""+id+"\\\\\"";
        String postbody = esRequest.replaceFirst("#query",condition);
        System.out.println("postbody="+postbody);
        String ret = HttpHandler.httpPostCall("http://localhost:9200/new_hospital/_search", postbody);
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
}
