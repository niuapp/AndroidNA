package com.xxx.base.utils.net;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/12/11.
 * <p/>
 * 设置请求参数的工具类，返回Map集合
 */
public class RequestMapData {

//    /**
//     * client_type    客户端类型 Android 5
//     * uuid           设备id
//     * client_version 客户端版本
//     * session_key    登录用户session
//     *
//     * @return 基本请求参数
//     */
//    public static Map<String, String> baseParamsMap() {
//        String uuid = YuntooArtUtils.getSharedPreferences().getString("ytart_UUID", "");
//        //判断本地是否有uuid
//        if (TextUtils.isEmpty(uuid)) {
//            uuid = UUID.randomUUID().toString();
//            YuntooArtUtils.getSharedPreferences().edit().putString("ytart_UUID", uuid).commit();
//        }
//
//        HashMap<String, String> paramsMap = new HashMap<String, String>();
//        //客户端类型 5  设备id  客户端版本号  session_key
//        paramsMap.put("client_type", "5");
//        paramsMap.put("uuid", TextUtils.isEmpty(uuid) ? "" : uuid);
//        paramsMap.put("client_version", PackageUtils.getVersionName() + "");
//        paramsMap.put("session_key", YuntooArtUtils.getUserInfo().SESSION_KEY + "");
////        paramsMap.put("session_key", "hxr");
//
//        return paramsMap;
//    }

    public static Map<String, String> baseParams() {

//        String uuid = FileUtils.getSharedPreferences().getString("ZBX_UUID", "");
//        //判断本地是否有uuid
//        if (TextUtils.isEmpty(uuid)) {
//            uuid = UUID.randomUUID().toString();
//            FileUtils.getSharedPreferences().edit().putString("ZBX_UUID", uuid).commit();
//        }

        HashMap<String, String> paramsMap = new HashMap<String, String>();

//        paramsMap.put("phonenum", SystemUtils.getAndroidId());
//
//        if (ZBXUtils.getUserInfo() != null) {
//            paramsMap.put("token", StringUtils.checkStr_empty(ZBXUtils.getUserInfo().getSESSION_KEY()));
//        }


        return paramsMap;
    }


    /**
     * 经纬度
     *
     * @param lat_data
     * @param lng_data
     * @return
     */
    public static Map<String, String> locationParams(String lat_data, String lng_data) {

        HashMap<String, String> paramsMap = new HashMap<String, String>(baseParams());

        paramsMap.put("lat_data", lat_data);
        paramsMap.put("lng_data", lng_data);

        return paramsMap;
    }
}
