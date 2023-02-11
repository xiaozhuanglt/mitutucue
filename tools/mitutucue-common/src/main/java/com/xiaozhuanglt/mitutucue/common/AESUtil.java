/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.xiaozhuanglt.mitutucue.common;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.UUID;

/**
 * @author hxz（Eason）
 * @version $Id: AESUtil.java, v 0.1 2023-02-06 09:59 hxz（Eason） Exp $$
 */
public class AESUtil {
    public static String ALGORITHM_DES = "DES";
    public static String ALGORITHM_3DES = "DESede"; // 3DES
    public static String ALGORITHM_BLOWFISH = "Blowfish";
    public static String ALGORITHM_AES = "AES";

    /**
     * 获得一个 密钥长度为 8*32 = 256 位的 AES 密钥，
     *
     * @return 返回经 BASE64 处理之后的密钥字符串（并截取 32 字节长度）
     */
    public static String getAESStrKey() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        UUID uuid = UUID.randomUUID();
        String aesKey = Base64.getEncoder().encodeToString(uuid.toString().getBytes()).substring(2, 34);
        return aesKey;
    }

    /**
     * 获得一个初始化向量，初始化向量长度为 4*4 = 16 个字节
     *
     * @return 返回经 BASE64 处理之后的密钥字符串（并截取 16 字节长度）
     */
    public static String getIv() {
        UUID uuid = UUID.randomUUID();
        String iv = Base64.getEncoder().encodeToString(uuid.toString().getBytes()).substring(2, 18);
        return iv;
    }

    // 生产随机初始向量
    public static IvParameterSpec genRandomIV() throws Exception {
        byte[] randomBytes = new byte[16];
        SecureRandom r = new SecureRandom();
        r.nextBytes(randomBytes);
        IvParameterSpec iv = new IvParameterSpec(randomBytes);
        return iv;
    }

    /**
     * 获得 AES key 及 初始化向量 iv
     * 其实 iv 和 aesKey 两者的生成并没有什么关系，两者只是对各自的长度有限制，
     * 这里只是为了方便使用，进行了一个组合返回。
     *
     * @return 返回 iv 和 aesKey 的组合
     */
    public static HashMap<String, String> getAESKeyAndIv() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        HashMap<String, String> aesKeyAndIv = new HashMap<>();
        aesKeyAndIv.put("aesKey", AESUtil.getAESStrKey());
        aesKeyAndIv.put("iv", AESUtil.getIv());

        return aesKeyAndIv;
    }

    public static Key genKey(String key) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
        return secretKey;
    }


    // 加密方法
    public static String encryptAES(String key, String iv, String text) throws Exception {
        // 获得一个 SecretKeySpec
        Key secretKey = genKey(key);
        // ECB是分组模式，PKCS5Padding 是补全策略
        Cipher cipher = Cipher.getInstance(ALGORITHM_AES + "/CBC/PKCS5Padding");
        // 获得一个 IvParameterSpec
        // 使用 CBC 模式，需要一个向量 iv, 可增加加密算法的强度
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes("UTF-8"));
        // 根据参数初始化算法
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
        cipher.doFinal(text.getBytes());
        return Base64.getEncoder().encodeToString(cipher.doFinal(text.getBytes()));
    }

    // 解密方法
    public static String decryptAES(String key, String iv, String text) throws Exception {
        // 密文进行 BASE64 解密处理
        byte[] contentDecByBase64 = Base64.getDecoder().decode(text);
        // 获得一个 SecretKeySpec
        Key secretKey = genKey(key);
        Cipher cipher = Cipher.getInstance(ALGORITHM_AES + "/CBC/PKCS5Padding");
        // 获得一个初始化 IvParameterSpec
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
        return new String(cipher.doFinal(contentDecByBase64), "UTF-8");
    }

    public static void main(String[] args) {
//        String content = "abcdefg789+-*+="; // 待加密的字符串
        String url = "https://touchngoewallet.onelink.me/p2p/money.packey?activateId=123abcd567gf..&userId=111222333444";
        String[] split = url.split("\\?");
        String content = split[1];
        try {
            HashMap<String, String> aesKeyAndIv = AESUtil.getAESKeyAndIv();
            System.out.println(aesKeyAndIv);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String aesKey = "I0YmM5NTgtY2IyYi00OWIzLWFkZTktZj";
        String iv = "BjNzhiZDctOGMxOS";

        try {
            // 加密
            String encrypted = AESUtil.encryptAES(aesKey, iv, content);
            // 解密
            String decrypted = AESUtil.decryptAES(aesKey, iv, encrypted);

            System.out.println("加密之后的密文为：" + encrypted +
                    "\n" + "解密之后的明文为：" + decrypted +
                    "\n" + "原始明文为：" + content +
                    "\n" + "解密是否成功：" + (content.equals(decrypted)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
