package ru.vps.retrofit2test;

import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ru.vps.retrofit2test.model.Post;

public class ClickablePostRecyclerAdapter extends PostsRecyclerAdapter implements View.OnClickListener
{
   public ClickablePostRecyclerAdapter(List<Post> posts, OnItemClickListener listener)
   {
      super(posts);

      this.listener = listener;
   }

   @Override
   public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
   {
      PostViewHolder postViewHolder = super.onCreateViewHolder(parent, viewType);
      postViewHolder.itemView.setOnClickListener(this);

      return postViewHolder;
   }

   @Override
   public void onBindViewHolder(PostViewHolder holder, int position)
   {
      super.onBindViewHolder(holder, position);
      holder.itemView.setTag(position);
   }

   @Override
   public void onClick(View v)
   {
      Integer position = (Integer) v.getTag();
      listener.onItemClick(v, position);
   }

   private OnItemClickListener listener;



   public interface OnItemClickListener
   {
      void onItemClick(View view, int position);
   }
}
