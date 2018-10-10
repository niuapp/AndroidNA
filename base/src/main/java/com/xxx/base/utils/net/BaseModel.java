package com.xxx.base.utils.net;

import java.io.Serializable;

/**
 * Created By: Bushijie 399516791@qq.com
 * Date: 2016-12-26 Time: 14:11
 * Describe:
 */
public class BaseModel<T> implements Serializable {

    private int code;
    private String message;
    private String debug;
    private T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDebug() {
        return debug;
    }

    public void setDebug(String debug) {
        this.debug = debug;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}