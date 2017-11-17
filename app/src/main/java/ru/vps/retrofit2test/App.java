package ru.vps.retrofit2test;

import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.vps.retrofit2test.api.UmoriliApi;


public class App extends Application
{
   public static UmoriliApi getUmoriliApi()
   {
      return umoriliApi;
   }

   public static String getUmoriliUrl()
   {
      return UMORILI_URL;
   }

   @Override
   public void onCreate()
   {
      super.onCreate();

      Gson gson = new GsonBuilder().setLenient().create();
      Retrofit retrofit = new Retrofit.Builder().baseUrl(UMORILI_URL)
                                                .addConverterFactory(GsonConverterFactory.create(gson))
                                                .build();
      umoriliApi = retrofit.create(UmoriliApi.class);
   }

   private static UmoriliApi umoriliApi;
   private static String UMORILI_URL = "http://umorili.herokuapp.com";
}
