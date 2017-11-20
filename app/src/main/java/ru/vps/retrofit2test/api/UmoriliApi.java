package ru.vps.retrofit2test.api;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.vps.retrofit2test.model.Post;

public interface UmoriliApi {
    @GET("/api/random")
    Call<List<Post>> getRandomPosts();

    @GET("/api/random")
    Call<List<Post>> getRandomPosts(@Query("num") int num);
}
