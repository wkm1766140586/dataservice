package com.abc.newsserversec.controller.user;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
public class CodeController {
    final static String apiUser = "Zhixie_test_f64MQp";
    final static String apiKey = "o2DpwtLUrDCi2vUn";
    final String url = "http://api.sendcloud.net/apiv2/mail/send";
    @RequestMapping("/method/sendCode")
    public String sendCode(HttpServletRequest request, HttpServletResponse response1) throws Exception{
        response1.setHeader("Access-Control-Allow-Origin", "*");
        String mail = request.getParameter("mail");
        String code = randomCode();
        JSONObject json = new JSONObject();
        json.put("name",mail);
        json.put("code",code);
        json.put("time",System.currentTimeMillis());
        //this.getSession(request).setAttribute("code",code);
        WebUtils.setSessionAttribute(request,"sendCode",code);
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httPost = new HttpPost(url);
        List params = new ArrayList();
        // 您需要登录SendCloud创建API_USER，使用API_USER和API_KEY才可以进行邮件的发送。
        params.add(new BasicNameValuePair("apiUser", apiUser));
        params.add(new BasicNameValuePair("apiKey", apiKey));
        params.add(new BasicNameValuePair("from", "yixiecha@mail.zhixie.info"));
        params.add(new BasicNameValuePair("fromName", "医械查"));
        params.add(new BasicNameValuePair("to", mail));
        params.add(new BasicNameValuePair("subject", "医械查验证码"));
        params.add(new BasicNameValuePair("html", "<h1>你的验证码为："+code+",有效期3分钟</h1>"));

        httPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
        // 请求
        HttpResponse response = httpclient.execute(httPost);
        // 处理响应
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) { // 正常返回
            // 读取xml文档
            String result = EntityUtils.toString(response.getEntity());
            System.out.println(result);
        } else {
            System.err.println("error");
        }
        httPost.releaseConnection();
        return json.toString();
    }

    /*生成六位随机数*/
    private String randomCode() {
        Random random = new Random();
        String result = "";
        for (int i = 0; i < 6; i++) {
            result += random.nextInt(10);
        }
        return result;
    }
    /*组装*/
    public static String convert(List<A> dataList) {
        JSONObject ret = new JSONObject();
        JSONArray to = new JSONArray();
        JSONArray codes = new JSONArray();
        for (A a : dataList) {
            to.add(a.getName());
            codes.add(a.getCode());
        }
        JSONObject sub = new JSONObject();
        sub.put("%name%", codes);

        ret.put("to", to);
        ret.put("sub", sub);

        return ret.toString();
    }
    public static HttpSession getSession(HttpServletRequest request){
        return request.getSession();
    }
}
class A{
    private String name;
    private String code;
    public String getName() { return name;}

    public void setName(String name) {this.name = name;}

    public String getCode() { return code;}

    public void setCode(String code) {this.code = code;}
}
