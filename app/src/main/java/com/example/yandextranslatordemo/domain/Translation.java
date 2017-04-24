package com.example.yandextranslatordemo.domain;


public class Translation {

    public String direction;
    public String originText;
    public String translation;

    public Translation(String direction, String originText, String translation) {
        this.direction = direction;
        this.originText = originText;
        this.translation = translation;
    }
}
