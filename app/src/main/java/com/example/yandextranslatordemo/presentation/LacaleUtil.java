package com.example.yandextranslatordemo.presentation;


import android.content.Context;

public class LacaleUtil {

    public static String getUiLanguage(Context context) {
        String language = context.getResources().getConfiguration().locale.getLanguage();
        if (language.contains("ru")) {
            return "ru";
        } else {
            return "en";
        }
    }
}
