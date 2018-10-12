package com.abc.newsserversec.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

/**
 * Created by hp on 2017/5/26.
 */

public class HttpHandler {

    public static String httpGetCall(String url)
            throws IOException
    {
        String ret;
        HttpURLConnection connection = null;
        try {
            URL urlObj = new URL(url);
            connection = (HttpURLConnection) urlObj.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(1000);
            InputStream inStm = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inStm));
            ret = reader.readLine();
        }finally {
            if(connection!=null)connection.disconnect();
        }
        return ret;
    }

    /**
     * 向指定URL发送GET方法的请求
     *
     * @param url
     *            发送请求的URL
     * @return URL 所代表远程资源的响应结果
     */
    public static String sendGet(String url) {
        String result = "";
        BufferedReader in = null;
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {

            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }
    public static String httpPostCall(String url, String requestBody)
            throws IOException
    {
        String ret;
        HttpURLConnection connection = null;
        try {
            URL urlObj = new URL(url);
            connection = (HttpURLConnection) urlObj.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("content-type","application/json");
//            connection.setRequestProperty("content-type", "plain/text");

//            String userpassword = "njweilai"+":"+"Njweilai1234";
//            String userpwd_B64 = Base64Util.toBase64(userpassword);
//            connection.setRequestProperty("Authorization","Basic "+userpwd_B64);

            connection.setConnectTimeout(1000);
            connection.getOutputStream().write(requestBody.getBytes());
            InputStream inStm = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inStm));
            ret = reader.readLine();
        }finally {
            if(connection!=null)connection.disconnect();
        }
        return ret;
    }
}
