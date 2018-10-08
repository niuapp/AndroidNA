package com.xxx.base.utils.net;

import com.google.gson.stream.JsonReader;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.model.Response;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;

/**
 * Created by niuapp on 2018/10/8 14:03.
 * Project : AndroidNA.
 * Email : 345485985@qq.com
 * -->
 */
public abstract class BaseCallback<T> extends AbsCallback<T> {

    @Override
    public T convertResponse(okhttp3.Response response) throws Throwable {
        Type genType = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        Type type = params[0];
        ResponseBody responseBody = response.body();
        if (responseBody != null) {
            JsonReader jsonReader = new JsonReader(responseBody.charStream());
            T data = Convert.fromJson(jsonReader, type);
            response.close();
            return data;
        } else {
            response.close();
            return null;
        }
    }

    @Override
    public void onSuccess(Response<T> response) {

    }
}
