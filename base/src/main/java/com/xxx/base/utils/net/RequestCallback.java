package com.xxx.base.utils.net;

public abstract class RequestCallback<T> {
    public abstract void onSuccess(T result);
    public void onError(Throwable errorInfo){}
}