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

public class PostsRecyclerAdapter extends RecyclerView.Adapter<PostsRecyclerAdapter.PostViewHolder>
{
   public PostsRecyclerAdapter(List<Post> posts)
   {
      this.posts = posts;
   }

   @Override
   public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
   {
      LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
      View postView = layoutInflater.inflate(R.layout.post_item, parent, false);

      return new PostViewHolder(postView);
   }

   @Override
   public void onBindViewHolder(PostViewHolder holder, int position)
   {
      if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
      {
         holder.textView.setText(Html.fromHtml(posts.get(position).getElementPureHtml(), Html.FROM_HTML_MODE_LEGACY));
      }
      else
      {
         holder.textView.setText(Html.fromHtml(posts.get(position).getElementPureHtml()));
      }
   }

   @Override
   public int getItemCount()
   {
      return posts.size();
   }

   private List<Post> posts;



   public static class PostViewHolder extends RecyclerView.ViewHolder
   {
      public PostViewHolder(View view)
      {
         super(view);

         textView = (TextView) view.findViewById(R.id.text);
      }

      public final TextView textView;
   }
}
