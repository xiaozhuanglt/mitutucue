package com.xiaozhuanglt.mitutucue.common;

import net.sf.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * @author hxz
 * @date  2019/1/23/023 14:20
 *
 * java 做http/https 请求工具类
 */
public class HttpRequestUtil {

    public static JSONArray httpsRequest(String requestUrl, String requestMethod){

        Logger logger = LoggerFactory.getLogger(HttpRequestUtil.class);

//        requestUrl = "https://restapi.amap.com/v3/config/district?keywords=%E5%8C%97%E4%BA%AC&subdistrict=2&key=7d5c4e2d4aa475b9682aa7c15217e84b";
        JSONArray jsonArray = null;
        try {

            // 创建SSLContext对象，并使用我们指定的信任管理器初始化
            TrustManager[] tm = { new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                }
                @Override
                public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                }
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            } };
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            sslContext.init(null, tm, new java.security.SecureRandom());
            // 从上述SSLContext对象中得到SSLSocketFactory对象
            SSLSocketFactory ssf = sslContext.getSocketFactory();

            URL url = new URL(requestUrl);
            HttpsURLConnection httpUrlConn = (HttpsURLConnection) url.openConnection();
            httpUrlConn.setSSLSocketFactory(ssf);

            httpUrlConn.setDoOutput(true);
            httpUrlConn.setDoInput(true);
            httpUrlConn.setUseCaches(false);
            // 设置请求方式（GET/POST）
            httpUrlConn.setRequestMethod(requestMethod);

            if ("GET".equalsIgnoreCase(requestMethod))
                httpUrlConn.connect();

            // 将返回的输入流转换成字符串
            InputStream inputStream = httpUrlConn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer buffer = new StringBuffer();
            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            bufferedReader.close();
            inputStreamReader.close();
            // 释放资源
            inputStream.close();
            httpUrlConn.disconnect();
//            jsonObject = JSONObject.fromObject(buffer.toString());
            if (buffer.substring(0,1) != "["){
                jsonArray = JSONArray.fromObject("[" + buffer.toString() + "]");
            }
            System.out.print(1);
        } catch (ConnectException ce) {
            logger.error("server connection timed out.");
        } catch (Exception e) {
            logger.error("https request error:{}", e);
        }
        return jsonArray;
    }

}
