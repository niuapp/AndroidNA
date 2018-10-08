package com.xxx.base.utils.net;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.GsonBuilder;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.Response;
import com.xxx.base.utils.AppUtils;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


/**
 * Created by niuapp on 2016/7/8 16:33.
 * Project : HandNote.
 * Email : *******
 * -->  网络请求
 */
public class NetUtils {

    public static <T> void get(String url, Object tag, Map<String, String> extendHeaders, Map<String, String> params, RequestCallback<T> callback) {

        HttpHeaders headers = new HttpHeaders();
        if (extendHeaders != null){
            for(Map.Entry<String, String> entry : extendHeaders.entrySet()){
                headers.put(entry.getKey(), entry.getValue());
            }
        }

        OkGo.post(url)
                .tag(tag)
                .headers(headers)
                .upJson(new JSONObject(params).toString())
                .execute(new BaseCallback<Object>() {
                    @Override
                    public void onSuccess(Response<Object> response) {
                        if (callback != null) {
                            try {
                                callback.onSuccess((T) response.body());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(Response<Object> response) {
                        if (callback != null) {
                            try {
                                callback.onError(response.getException());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    /**
     * 取消所有请求
     */
    public static void cancleAll() {
        try {
            OkGo.getInstance().cancelAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 取消标记对应请求
     *
     * @param cancelTag
     */
    public static void cancle(Object cancelTag) {
        try {
            OkGo.getInstance().cancelTag(cancelTag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * @param json
     * @param c
     * @param
     * @return
     */
    public static <P> P fromJson(String json, Class<P> c) {
//        return new GsonBuilder().excludeFieldsWithoutExposeAnnotation()//忽略@Expose
//                .create().fromJson(json, c);
//        return new GsonBuilder().serializeNulls().create().fromJson(json, c);//

        return new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                return f.getName().contains("gson_filter");//过滤字段
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        }).create().fromJson(json, c);
    }


    /**
     * 请求的回调接口
     */
    public interface SuccessListener {
        public abstract void onSuccess(Object data);

        public abstract void onError(String msg);

        public abstract void saveJson(String json);
    }

    /**
     * wifi是否连接
     *
     * @return wifi是否连接
     */
    public static boolean isWifiConnected() {
        Context context = AppUtils.getContext();
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWiFiNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWiFiNetworkInfo != null) {
                return mWiFiNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 是否能上网
     *
     * @return 是否能上网
     */
    public static boolean hasConnectedNetwork() {
        Context context = AppUtils.getContext();
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 打开设置网络界面
     */
    public static void openNetworkSetting(final Context context) {
        //提示对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("网络设置提示").setMessage("网络连接不可用,是否进行设置?").setPositiveButton("设置", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // TODO Auto-generated method stub
                Intent intent = null;
                //判断手机系统的版本  即API大于10 就是3.0或以上版本
                if (android.os.Build.VERSION.SDK_INT > 10) {
                    intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
                } else {
                    intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                }
                context.startActivity(intent);
                System.exit(0);
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
                System.exit(0);
            }
        }).show();
    }


    /**
     * 对请求参数进行URL编码
     */
    private static String encodeParameters(Map<String, String> map) {
        if (map == null) return "";
        StringBuffer buf = new StringBuffer();
        Set<String> set = map.keySet();
        Iterator<String> iterator = set.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            String value = (String) map.get(key);

            if ((key == null) || ("".equals(key)) || (value == null)
                    || ("".equals(value))) {
                continue;
            }
            if (i != 0)
                buf.append("&");
            try {
                buf.append(URLEncoder.encode(key, "UTF-8")).append("=")
                        .append(URLEncoder.encode(value, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            i++;
        }
        return buf.toString();
    }
}
