package com.xxx.base.utils;

import android.text.TextUtils;
import android.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;

/**
 * Created by Administrator on 2016/1/13.
 */
public class CodeUtils {

    // ===================================== MD5 ================================= 待
    /**
     * 把字符串进行 md5加密
     *
     * @param str 被加密的字符串
     * @return 加密后的字符串
     */
    public static String md5Encoded(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            return md5Encoded(md.digest());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String md5Encoded(File file) {
        InputStream fis;
        byte[] buffer = new byte[1024];
        int numRead = 0;
        MessageDigest md5;
        try {
            fis = new FileInputStream(file);
            md5 = MessageDigest.getInstance("MD5");
            while ((numRead = fis.read(buffer)) > 0) {
                md5.update(buffer, 0, numRead);
            }
            fis.close();
            return md5Encoded(md5.digest());
        } catch (Exception e) {
            System.out.println("error");
            return null;
        }
    }

    public static String md5Encoded(byte[] data) {
        try {
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < data.length; offset++) {
                i = data[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            //32位加密
            return buf.toString();
            // 16位的加密
            //return buf.toString().substring(8, 24);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ===================================== URL =================================

    /**
     * url解码
     * @param str
     * @return
     */
    public static String decodeUrl(String str){

        if (TextUtils.isEmpty(str)) str = "";
        try {
            return URLDecoder.decode(str, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * url编码
     * @param str
     * @return
     */
    public static String encodeUrl(String str){
        if (TextUtils.isEmpty(str)) str = "";
        try {
            return URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }
    // ===================================== BASE64 =================================
    /**
     * 加密 String --> String
     * @param str 要加密的字符串
     * @return
     */
    public static String encodeByBase64(String str){
        return Base64.encodeToString(str.getBytes(), Base64.NO_WRAP);
    }

    /**
     * 解密 String --> String
     * @param str 要解码的字符串
     * @return
     */
    public static String decodeByBase64(String str){
        return new String(Base64.decode(str, Base64.NO_WRAP));
    }


    /**
     * 加密 byte[] --> byte[]
     * @param byteArray 要加密的字节数组(图片什么的)
     * @return
     */
    public static byte[] encodeByBase64ToByteArray(byte[] byteArray){
        return Base64.encode(byteArray, Base64.DEFAULT);
    }

    /**
     * 解密 byte[] --> byte[]
     * @param byteArray 要解码的字节数组
     * @return
     */
    public static byte[] decodeByBase64FromByteArray(byte[] byteArray){
        return Base64.decode(byteArray, Base64.DEFAULT);
    }

}
