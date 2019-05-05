package com.xiaozhuantlt.mitutucue.common;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;

/**
 * @Author: zyy
 * @Date: 2018/11/29 14:53
 * @Description:
 */
public class ResponseHeaderUtil {

    //发送响应流方法
    public static void setResponseHeader(HttpServletResponse response, String fileName) {
        try {
            try {
                fileName = new String(fileName.getBytes("gbk"), "iso8859-1");
                //fileName = new String(fileName.getBytes(),"utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            response.setContentType("application/x-download");//下面三行是关键代码，处理乱码问题
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/octet-stream;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment;filename="+ fileName);
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
