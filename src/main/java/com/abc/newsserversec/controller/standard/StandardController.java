package com.abc.newsserversec.controller.standard;

import com.abc.newsserversec.model.standard.StandardData;
import com.abc.newsserversec.service.standard.StandardInfoService;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 标准查询控制器
 */
@RestController
public class StandardController {

    @Autowired
    private StandardInfoService standardInfoService;

    /**
     * 查询
     */
    @RequestMapping("/method/selectStandardDataByCondition")
    public String selectStandardDataByCondition(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");

        String keyword = request.getParameter("keyword");
        String standard_type = request.getParameter("standard_type");
        String standard_nature = request.getParameter("standard_nature");
        String standard_state = request.getParameter("standard_state");
        String num_string = request.getParameter("num");

        Map<String,Object> map = new HashMap<>();
        Map<String,Object> dataMap = new HashMap<>();

        String standard_code = "";
        int num = Integer.valueOf(num_string)*10;

        if(standard_type != null) {
            if (standard_type.equals("国家标准")) standard_code = "GB";
            else if (standard_type.equals("行业标准")) standard_code = "YY";
        }

        if(standard_nature != null) {
            if (standard_nature.equals("强制性")) standard_nature = "0";
            else if (standard_type.equals("推荐性")) standard_nature = "1";
        }

        if(standard_state != null && standard_state.equals("废止")) standard_state = "%废%";

        if(keyword != null && !keyword.equals("")) map.put("keyword","%"+keyword+"%");
        if(standard_code != null && !standard_code.equals("")) map.put("standard_code","%"+standard_code+"%");
        if(standard_state != null && !standard_state.equals("")) map.put("standard_state",standard_state);
        if(standard_nature != null && !standard_nature.equals("")) map.put("standard_nature",standard_nature);

        map.put("num",num);
        if(num == 0) {
            int count = standardInfoService.selectStandardInfoCountByCondition(map);
            dataMap.put("matchCount",count);
        }

        ArrayList<StandardData> list = standardInfoService.selectStandardInfo(map);
        dataMap.put("datas",list);

        return new GsonBuilder().create().toJson(dataMap);
    }

    /**
     * 通过code找标准
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/method/selectStandardByCode")
    public String selectStandardByCode(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");

        String code = request.getParameter("code");
        if(code == null && "".equals(code)){
            return "error!";
        }
        StandardData standardData = standardInfoService.selectStandardInfoByCode(code);
        return new GsonBuilder().create().toJson(standardData);
    }

    /**
     * 首页底部的信息：实施时间最近的六条信息
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/method/selectRecentStandardInfo")
    public String selectRecentStandardInfo(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        ArrayList<StandardData> list = standardInfoService.selectRecentStandardInfo();
        return new GsonBuilder().create().toJson(list);
    }

//    /**
//     * 上传标准数据
//     */
//    @RequestMapping("/method/uploadStandardData")
//    public String uploadStandardData(@RequestParam("file") MultipartFile file, HttpServletRequest request, HttpServletResponse response){
//        response.setHeader("Access-Control-Allow-Origin", "*");
//
//        BufferedReader reader = null;
//        try {
//            reader = new BufferedReader(new InputStreamReader(file.getInputStream(), "utf-8"));
//            String line;
//            while ((line=reader.readLine())!=null){
//                if(line.startsWith("\uFEFF")){
//                    continue;
//                }
//                try {
//                    JSONObject object = JSONObject.fromObject(line);
//                    StandardData standardData = (StandardData) JSONObject.toBean(object, StandardData.class);
//
//                    String date = standardData.getRelease_date();
//                    if(date!= null && !date.equals("")) {
//                        date = date.substring(0,10);
//                        standardData.setRelease_date(formatTime(date));
//                    }
//                    date = standardData.getImplement_date();
//                    if(date!= null && !date.equals("")) {
//                        date = date.substring(0,10);
//                        standardData.setImplement_date(formatTime(date));
//                    }
//                    date = standardData.getInvalid_date();
//                    if(date!= null && !date.equals("")) {
//                        date = date.substring(0,10);
//                        standardData.setInvalid_date(formatTime(date));
//                    }
//                    date = standardData.getStart_date();
//                    if(date!= null && !date.equals("")) {
//                        date = date.substring(0,10);
//                        standardData.setStart_date(formatTime(date));
//                    }
//                    date = standardData.getReview_date();
//                    if(date!= null && !date.equals("")) {
//                        date = date.substring(0,10);
//                        standardData.setReview_date(formatTime(date));
//                    }
//                    standardInfoService.insertStandardInfo(standardData);
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//            }
//            if(reader!=null)reader.close();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//        return "结束了";
//    }
//
//    /**
//     * 更新下载地址信息
//     */
//    @RequestMapping("/method/updateDownloadUrl")
//    public String updateDownloadUrl(@RequestParam("file") MultipartFile file, HttpServletRequest request, HttpServletResponse response){
//        response.setHeader("Access-Control-Allow-Origin", "*");
//
//        BufferedReader reader = null;
//        try {
//            reader = new BufferedReader(new InputStreamReader(file.getInputStream(), "utf-8"));
//            String line;
//            int i = 0;
//            while ((line=reader.readLine())!=null){
//                if(line.startsWith("\uFEFF")){
//                    continue;
//                }
//                try {
//                    String[] lines = line.split(",");
//
//                    if(lines.length>2) {
//                        String standard_code = lines[1];
//                        int start = standard_code.indexOf("：");
//                        standard_code = standard_code.substring(start+1);
//                        start = standard_code.indexOf("标");
//                        if(start > 0) standard_code = standard_code.substring(0,start);
//
//                        String download_url_one = lines[2];
////                        String standard_code = lines[0];
////                        String download_url_two = lines[1];
////                        System.out.println(standard_code + "@" + download_url_one);
//                        Map<String, Object> map = new HashMap<>();
//                        map.put("standard_code", standard_code);
//
//                        //StandardData standardData = standardInfoService.selectStandardInfo(map);
////                        if(standardData != null) {
////                            i++;
////                            map.put("id",standardData.getId());
////                            map.put("download_url_one", download_url_one);
////                            standardInfoService.updateStandardInfo(map);
////                        }else{
////                            writeToFile("F://gb.txt",standard_code);
////                        }
//                    }
//
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//            }
//            System.out.println(i);
//            if(reader!=null)reader.close();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//        return "";
//    }

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

