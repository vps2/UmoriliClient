package ru.vps.retrofit2test.fragment;

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
import java.util.List;

import ru.vps.retrofit2test.PostsRecyclerAdapter;
import ru.vps.retrofit2test.R;
import ru.vps.retrofit2test.model.Post;
import ru.vps.retrofit2test.model.loader.PostsLoader;
import ru.vps.retrofit2test.model.loader.result.Result;

public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Result<List<Post>>> {
    private static final int POSTS_LOADER_ID = 1;
    private static final int MESSAGE_SHOW_TIME = 10000;
    private static final String EXTRA_LOADING = "ru.vps.retrofit2test.fragment.main_fragment.loading";
    private static final String EXTRA_CACHED_POSTS = "ru.vps.retrofit2test.fragment.main_fragment.cached_posts";
    //
    private RecyclerView postsView;
    private SwipeRefreshLayout swipeRefreshLayout;
    //
    private List<Post> cachedPosts;
    private Loader<Result<List<Post>>> postsLoader;
    private boolean loading = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        postsView = (RecyclerView) rootView.findViewById(R.id.posts);
        postsView.setHasFixedSize(true);
        postsView.setLayoutManager(new LinearLayoutManager(getContext()));
        postsView.setAdapter(new PostsRecyclerAdapter(new ArrayList<>()));

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(this::getPostsAsync);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);

        postsLoader = getLoaderManager().initLoader(POSTS_LOADER_ID, Bundle.EMPTY, this);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null) {
            loading = savedInstanceState.getBoolean(EXTRA_LOADING);
            cachedPosts = (List<Post>) savedInstanceState.getSerializable(EXTRA_CACHED_POSTS);
        }

        if (loading) {
            swipeRefreshLayout.setRefreshing(true);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(EXTRA_LOADING, loading);
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
                if (!swipeRefreshLayout.isRefreshing()) {
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

        PostsRecyclerAdapter postsRecyclerAdapter = null;

        if (!data.isUsed()) {
            loading = false;
            swipeRefreshLayout.setRefreshing(false);

            try {
                List<Post> temp = data.get();
                cachedPosts = temp;
                postsRecyclerAdapter = new PostsRecyclerAdapter(cachedPosts);
                postsView.setAdapter(postsRecyclerAdapter);
            } catch (Throwable ex) {
                String message = getString(R.string.error) + " " + ex.getLocalizedMessage();
                Snackbar snackbar = Snackbar.make(postsView, message, MESSAGE_SHOW_TIME);
                snackbar.setAction(R.string.ok, view -> snackbar.dismiss());
                snackbar.show();
            }
        } else {
            postsRecyclerAdapter = new PostsRecyclerAdapter(cachedPosts);
            postsView.setAdapter(postsRecyclerAdapter);
        }
    }

    @Override
    public void onLoaderReset(Loader<Result<List<Post>>> loader) {
        if (loader.getId() == POSTS_LOADER_ID) {
            cachedPosts.clear();
            cachedPosts = null;
        }
    }

    private void getPostsAsync() {
        loading = true;
        postsLoader.forceLoad();
    }
}