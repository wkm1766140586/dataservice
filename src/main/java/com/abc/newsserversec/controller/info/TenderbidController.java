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

/**
 * 招标中标数据查询控制器
 */
@RestController
public class TenderbidController {

    /**
     * 根据名称查找招标中标公告
     * @param
     * @return
     * @throws IOException
     */
    @RequestMapping("/method/search_tenderbid_name")
    public String searchTenderbidByName(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");

        String num = request.getParameter("num");
        String keyword = request.getParameter("keyword");
        if(num == null || keyword == null) return "fail";
        if(num.equals("") || keyword.equals("")) return "fail";

        String size = request.getParameter("size");
        String esRequest = StaticVariable.esRequest;
        SourceSet productSet = new SourceSet();
        int from = Integer.valueOf(num);
        if(size != null) { from = from * Integer.valueOf(size); }
        else{ from = from * 10; }

        String condition = "content:\\\\\""+keyword+"\\\\\" OR title:\\\\\""+keyword+"\\\\\"";
        esRequest = esRequest.replaceFirst("\"#from\"",String.valueOf(from));
        esRequest = esRequest.replaceFirst("approval_date","date");
        if(size == null){ esRequest = esRequest.replaceFirst("\"#size\"","10"); }
        else{ esRequest = esRequest.replaceFirst("\"#size\"",size); }
        esRequest = esRequest.replaceFirst("approval_date","date");
        esRequest = esRequest.replaceFirst("\"#includes\"","");
        esRequest = esRequest.replaceFirst("\"#excludes\"",StaticVariable.searchTenderbidExcludeFields);
        esRequest = esRequest.replaceFirst("\"#filter\"","");
        String postbody = esRequest.replaceFirst("#query",condition);
        postbody = postbody.replaceFirst("\"#aggs\"","{}");
        String ret = HttpHandler.httpPostCall("http://localhost:9200/newtenderbid/_search", postbody);
        ESResultRoot retObj = new GsonBuilder().create().fromJson(ret, ESResultRoot.class);
        for(Hit hit:retObj.hits.hits){
            productSet.add(hit._source);
        }
        if(from == 0) {
            //计数
            String esCount = StaticVariable.esCount;
            esCount = esCount.replaceFirst("#query", condition);
            esCount = esCount.replaceFirst("\"#filter\"","");
            String countRet = HttpHandler.httpPostCall("http://localhost:9200/newtenderbid/_count", esCount);
            ESCount esCt = new GsonBuilder().create().fromJson(countRet, ESCount.class);
            productSet.setMatchCount(esCt.count);
        }
        return new GsonBuilder().create().toJson(productSet);
    }
}
