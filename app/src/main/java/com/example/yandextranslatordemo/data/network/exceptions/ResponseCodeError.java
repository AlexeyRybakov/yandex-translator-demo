package com.example.yandextranslatordemo.data.network.exceptions;


public class ResponseCodeError extends Exception {

    public int code;

    public ResponseCodeError(int code) {
        this.code = code;
    }

}
