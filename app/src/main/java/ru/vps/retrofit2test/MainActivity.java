package ru.vps.retrofit2test;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.vps.retrofit2test.model.Post;

public class MainActivity extends AppCompatActivity
{
   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      postsView = (ListView) findViewById(R.id.posts);
      postsView.setAdapter(new PostsViewAdapter(posts));
      postsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
         @Override
         public void onItemClick(AdapterView<?> parent, View view, int position, long id)
         {
            Post post = posts.get(position);
            String postUrl = App.getUmoriliUrl() + post.getLink();
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(postUrl));
            startActivity(intent);
         }
      });

      final Button btnGetPosts = (Button) findViewById(R.id.btnGetPosts);
      btnGetPosts.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(final View v)
         {
            App.getUmoriliApi().getRandomPosts().enqueue(new Callback<List<Post>>() {
               @Override
               public void onResponse(Call<List<Post>> call, Response<List<Post>> response)
               {
                  if(!response.isSuccessful())
                  {
                     return;
                  }

                  List<Post> body = response.body();
                  if(body != null)
                  {
                     posts.clear();
                     posts.addAll(body);

                     postsView.setAdapter(new PostsViewAdapter(posts));
                  }
               }

               @Override
               public void onFailure(Call<List<Post>> call, Throwable t)
               {
                  final Snackbar snackbar = Snackbar.make(v, t.getLocalizedMessage(), MESSAGE_SHOW_TIME);
                  snackbar.setAction(R.string.ok, new View.OnClickListener() {
                     @Override
                     public void onClick(View v)
                     {
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
   protected void onSaveInstanceState(Bundle outState)
   {
      super.onSaveInstanceState(outState);

      outState.putSerializable(EXTRA_POSTS, (Serializable) posts);
   }

   @Override
   protected void onRestoreInstanceState(Bundle savedInstanceState)
   {
      super.onRestoreInstanceState(savedInstanceState);

      posts = (List<Post>) savedInstanceState.getSerializable(EXTRA_POSTS);
      postsView.setAdapter(new PostsViewAdapter(posts));
   }

   private static final String EXTRA_POSTS = "ru.vps.retrofit2test.extra.posts";
   private static final int MESSAGE_SHOW_TIME = 10000;
   //
   private List<Post> posts = new ArrayList<>();
   //
   private ListView postsView;
   private PostsViewAdapter postsViewAdapter;



   private class PostsViewAdapter extends BaseAdapter
   {
      public PostsViewAdapter(@NonNull List<Post> posts)
      {
         this.posts = posts;
      }

      @Override
      public int getCount()
      {
         return posts.size();
      }

      @Override
      public Object getItem(int position)
      {
         return posts.get(position);
      }

      @Override
      public long getItemId(int position)
      {
         return position;
      }

      @Override
      public View getView(int position, View convertView, ViewGroup parent)
      {
         PostItemViewHolder postItemViewHolder;

         if (convertView == null)
         {
            convertView = getLayoutInflater().inflate(R.layout.post_item, parent, false);
            postItemViewHolder = new PostItemViewHolder(convertView);
            convertView.setTag(postItemViewHolder);
         }
         else
         {
            postItemViewHolder = (PostItemViewHolder) convertView.getTag();
         }

         Post post = (Post) getItem(position);

         TextView textView = postItemViewHolder.getTextView();
         textView.setText(Html.fromHtml(post.getElementPureHtml()));

         return convertView;
      }

      private List<Post> posts;



      private class PostItemViewHolder
      {
         PostItemViewHolder(View view)
         {
            textView = (TextView) view.findViewById(R.id.text);
         }

         TextView getTextView()
         {
            return textView;
         }

         final TextView textView;
      }
   }
}
