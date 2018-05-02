package com.abc.newsserversec.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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
