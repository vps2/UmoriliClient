package ru.vps.retrofit2test.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

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
    private static final int NUMBER_OF_REQUESTED_POSTS = 30;
    //
    private List<Post> posts = new ArrayList<>();
    //
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView postsView;
    private MenuItem refreshMenuItem;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        postsView = (RecyclerView) rootView.findViewById(R.id.posts);
        postsView.setHasFixedSize(true);
        postsView.setLayoutManager(new LinearLayoutManager(getContext()));
        postsView.setAdapter(new PostsRecyclerAdapter(posts));

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(this::getPostsAsync);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_fragment_main, menu);

        refreshMenuItem = menu.findItem(R.id.menu_refresh);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                swipeRefreshLayout.setRefreshing(true);
                refreshMenuItem.setEnabled(false);
                getPostsAsync();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
        } else {
            getPostsAsync();
        }
    }

    private void getPostsAsync() {
        Call<List<Post>> getPostsCall = App.getUmoriliApi().getRandomPosts(NUMBER_OF_REQUESTED_POSTS);
        getPostsCall.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(@NonNull Call<List<Post>> call, @NonNull Response<List<Post>> response) {
                swipeRefreshLayout.setRefreshing(false);
                refreshMenuItem.setEnabled(true);

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
                swipeRefreshLayout.setRefreshing(false);
                refreshMenuItem.setEnabled(true);

                final Snackbar snackbar = Snackbar.make(postsView, t.getLocalizedMessage(), MESSAGE_SHOW_TIME);
                snackbar.setAction(R.string.ok, view -> snackbar.dismiss());
                snackbar.show();
            }
        });
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
