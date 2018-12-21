package com.abc.newsserversec.controller;

import com.abc.newsserversec.common.HttpHandler;
import com.abc.newsserversec.common.StaticVariable;
import com.abc.newsserversec.model.info.ESCount;
import com.abc.newsserversec.model.info.ESResultRoot;
import com.google.gson.GsonBuilder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.Map;

/**
 * 临时
 */
@RestController
public class TempController {

    /**
     *
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping("/method/product_temp")
    public String product_temp(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");

        String esRequest = StaticVariable.esRequest;
        String condition = "";
        String postbody = "";
        String ret = "";
        condition = "main_class:2 AND class_code:66 AND product_ivd:noivd";

        String filter = "{\"range\":{\"approval_date\":{\"gte\":\"2017-01-01\"}}}";
        esRequest = esRequest.replaceFirst("\"#from\"","0");
        esRequest = esRequest.replaceFirst("\"#size\"","10");

        esRequest = esRequest.replaceFirst("approval_date","end_date");
        esRequest = esRequest.replaceFirst("\"#includes\"",StaticVariable.searchProductIncludeFields);
        esRequest = esRequest.replaceFirst("\"#excludes\"","");
        esRequest = esRequest.replaceFirst("\"#filter\"",filter);
        esRequest = esRequest.replaceFirst("#query",condition);

        String productAggsProductName = "{ \"tags\":{ \"terms\":{ \"field\":\"product_name_agg\",\"size\":1000  }" +
                " } }";

        postbody = esRequest.replaceFirst("\"#aggs\"",productAggsProductName);

        System.out.println(postbody);
        ret = HttpHandler.httpPostCall("http://localhost:9200/product/_search", postbody);
        ESResultRoot retObj = new GsonBuilder().create().fromJson(ret, ESResultRoot.class);

        //聚合
        ArrayList<Map> productBuckets = (ArrayList<Map>) ((Map) retObj.aggregations.get("tags")).get("buckets");

        for (Map map : productBuckets) {
            String name = (String) map.get("key");
            int product_count = (new Double((Double) map.get("doc_count"))).intValue();

            writeToFile("F:\\test.txt",name+","+product_count);

        }
        return "";
    }

    /**
     *
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping("/method/product_temp_two")
    public String product_temp_two(@RequestParam("file") MultipartFile file, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(file.getInputStream(), "utf-8"));
            String line;
            int i = 0;
            while ((line=reader.readLine())!=null){
                if(line.startsWith("\uFEFF")){
                    continue;
                }
                try {
                    String[] lines = line.split(",");
                    if(lines.length==2) {
                        String name = lines[0];
                        System.out.println(name);
                        String esCount = StaticVariable.esCount;
                        String condition = "product_name_ch:\\\\\""+name+"\\\\\"";
                        esCount = esCount.replaceFirst("#query", condition);
                        esCount = esCount.replaceFirst("\"#filter\"", "");
                        String countRet = HttpHandler.httpPostCall("http://localhost:9200/product/_count", esCount);
                        ESCount esCt = new GsonBuilder().create().fromJson(countRet, ESCount.class);
                        int count = esCt.count;
                        writeToFile("F:\\test_two.txt",line+","+count);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            System.out.println(i);
            if(reader!=null)reader.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        return "";
    }

    private void writeToFile(String filename, String line)
            throws IOException
    {
        BufferedWriter writer = null;
        try {
            FileOutputStream outStm = new FileOutputStream(filename, true);
            writer = new BufferedWriter(new OutputStreamWriter(outStm, "utf-8"));
            writer.write(line);
            writer.newLine();
        }finally {
            if(writer!=null)writer.close();
        }
    }
}
