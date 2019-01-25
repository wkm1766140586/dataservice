package com.abc.newsserversec.controller.detection;

import com.abc.newsserversec.service.detection.DetectionService;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 承检机构控制器
 */
@RestController
@RequestMapping("/method")
public class DetectionInfoController {

    @Autowired
    private DetectionService detectionService;
    /**
     * 通过关键词找承检机构
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/selectDetectionByMap")
    public String selectDetectionInfoByMap(HttpServletRequest request, HttpServletResponse response){
        response.setHeader("Access-Control-Allow-Origin", "*");

        String keyword = request.getParameter("keyword");
        String num_string = request.getParameter("num");
        String size_string = request.getParameter("size");
        String province = request.getParameter("province");
        String institution_name = request.getParameter("institution_name");//机构名称
        String key = request.getParameter("key");//机构关键词

        if(num_string == null || size_string == null) return "error";

        if(keyword != null && keyword.equals("")) keyword = null;
        if(key != null && key.equals(""))key = null;

        if(keyword == null && key == null) return "error";
        int num = Integer.valueOf(num_string);
        int size = Integer.valueOf(size_string);

        Map<String,Object> map = new HashMap<>();
        Map<String,Object> dataMap = new HashMap<>();

        map.put("keyword",keyword);
        map.put("key",key);
        map.put("startnum",num*size);
        map.put("size",size);
        map.put("province",province);
        map.put("institution_name",institution_name);
        if(num == 0) {
            int count = detectionService.selectDetectionInfoCountByMap(map);
            dataMap.put("count",count);
        }
        dataMap.put("data",detectionService.selectDetectionInfoByMap(map));

        return new GsonBuilder().create().toJson(dataMap);
    }

    /**
     * 通过ID获取机构信息
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/selectDetectionById")
    public String selectDetectionInfoById(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");

        String id = request.getParameter("id");
        if(id == null) return "error";

        return new GsonBuilder().create().toJson(detectionService.selectDetectionInfoById(id));
    }
}
