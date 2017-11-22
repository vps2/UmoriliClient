package ru.vps.retrofit2test.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.vps.retrofit2test.App;
import ru.vps.retrofit2test.PostsRecyclerAdapter;
import ru.vps.retrofit2test.R;
import ru.vps.retrofit2test.model.Post;

public class MainFragment extends Fragment {
    private static final String EXTRA_POSTS = "ru.vps.retrofit2test.extra.posts";
    private static final int MESSAGE_SHOW_TIME = 10000;
    //
    private List<Post> posts = new ArrayList<>();
    //
    private RecyclerView postsView;
    private Button getPosts;
    private ProgressBar loadingProgress;
    //
    private Call<List<Post>> getPostsCall;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        postsView = (RecyclerView) rootView.findViewById(R.id.posts);
        postsView.setHasFixedSize(true);
        postsView.setLayoutManager(new LinearLayoutManager(getContext()));
        postsView.setAdapter(new PostsRecyclerAdapter(posts));

        loadingProgress = (ProgressBar) rootView.findViewById(R.id.loadingProgress);

        getPosts = (Button) rootView.findViewById(R.id.getPosts);
        getPosts.setOnClickListener(new View.OnClickListener() {
            private static final int NUMBER_OF__POSTS = 15;

            @Override
            public void onClick(final View v) {
                showGetPostsButton(false);

                getPostsCall = App.getUmoriliApi().getRandomPosts(NUMBER_OF__POSTS);
                getPostsCall.enqueue(new Callback<List<Post>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<Post>> call, @NonNull Response<List<Post>> response) {
                        showGetPostsButton(true);

                        if (!response.isSuccessful()) {
                            return;
                        }

                        List<Post> body = response.body();

                        if (body != null) {
                            //TODO возможно настройку ссылок надо перенести на уровень формирования объектов Post из JSON
                            configuringLinks(body, response);

                            posts.clear();
                            posts.addAll(body);

                            postsView.setAdapter(new PostsRecyclerAdapter(posts));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<Post>> call, @NonNull Throwable t) {
                        showGetPostsButton(true);

                        final Snackbar snackbar = Snackbar.make(v, t.getLocalizedMessage(), MESSAGE_SHOW_TIME);
                        snackbar.setAction(R.string.ok, v1 -> snackbar.dismiss());
                        snackbar.show();
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
                });
            }
        });

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(EXTRA_POSTS, (Serializable) posts);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null) {
            @SuppressWarnings("unchecked")
            List<Post> temp = (List<Post>) savedInstanceState.getSerializable(EXTRA_POSTS);
            posts = temp;
            postsView.setAdapter(new PostsRecyclerAdapter(posts));
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (getPostsCall.isExecuted()) {
            getPostsCall.cancel();

            showGetPostsButton(true);
        }
    }

    //TODO надо как-то правильно обозвать метод
    private void showGetPostsButton(boolean flag) {
        if (flag) {
            getPosts.setVisibility(View.VISIBLE);
            loadingProgress.setVisibility(View.GONE);
        } else {
            getPosts.setVisibility(View.GONE);
            loadingProgress.setVisibility(View.VISIBLE);
        }
    }
}
