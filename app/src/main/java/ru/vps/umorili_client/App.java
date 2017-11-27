package ru.vps.umorili_client;

import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.vps.umorili_client.model.loader.api.UmoriliApi;

public class App extends Application {
    private static UmoriliApi umoriliApi;
    private static String UMORILI_URL = "http://umorili.herokuapp.com";

    public static UmoriliApi getUmoriliApi() {
        return umoriliApi;
    }

    public static String getUmoriliUrl() {
        return UMORILI_URL;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(UMORILI_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        umoriliApi = retrofit.create(UmoriliApi.class);
    }
}
