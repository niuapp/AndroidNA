package com.xxx.base.utils.net;

import com.google.gson.stream.JsonReader;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.request.base.Request;
import com.socks.library.KLog;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Response;
import okhttp3.ResponseBody;


public class BaseModelCallBack<T> extends AbsCallback<T> {

    private RequestCallback<T> mRequestCallback;

    public BaseModelCallBack(RequestCallback<T> requestCallback) {
        this.mRequestCallback = requestCallback;
    }


    @Override
    public void onSuccess(com.lzy.okgo.model.Response<T> response) {
        if (mRequestCallback != null) {
            mRequestCallback.onSuccess(response.body());
        }
    }

    @Override
    public void onError(com.lzy.okgo.model.Response<T> response) {
        super.onError(response);
        if (mRequestCallback != null) {
            mRequestCallback.onError(response.getException());
        }
    }

    @Override
    public void onStart(Request<T, ? extends Request> request) {
        super.onStart(request);

        KLog.d("url -->  " + request.getUrl());

//        // 主要用于在所有请求之前添加公共的请求头或请求参数
//        // 例如登录授权的 token
//        // 使用的设备信息
//        // 可以随意添加,也可以什么都不传
//        // 还可以在这里对所有的参数进行加密，均在这里实现
//        request.headers("header1", "HeaderValue1")//
//                .params("params1", "ParamsValue1")//
//                .params("token", "3215sdf13ad1f65asd4f3ads1f");
    }

    /**
     * 该方法是子线程处理，不能做ui相关的工作
     * 主要作用是解析网络返回的 response 对象,生产onSuccess回调中需要的数据对象
     */
    @Override
    public T convertResponse(Response response) throws Throwable {

        ResponseBody body = response.body();
        if (body == null) return null;

        if (mRequestCallback == null) {
            response.close();
            return null;
        }

        Type genType = mRequestCallback.getClass().getGenericSuperclass();
        Type type = ((ParameterizedType) genType).getActualTypeArguments()[0];


        if (type == String.class) {
            //当做字符串处理
            T result = (T) body.string();
            response.close();
            return result;
        }
        else if (type == Void.class){
            response.close();
            return null;
        }
        else { // if (type == BaseModel.class)都当做BaseModel处理，抛错到 onError
            JsonReader jsonReader = new JsonReader(body.charStream());

            BaseModel baseModel = Convert.fromJson(jsonReader, type);
            response.close();

            int code = baseModel.getCode();

//            例：
            if (code == 0) {
//            如果结果是成功的
                return (T) baseModel;
            }

//        else if (code == 104) {
//            throw new IllegalStateException("用户授权信息无效");
//        } else if (code == 105) {
//            throw new IllegalStateException("用户收取信息已过期");
//        }

            else {
//            直接将服务端的错误信息抛出，onError中可以获取
                throw new IllegalStateException("错误代码：" + code + "，错误信息：" + baseModel.getMessage());
            }
        }
    }
}