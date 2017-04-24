package com.example.yandextranslatordemo.data.network;


import com.example.yandextranslatordemo.data.network.exceptions.NetworkException;
import com.example.yandextranslatordemo.domain.Languages;
import com.example.yandextranslatordemo.domain.Translation;

public interface NetworkDataManager {

    Languages getLangs(String ui) throws NetworkException;

    Translation translate(String langs, String originText) throws NetworkException;
}
