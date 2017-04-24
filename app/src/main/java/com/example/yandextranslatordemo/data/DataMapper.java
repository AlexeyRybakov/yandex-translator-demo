package com.example.yandextranslatordemo.data;


import com.example.yandextranslatordemo.data.network.model.LanguagesResponse;
import com.example.yandextranslatordemo.data.network.model.TranslationResponse;
import com.example.yandextranslatordemo.data.realm.model.RealmTranslation;
import com.example.yandextranslatordemo.domain.Languages;
import com.example.yandextranslatordemo.domain.Translation;

import java.util.HashMap;

public class DataMapper {

    public static Languages transform(String ui, LanguagesResponse response) {
        return new Languages(new HashMap<>(response.langs), ui);
    }

    public static Translation transform(String originText, TranslationResponse response) {
        String text = response.text != null && response.text.size() > 0 ? response.text.get(0) : "";
        return new Translation(response.lang, originText, text);
    }

    public static RealmTranslation transform(Translation translation) {
        RealmTranslation realmTranslation = new RealmTranslation();
        realmTranslation.setDirection(translation.direction);
        realmTranslation.setOriginText(translation.originText);
        realmTranslation.setTranslation(translation.translation);
        return realmTranslation;
    }


}
