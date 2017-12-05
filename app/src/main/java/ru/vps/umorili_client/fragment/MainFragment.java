package ru.vps.umorili_client.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
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
import java.util.Collections;
import java.util.List;

import ru.vps.umorili_client.PostsRecyclerAdapter;
import ru.vps.umorili_client.R;
import ru.vps.umorili_client.model.Post;
import ru.vps.umorili_client.model.loader.PostsLoader;
import ru.vps.umorili_client.model.loader.result.Result;

public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Result<List<Post>>> {
    private static final int POSTS_LOADER_ID = 1;
    private static final int ERROR_SHOW_TIME = 10000;
    private static final String EXTRA_LOADING = "ru.vps.umorili_client.fragment.main_fragment.loading";
    private static final String EXTRA_CACHED_POSTS = "ru.vps.umorili_client.fragment.main_fragment.cached_posts";
    //
    private RecyclerView postsView;
    private SwipeRefreshLayout swipeRefreshLayout;
    //
    private List<Post> cachedPosts;
    private PostsRecyclerAdapter postsRecyclerAdapter;
    private Loader<Result<List<Post>>> postsLoader;
    private boolean isPostsLoading;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        cachedPosts = new ArrayList<>();
        postsRecyclerAdapter = new PostsRecyclerAdapter(cachedPosts);

        isPostsLoading = true;

        postsLoader = getLoaderManager().initLoader(POSTS_LOADER_ID, Bundle.EMPTY, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        postsView = (RecyclerView) rootView.findViewById(R.id.posts);
        postsView.setHasFixedSize(true);
        postsView.setLayoutManager(new LinearLayoutManager(getContext()));
        postsView.setAdapter(postsRecyclerAdapter);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(this::getPostsAsync);

        return rootView;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null) {
            @SuppressWarnings("unchecked")
            List<Post> savedPosts = (List<Post>) savedInstanceState.getSerializable(EXTRA_CACHED_POSTS);
            updatePostsView(savedPosts);

            isPostsLoading = savedInstanceState.getBoolean(EXTRA_LOADING);
        }

        swipeRefreshLayout.setRefreshing(isPostsLoading);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(EXTRA_LOADING, isPostsLoading);
        outState.putSerializable(EXTRA_CACHED_POSTS, (Serializable) cachedPosts);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_fragment_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                if (!isPostsLoading) {
                    swipeRefreshLayout.setRefreshing(true);
                    getPostsAsync();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Loader<Result<List<Post>>> onCreateLoader(int id, Bundle args) {
        if (id == POSTS_LOADER_ID) {
            return new PostsLoader(getActivity());
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Result<List<Post>>> loader, Result<List<Post>> data) {
        if (loader.getId() != POSTS_LOADER_ID) {
            return;
        }

        if (!data.isUsed()) {
            isPostsLoading = false;
            swipeRefreshLayout.setRefreshing(false);

            updatePostsViewOrShowError(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Result<List<Post>>> loader) {
        if (loader.getId() == POSTS_LOADER_ID) {
            updatePostsView(Collections.emptyList());
        }
    }

    private void getPostsAsync() {
        isPostsLoading = true;

        postsLoader.forceLoad();
    }

    private void updatePostsViewOrShowError(Result<List<Post>> data) {
        try {
            updatePostsView(data.get());
        } catch (Throwable ex) {
            showError(ex.getLocalizedMessage());
        }
    }

    private void updatePostsView(List<Post> posts) {
        cachedPosts.clear();
        cachedPosts.addAll(posts);

        postsRecyclerAdapter.notifyDataSetChanged();
    }

    private void showError(String errorText) {
        String message = getString(R.string.error) + " " + errorText;
        Snackbar snackbar = Snackbar.make(postsView, message, ERROR_SHOW_TIME);
        snackbar.setAction(R.string.ok, view -> snackbar.dismiss());
        snackbar.show();
    }
}