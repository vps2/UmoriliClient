package ru.vps.retrofit2test;

import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ru.vps.retrofit2test.model.Post;

public class PostsRecyclerAdapter extends RecyclerView.Adapter<PostsRecyclerAdapter.PostViewHolder> {
    private List<Post> posts;

    public PostsRecyclerAdapter(List<Post> posts) {
        this.posts = posts;
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View postView = layoutInflater.inflate(R.layout.post_item, parent, false);

        return new PostViewHolder(postView);
    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.setText(Html.fromHtml(posts.get(position).getElementPureHtml(), Html.FROM_HTML_MODE_LEGACY).toString());
        } else {
            holder.setText(Html.fromHtml(posts.get(position).getElementPureHtml()).toString());
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        public PostViewHolder(View view) {
            super(view);

            textView = (TextView) view.findViewById(R.id.text);
        }

        public void setText(String text) {
            textView.setText(text);
        }
    }
}
