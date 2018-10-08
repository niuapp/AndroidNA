package com.xxx.androidna;

import android.os.Bundle;
import android.view.View;

import com.xxx.base.app.BaseActivity;
import com.xxx.base.utils.AppUtils;
import com.xxx.base.utils.CodeUtils;
import com.xxx.base.utils.DevicesUtil;
import com.xxx.base.utils.net.NetUtils;
import com.xxx.base.utils.net.RequestCallback;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BaseActivity {

    @Override
    protected View getContentView() {
        return View.inflate(this, R.layout.activity_main, null);
    }


    public static String getClientMsg() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("BRAND", android.os.Build.BRAND);
        map.put("MODEL", android.os.Build.MODEL);
        map.put("SDK_INT", android.os.Build.VERSION.SDK_INT);
        map.put("SDK_RELEASE", android.os.Build.VERSION.RELEASE);

        return mapToJsonString(map);
    }

    public static String mapToJsonString(Map map) {
        JSONObject jsonObject = new JSONObject(map);
        return jsonObject.toString();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {

        $(R.id.testButton).setOnClickListener(v -> {

            HashMap<String, String> map = new HashMap<>();
            map.put("account", "13988817269");

            String timestamp = "" + System.currentTimeMillis() / 1000;
            String signature;
            HashMap<String, String> headers = new HashMap<>();
            headers.put("os", "android");
            headers.put("equipment", URLEncoder.encode(getClientMsg()));
            headers.put("User-Agent", "%E7%A7%AF%E7%9B%AE/1.7.1 CFNetwork/808.3 Darwin/16.3.0");
            headers.put("version", "3.0.1");
            headers.put("device", DevicesUtil.getDevicesId());
            signature = "Account" + "checkAccountStatus" + timestamp;
            headers.put("timestamp", timestamp);    //header不支持中文
            headers.put("signature", CodeUtils.md5Encoded(signature));


            NetUtils.get(
                    "https://dev.gmugmu.com/" + "api/account/checkaccountstatus",
                    this,
                    headers,
                    map,
                    new RequestCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            AppUtils.showToastSafe(result);
                        }
                    });
        });
    }
}
