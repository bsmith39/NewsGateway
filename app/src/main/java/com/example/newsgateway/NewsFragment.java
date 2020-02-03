package com.example.newsgateway;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.squareup.picasso.Picasso;

public class NewsFragment extends Fragment {
    private static final String TAG = "NewsFragment";
    TextView headline, date, author, description, indexView;
    ImageView thumbnail;
    Article article;
    View view;
    int total, index;
    Bitmap bitmap;

    public static final NewsFragment newInstance(Article article, int index, int total){
        NewsFragment newsFragment = new NewsFragment();
        Bundle bundle = new Bundle(1);
        bundle.putSerializable("ARTICLE", article);
        bundle.putInt("INDEX", index);
        bundle.putInt("TOTAL", total);
        newsFragment.setArguments(bundle);
        return newsFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState){
        article = (Article) (getArguments() != null ? getArguments().getSerializable("ARTICLE") : null);
        index = getArguments().getInt("INDEX") + 1;
        total = getArguments().getInt("TOTAL");
        String indexText = index + " of " + total;
        view = inflater.inflate(R.layout.news_fragment,viewGroup, false);
        headline = view.findViewById(R.id.headline);
        date = view.findViewById(R.id.date);
        author = view.findViewById(R.id.author);
        description = view.findViewById(R.id.description);
        indexView = view.findViewById(R.id.index);
        thumbnail = view.findViewById(R.id.thumbnail);
        indexView.setText(indexText);
        if(article.getTitle() != null){
            headline.setText(article.getTitle());
        }else{
            headline.setText("");
        }

        if(article.getDate() != null){
            String d = article.getDate();
            Date dateDate = null;
            String realDate;
            try{
                if(d != null){
                    dateDate = new SimpleDateFormat("yyy-MM-dd'T'HH:mm:ss").parse(d);
                }
                String pattern = "MM dd, yyyy HH:mm";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                realDate = simpleDateFormat.format(dateDate);
                this.date.setText(realDate);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        if(article.getDescription() != null){
            description.setText(article.getDescription());
        }else{
            description.setText("");
        }

        if(article.getAuthor() != null){
            author.setText(article.getAuthor());
        }else{
            author.setText("");
        }

        if(article.getImageUrl() != null){
            imageUrl(article.getImageUrl());
        }

        headline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToArticle();
            }
        });

        author.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToArticle();
            }
        });

        thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToArticle();
            }
        });
        return view;

    }

    private void goToArticle(){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse(article.getArticleUrl()));
        startActivity(intent);
    }

    private void imageUrl(final String url){
        if(url != null){
           Picasso picasso = new Picasso.Builder(getActivity()).listener(new Picasso.Listener() {
               @Override
               public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                   String newUrl = url.replace("http", "https");
                   picasso.load(newUrl).fit().centerCrop().error(R.drawable.no_image).placeholder(R.drawable.placeholder).into(thumbnail);

               }
           }).build();
           picasso.load(url).fit().centerCrop().error(R.drawable.no_image).placeholder(R.drawable.placeholder).into(thumbnail);


          /*  try{

                URL u = new URL(url);
                HttpURLConnection con = (HttpURLConnection) u.openConnection();
                con.setRequestMethod("GET");
                con.connect();
                InputStream inputStream = null;
                if(con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    inputStream = con.getInputStream();
                }
                bitmap = BitmapFactory.decodeStream(inputStream);


                //InputStream inputStream = new java.net.URL(article.getImageUrl()).openStream();
                //bitmap = BitmapFactory.decodeStream(inputStream);
                Log.d(TAG, "imageUrl: Sucess");
            }catch (Exception e){
                e.printStackTrace();
                Log.d(TAG, "imageUrl: Failed");
            }*/
            thumbnail.setImageBitmap(bitmap);
        }else{
            Picasso picasso2 = new Picasso.Builder(getActivity()).listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                    String newUrl = url.replace("http", "https");
                    picasso.load(newUrl).fit().centerCrop().error(R.drawable.no_image).placeholder(R.drawable.placeholder).into(thumbnail);

                }
            }).build();

        }
    }
}
