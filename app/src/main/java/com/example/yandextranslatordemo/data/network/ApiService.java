package com.example.yandextranslatordemo.data.network;


import com.example.yandextranslatordemo.data.network.model.LanguagesResponse;
import com.example.yandextranslatordemo.data.network.model.TranslationResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    @POST("getLangs")
    Call<LanguagesResponse> getLanguages(@Query("ui") String ui);

    @FormUrlEncoded
    @POST("translate")
    Call<TranslationResponse> translate(@Query("lang") String langParam, @Field("text") String text);

}