    /**
     * 日期格式转换
     * @param item
     * @return
     */
    private String formatTime(String item){
        String[] s = null;
        if(item.matches("[0-9]{4}\\.[0-9]{1,2}\\.[0-9]{1,2}")) {
            s = item.split("\\.");
        }else if(item.matches("[0-9]{4}\\/[0-9]{1,2}\\/[0-9]{1,2}")) {
            s = item.split("\\/");
        }else if(item.matches("[0-9]{4}\\-[0-9]{1,2}\\-[0-9]{1,2}")){
            s = item.split("\\-");
        }else if(item.matches("[0-9]{4}\\、[0-9]{1,2}\\、[0-9]{1,2}")){
            s = item.split("\\、");
        }else if(item.matches("[0-9]{8}")){
            s = new String[3];
            s[0] = item.substring(0,4);
            s[1] = item.substring(4,6);
            s[2] = item.substring(6);
        }else if(item.contains("年") && item.contains("月") && item.contains("日")){
            int y = item.indexOf("年");
            int m = item.indexOf("月");
            int d = item.indexOf("日");
            s = new String[3];
            s[0] = item.substring(0,y);
            s[1] = item.substring(y+1,m);
            s[2] = item.substring(m+1,d);
        }else if(item.matches("[0-9]{4}\\.[0-9]{1,2}") || item.matches("[0-9]{4}\\/[0-9]{1,2}")
                || item.matches("[0-9]{4}\\-[0-9]{1,2}") || item.matches("[0-9]{4}\\、[0-9]{1,2}")
                || item.matches("[0-9]{6}")){
            s = new String[3];
            s[0] = item.substring(0, 4);
            s[1] = item.substring(5);
            s[2] = "1";
        }else if(item.contains("年") && item.contains("月")){
            int y = item.indexOf("年");
            int m = item.indexOf("月");
            s = new String[3];
            s[0] = item.substring(0,y);
            s[1] = item.substring(y+1,m);
            s[2] = "1";
        }else if(item.matches("[0-9]{4}")){
            s = new String[3];
            s[0] = item;
            s[1] = "1";
            s[2] = "1";
        }else if(item.contains("年")){
            int y = item.indexOf("年");
            s = new String[3];
            s[0] = item.substring(0,y);
            s[1] = "1";
            s[2] = "1";
        }else{
            return item;
        }

        int year = Integer.parseInt(s[0]);
        int month = Integer.parseInt(s[1]);
        int day = Integer.parseInt(s[2]);
        Calendar c = Calendar.getInstance();
        c.set(year, month - 1, day, 0, 0, 0);
        Date d = c.getTime();
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(d);
    }
}
