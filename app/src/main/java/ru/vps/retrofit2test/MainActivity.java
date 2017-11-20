package ru.vps.retrofit2test;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.vps.retrofit2test.model.Post;

public class MainActivity extends AppCompatActivity implements ClickablePostRecyclerAdapter.OnItemClickListener {
    private static final String EXTRA_POSTS = "ru.vps.retrofit2test.extra.posts";
    private static final int MESSAGE_SHOW_TIME = 10000;
    //
    private List<Post> posts = new ArrayList<>();
    //
    private RecyclerView postsView;

    @Override
    public void onItemClick(View view, int position) {
        Post post = posts.get(position);
        String postUrl = App.getUmoriliUrl() + post.getLink();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(postUrl));
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        postsView = (RecyclerView) findViewById(R.id.posts);
        postsView.setHasFixedSize(true);
        postsView.setLayoutManager(new LinearLayoutManager(this));
        postsView.setAdapter(new ClickablePostRecyclerAdapter(posts, this));

        final Button btnGetPosts = (Button) findViewById(R.id.btnGetPosts);
        btnGetPosts.setOnClickListener(new View.OnClickListener() {
            private static final int NUMBER_OF__POSTS = 15;

            @Override
            public void onClick(final View v) {
                App.getUmoriliApi().getRandomPosts(NUMBER_OF__POSTS).enqueue(new Callback<List<Post>>() {
                    @Override
                    public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                        if (!response.isSuccessful()) {
                            return;
                        }

                        List<Post> body = response.body();
                        if (body != null) {
                            posts.clear();
                            posts.addAll(body);

                            postsView.setAdapter(new ClickablePostRecyclerAdapter(posts, MainActivity.this));
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Post>> call, Throwable t) {
                        final Snackbar snackbar = Snackbar.make(v, t.getLocalizedMessage(), MESSAGE_SHOW_TIME);
                        snackbar.setAction(R.string.ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                snackbar.dismiss();
                            }
                        });
                        snackbar.show();
                    }
                });
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(EXTRA_POSTS, (Serializable) posts);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        posts = (List<Post>) savedInstanceState.getSerializable(EXTRA_POSTS);
        postsView.setAdapter(new ClickablePostRecyclerAdapter(posts, this));
    }
}
