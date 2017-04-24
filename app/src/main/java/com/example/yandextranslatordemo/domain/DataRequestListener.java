package com.example.yandextranslatordemo.domain;


public interface DataRequestListener<T> {

    void onSuccess(T t);

    void onError(String s);

}
