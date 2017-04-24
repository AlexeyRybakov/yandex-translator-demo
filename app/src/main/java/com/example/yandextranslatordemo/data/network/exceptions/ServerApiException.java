package com.example.yandextranslatordemo.data.network.exceptions;


public class ServerApiException extends Exception {

    public int code;

    public ServerApiException(int code) {
        this.code = code;
    }

}
