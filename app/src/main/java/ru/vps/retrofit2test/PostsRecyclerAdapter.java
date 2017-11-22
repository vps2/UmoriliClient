package ru.vps.retrofit2test;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
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
        String elementPureHtml = posts.get(position).getElementPureHtml();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.setPostText(Html.fromHtml(elementPureHtml, Html.FROM_HTML_MODE_LEGACY).toString());
        } else {
            holder.setPostText(Html.fromHtml(elementPureHtml).toString());
        }

        String postLink = posts.get(position).getLink();
        if (postLink != null) {
            holder.setPostLink(postLink);
            holder.setLinkViewVisibility(View.VISIBLE);
        } else {
            holder.setPostLink(null);
            holder.setLinkViewVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        private final Context context;
        private final TextView postText;
        private final TextView postLink;

        public PostViewHolder(View view) {
            super(view);

            context = view.getContext();

            postText = (TextView) view.findViewById(R.id.postText);
            postLink = (TextView) view.findViewById(R.id.postLink);
        }

        public void setPostText(String text) {
            postText.setText(text);
        }

        public void setPostLink(String url) {
            if (URLUtil.isValidUrl(url)) {
                postLink.setMovementMethod(LinkMovementMethod.getInstance());

                String text = context.getString(R.string.link, url);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    postLink.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY));
                } else {
                    postLink.setText(Html.fromHtml(text));
                }
            } else {
                postLink.setMovementMethod(null);

                String text = context.getString(R.string.wrong_url, url);
                postLink.setText(text);
            }
        }

        public void setLinkViewVisibility(int flag) {
            postLink.setVisibility(flag);
        }
    }
}
