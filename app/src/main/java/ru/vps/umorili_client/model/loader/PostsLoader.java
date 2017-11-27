package ru.vps.umorili_client.model.loader;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.Loader;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.vps.umorili_client.App;
import ru.vps.umorili_client.model.Post;
import ru.vps.umorili_client.model.loader.result.ErrorResult;
import ru.vps.umorili_client.model.loader.result.GoodResult;
import ru.vps.umorili_client.model.loader.result.Result;

public class PostsLoader extends Loader<Result<List<Post>>> {
    private static final int NUMBER_OF_REQUESTED_POSTS = 30;
    //
    private List<Post> posts = new ArrayList<>();
    private Call<List<Post>> call;

    public PostsLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();

        if (posts.isEmpty()) {
            forceLoad();
        } else {
            deliverResult(new GoodResult<>(posts));
        }
    }

    @Override
    protected void onForceLoad() {
        super.onForceLoad();

        cancelCall();

        call = App.getUmoriliApi().getRandomPosts(NUMBER_OF_REQUESTED_POSTS);
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(@NonNull Call<List<Post>> call, @NonNull Response<List<Post>> response) {
                if (!response.isSuccessful()) {
                    deliverResult(new ErrorResult<>(new RuntimeException(response.message())));
                }

                List<Post> body = response.body();

                if (body != null) {
                    //TODO возможно настройку ссылок надо перенести на уровень формирования объектов Post из JSON
                    configuringLinks(body, response);

                    posts.clear();
                    posts.addAll(body);

                    deliverResult(new GoodResult<>(posts));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Post>> call, @NonNull Throwable t) {
                deliverResult(new ErrorResult<>(t));
            }
        });
    }

    @Override
    protected void onStopLoading() {
        cancelCall();

        super.onStopLoading();
    }

    private void cancelCall() {
        if (call != null) {
            call.cancel();
            call = null;
        }
    }

    private void configuringLinks(List<Post> posts, Response<List<Post>> response) {
        String server = getServerUrl(response);
        for (Post post : posts) {
            String postLink = post.getLink();
            if (postLink != null) {
                String postUrl = server + postLink;
                post.setLink(postUrl);
            }
        }
    }

    private String getServerUrl(Response<List<Post>> response) {
        okhttp3.Response rawResponse = response.raw();
        String url = rawResponse.request().url().toString();
        String urlPath = rawResponse.request().url().encodedPath();

        return url.substring(0, url.indexOf(urlPath));
    }
}
