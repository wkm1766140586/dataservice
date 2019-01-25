package com.abc.newsserversec.controller.detection;

import com.abc.newsserversec.service.detection.DetectionService;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 承检机构批量上传控制器
 */
@RestController
@RequestMapping("/method")
public class DetectionUploadController {

    @Autowired
    private DetectionService detectionService;
    @RequestMapping("/uploadDetection")
    public String upload(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        txt2String("/data/jys/检测所列表.txt");
        return "";

    }

    public String txt2String(String filepath){
        File file = new File(filepath);
        StringBuilder result = new StringBuilder();
        try{
            InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "UTF-8");
            BufferedReader br = new BufferedReader(isr);//构造一个BufferedReader类来读取文件
            String s = null;
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
                result.append(System.lineSeparator()+s);
               // System.out.println(s);
                datatojson(s);
            }
            br.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return result.toString();
    }

    public String detailinfo(String filepath,String uid){
        File file = new File(filepath);
        StringBuilder result = new StringBuilder();
        if(file.exists()){
            try{
                InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "UTF-8");
                BufferedReader br = new BufferedReader(isr);//构造一个BufferedReader类来读取文件
                String s = null;
                while((s = br.readLine())!=null){//使用readLine方法，一次读一行
                    result.append(System.lineSeparator()+s);
                    detailtojson(s,uid);
                }
                br.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }else{
            System.out.println(filepath+"不存在");
        }

        return result.toString();
    }

    public String datatojson(String data){
        JSONObject jsonObject = JSONObject.fromObject(data);
        Map<String,Object> map = new HashMap<>();
        String uuid = getUUID();
        map.put("id",uuid);
        map.put("institution_name",jsonObject.get("institution_name"));
        map.put("registration_number",jsonObject.get("registration_number"));
        map.put("institution_other_name",jsonObject.get("institution_other_name"));
        map.put("effective_date",jsonObject.get("effective_date"));
        map.put("expiry_date",jsonObject.get("expiry_date"));
        map.put("unavailable_item",jsonObject.get("unavailable_item"));
        map.put("cert_update_date",jsonObject.get("cert_update_date"));
        map.put("valid_date",jsonObject.get("valid_date"));
        map.put("attach_update_date",jsonObject.get("attach_update_date"));
        map.put("contact",jsonObject.get("contact"));
        map.put("phone",jsonObject.get("phone"));
        map.put("fax",jsonObject.get("fax"));
        map.put("website",jsonObject.get("website"));
        map.put("email",jsonObject.get("email"));
        map.put("address",jsonObject.get("address"));
        map.put("postal_code",jsonObject.get("postal_code"));
        map.put("date",jsonObject.get("date"));
        detectionService.insertDetectionInfo(map);

        String name = jsonObject.get("institution_name").toString();
        String filePath = "/data/jys/"+name+".txt";
        detailinfo(filePath,uuid);
        return "success!";
    }
    public String detailtojson(String data,String pid){
        JSONObject jsonObject = JSONObject.fromObject(data);
        Map<String,Object> map = new HashMap<>();
        map.put("id",getUUID());
        map.put("pid",pid);
        map.put("scope",jsonObject.get("scope"));
        map.put("list_number",jsonObject.get("list_number"));
        map.put("test_object",jsonObject.get("test_object"));
        map.put("item_number",jsonObject.get("item_number"));
        map.put("item_content",jsonObject.get("item_content"));
        map.put("standard_or_method",jsonObject.get("standard_or_method"));
        map.put("note",jsonObject.get("note"));
        map.put("item_state",jsonObject.get("item_state"));
        map.put("institution_name",jsonObject.get("institution_name"));
        map.put("date",jsonObject.get("date"));

        detectionService.insertDetectionDetailInfo(map);
        return "";
    }

    public static String getUUID(){
        String uuid = UUID.randomUUID().toString();
        //去掉“-”符号
        return uuid.replaceAll("-", "");
    }
}
