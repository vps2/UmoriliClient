package ru.vps.retrofit2test;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
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

      postsView = (TextView) findViewById(R.id.posts);

      final Button btnGetPosts = (Button) findViewById(R.id.btnGetPosts);
      btnGetPosts.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(final View v)
         {
            App.getApi().getPosts("bash", 10).enqueue(new Callback<List<Post>>() {
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
                     posts.addAll(body);
                     fillPostsView();
                  }
               }

               @Override
               public void onFailure(Call<List<Post>> call, Throwable t)
               {
                  final Snackbar snackbar = Snackbar.make(v, t.getLocalizedMessage(), MESSAGE_SHOW_TIME);
                  snackbar.setAction("OK", new View.OnClickListener() {
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
      fillPostsView();
   }

   private void fillPostsView()
   {
      String postsAsString = convertPostsToString();

      postsView.clearComposingText();
      postsView.setText(postsAsString);
   }

   private String convertPostsToString()
   {
      StringBuilder sb = new StringBuilder();
      for(Post post : posts)
      {
         sb.append(Html.fromHtml(post.getElementPureHtml()))
           .append("\n")
           .append(post.getLink())
           .append("\n");
      }

      return sb.toString();
   }

   private static final String EXTRA_POSTS = "ru.vps.retrofit2test.extra.posts";
   private static final int MESSAGE_SHOW_TIME = 10000;
   //
   private List<Post> posts = new ArrayList<>();
   //
   private TextView postsView;
}
