package ru.vps.retrofit2test;

import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.vps.retrofit2test.api.UmoriliApi;


public class App extends Application
{
   public static UmoriliApi getApi()
   {
      return umoriliApi;
   }

   @Override
   public void onCreate()
   {
      super.onCreate();

      Gson gson = new GsonBuilder().setLenient().create();
      Retrofit retrofit = new Retrofit.Builder().baseUrl("http://umorili.herokuapp.com/")
                                                .addConverterFactory(GsonConverterFactory.create(gson))
                                                .build();
      umoriliApi = retrofit.create(UmoriliApi.class);
   }

   private static UmoriliApi umoriliApi;
}
