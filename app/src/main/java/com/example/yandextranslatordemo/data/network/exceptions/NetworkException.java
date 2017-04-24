package com.example.yandextranslatordemo.data.network.exceptions;


public class NetworkException extends Exception {


    public NetworkException(String message) {
        super(message);
    }

    public NetworkException(String message, final Throwable cause) {
        super(message, cause);
    }
}
