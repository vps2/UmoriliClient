package ru.vps.umorili_client.model.loader.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.vps.umorili_client.model.Post;

public interface UmoriliApi {
    @GET("/api/random")
    Call<List<Post>> getRandomPosts();

    @GET("/api/random")
    Call<List<Post>> getRandomPosts(@Query("num") int num);
}
