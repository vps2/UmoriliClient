package ru.vps.retrofit2test.fragment;

import android.content.Intent;
import android.net.Uri;
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
import ru.vps.retrofit2test.ClickablePostRecyclerAdapter;
import ru.vps.retrofit2test.R;
import ru.vps.retrofit2test.model.Post;

public class MainFragment extends Fragment implements ClickablePostRecyclerAdapter.OnItemClickListener {
    private static final String EXTRA_POSTS = "ru.vps.retrofit2test.extra.posts";
    private static final int MESSAGE_SHOW_TIME = 10000;
    //
    private List<Post> posts = new ArrayList<>();
    //
    private RecyclerView postsView;
    //
    private Call<List<Post>> getPostsCall;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        postsView = (RecyclerView) rootView.findViewById(R.id.posts);
        postsView.setHasFixedSize(true);
        postsView.setLayoutManager(new LinearLayoutManager(getContext()));
        postsView.setAdapter(new ClickablePostRecyclerAdapter(posts, this));

        final ProgressBar loadingProgress = (ProgressBar) rootView.findViewById(R.id.loadingProgress);

        final Button getPosts = (Button) rootView.findViewById(R.id.getPosts);
        getPosts.setOnClickListener(new View.OnClickListener() {
            private static final int NUMBER_OF__POSTS = 15;

            @Override
            public void onClick(final View v) {
                getPosts.setVisibility(View.GONE);
                loadingProgress.setVisibility(View.VISIBLE);

                getPostsCall = App.getUmoriliApi().getRandomPosts(NUMBER_OF__POSTS);
                getPostsCall.enqueue(new Callback<List<Post>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<Post>> call, @NonNull Response<List<Post>> response) {
                        getPosts.setVisibility(View.VISIBLE);
                        loadingProgress.setVisibility(View.GONE);

                        if (!response.isSuccessful()) {
                            return;
                        }

                        List<Post> body = response.body();

                        if (body != null) {
                            posts.clear();
                            posts.addAll(body);

                            postsView.setAdapter(new ClickablePostRecyclerAdapter(posts, MainFragment.this));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<Post>> call, @NonNull Throwable t) {
                        getPosts.setVisibility(View.VISIBLE);
                        loadingProgress.setVisibility(View.GONE);

                        final Snackbar snackbar = Snackbar.make(v, t.getLocalizedMessage(), MESSAGE_SHOW_TIME);
                        snackbar.setAction(R.string.ok, v1 -> snackbar.dismiss());
                        snackbar.show();
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
            postsView.setAdapter(new ClickablePostRecyclerAdapter(posts, this));
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        Post post = posts.get(position);
        String postUrl = App.getUmoriliUrl() + post.getLink();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(postUrl));
        startActivity(intent);
    }

    @Override
    public void onPause() {
        super.onPause();

        if(getPostsCall.isExecuted())
        {
            getPostsCall.cancel();
        }
    }
}
