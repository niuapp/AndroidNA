package com.xxx.base.app;

/**
 * Created by niuapp on 2016/3/7 15:40.
 * -->
 */
public class BaseConst {


    private static final boolean testFlag = true; //true  url测试模式
    public final boolean deBugFlag = true; //true deBug模式 关闭异常捕获，开启 log
    //是否接收消息推送（true -> 接收）

    private static final BaseConst BASE_CONST = new BaseConst();

    public static BaseConst getInstance() {
        return BASE_CONST;
    }


    static {
        if (testFlag) {
            BASE_URL = "http://192.168.1.33:8081/";//测试
        } else {
            BASE_URL = "http://www.zbxvip.com:8080/";//正式
        }
    }

    // url
    private static String BASE_URL;

    public String BASE_URL() {
        return BASE_URL;
    }

    public void updateBaseUrl(String url){
        BASE_URL = url;
    }

    //------------------------- api url start ---------------------------------------


    //------------------------- api url end -----------------------------------------

    // ===============================================

    public static final String splitMark = "-_-";//分割符
    public final String CACHE_USERINFO = "CACHE_USERINFO";
}
