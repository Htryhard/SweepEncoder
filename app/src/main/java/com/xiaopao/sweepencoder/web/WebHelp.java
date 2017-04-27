package com.xiaopao.sweepencoder.web;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * 访问网络助手类
 * Created by Huan on 2017/3/17 0017.
 */

public class WebHelp {

    /**
     * 根据有效的url，获取指定的json字符串
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static String getJson(URL url) throws IOException {
        String jsonResult = "";
        URLConnection conurl = url.openConnection();
        InputStream is = conurl.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String line;
        while ((line = br.readLine()) != null) {
            jsonResult = line;
        }
        br.close();
        isr.close();
        is.close();
        return jsonResult;
    }

    /**
     * post提交数据
     *
     * @param userData
     * @param url
     * @return
     * @throws IOException
     */
    public static String postJson(String userData, URL url) throws IOException {
        String jsonResult = "";
        BufferedReader in = null;
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setDoOutput(true);
        urlConnection.setDoInput(true);
        urlConnection.setChunkedStreamingMode(0);
        urlConnection.setRequestMethod("POST");
        urlConnection.setUseCaches(false);
        urlConnection.setRequestProperty("Content_Type", "application/json");
        byte[] data = userData.getBytes("UTF-8");
        urlConnection.setRequestProperty("Content_Length", String.valueOf(data.length));
        OutputStream stream = null;
        try {
            stream = urlConnection.getOutputStream();
            stream.write(data);
            stream.flush();


            in = new BufferedReader(
                    new InputStreamReader(urlConnection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                jsonResult += line;
            }
        } finally {
            stream.close();
            if (in != null) {
                in.close();
            }
            urlConnection.disconnect();
        }

        return jsonResult;
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url   发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {

            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！" + e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }
}
