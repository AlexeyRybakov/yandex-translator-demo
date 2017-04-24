package com.example.yandextranslatordemo.data.network;


import com.example.yandextranslatordemo.BuildConfig;
import com.example.yandextranslatordemo.Const;
import com.example.yandextranslatordemo.data.DataMapper;
import com.example.yandextranslatordemo.data.network.exceptions.NetworkException;
import com.example.yandextranslatordemo.data.network.exceptions.ResponseCodeError;
import com.example.yandextranslatordemo.data.network.exceptions.ServerApiException;
import com.example.yandextranslatordemo.data.network.model.LanguagesResponse;
import com.example.yandextranslatordemo.data.network.model.TranslationResponse;
import com.example.yandextranslatordemo.domain.Languages;
import com.example.yandextranslatordemo.domain.Translation;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.yandextranslatordemo.Const.BASE_URL;

public class NetworkDataManagerImpl implements NetworkDataManager {

    private ApiService apiService;

    public NetworkDataManagerImpl() {
        OkHttpClient client;
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            client = new OkHttpClient.Builder()
                    .addNetworkInterceptor(new Interceptor() {
                        @Override
                        public okhttp3.Response intercept(Chain chain) throws IOException {
                            Request original = chain.request();
                            HttpUrl originalHttpUrl = original.url();
                            HttpUrl url = originalHttpUrl.newBuilder()
                                    .addQueryParameter("key", Const.API_KEY)
                                    .build();
                            Request.Builder requestBuilder = original.newBuilder()
                                    .url(url);
                            Request request = requestBuilder.build();
                            return chain.proceed(request);
                        }
                    })
                    .addInterceptor(logging)
                    .build();
        } else {
            client = new OkHttpClient();
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        apiService = retrofit.create(ApiService.class);
    }


    @Override
    public Languages getLangs(String ui) throws NetworkException {
        try {
            Response<LanguagesResponse> response = apiService.getLanguages(ui).execute();
            if (response.isSuccessful()) {
                LanguagesResponse body = response.body();
                if (body.langs != null) {
                    return DataMapper.transform(ui, body);
                } else {
                    throw new NetworkException(Errors.UNKNOWN);
                }
            } else {
                throw new NetworkException(Errors.SERVER_ERROR);
            }
        } catch (IOException e) {
            throw new NetworkException(Errors.CONNECTION_ERROR, e);
        }
    }

    @Override
    public Translation translate(String langs, String originText) throws NetworkException {
        try {
            Response<TranslationResponse> response = apiService.translate(langs, originText).execute();
            if (response.isSuccessful()) {
                TranslationResponse translation = response.body();
                if (translation.code.equals("200")) {
                    return DataMapper.transform(originText, response.body());
                } else {
                    throw new NetworkException(Errors.SERVER_ERROR, new ServerApiException(Integer.valueOf(translation.code)));
                }
            } else {
                throw new NetworkException(Errors.SERVER_ERROR, new ResponseCodeError(response.code()));
            }
        } catch (IOException e) {
            throw new NetworkException(Errors.CONNECTION_ERROR, e);
        }
    }
}
