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
 * 招标中标数据查询控制器
 */
@RestController
public class TenderbidController {

    /**
     * 根据名称查找招标中标公告
     * @param keyword
     * @return
     * @throws IOException
     */
    @RequestMapping("/tenderbid/search_tenderbid_name")
    public String searchTenderbidByName(String keyword, HttpServletResponse response) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        System.out.println("keyword="+keyword);
        String esRequest = StaticVariable.esRequest;
        String condition = "web_content:\\\\\""+keyword+"\\\\\"";
        String postbody = esRequest.replaceFirst("#query",condition);
        System.out.println("postbody="+postbody);
        String ret = HttpHandler.httpPostCall("http://localhost:9200/tenderbid/_search", postbody);
        ESResultRoot retObj = new GsonBuilder().create().fromJson(ret, ESResultRoot.class);
        SourceSet productSet = new SourceSet();
        for(Hit hit:retObj.hits.hits){
            productSet.add(hit._source);
        }
        productSet.setMatchCount(productSet.getDatas().size());
        return new GsonBuilder().create().toJson(productSet);
    }


}
