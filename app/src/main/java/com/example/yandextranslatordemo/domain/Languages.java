package com.example.yandextranslatordemo.domain;


import java.util.Map;

public class Languages {

    public Map<String, String> langs;
    public String uiLanguage;

    public Languages(Map<String, String> langs, String uiLanguage) {
        this.langs = langs;
        this.uiLanguage = uiLanguage;
    }

}
