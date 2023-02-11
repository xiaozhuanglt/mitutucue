package com.xiaozhuanglt.mitutucue.common;

/**
 * @description: 编码器
 * @author: hxz
 * @create: 2019-05-05 10:55
 **/
public class IncrementalEncoder {

    static Long encoder = Long.valueOf(0) ;

    public static Long getEncoder(){
        encoder = encoder +1;
        return encoder;
    }
}
