package com.example.newsgateway;

import android.net.Uri;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class NewsArticleDownloaderAsyncTask extends AsyncTask<String, Integer, String> {
    String id;
    String apiKey = "43c03b3b7f2349c498e20baeadf2b3c2";
    Uri.Builder builder = null;
    NewsService newsService;
    StringBuilder stringBuilder;
    boolean noData = false;
    boolean isNoData = true;
    ArrayList<Article> articles = new ArrayList<>();

    public NewsArticleDownloaderAsyncTask(NewsService newsService, String id){
        this.id = id;
        this.newsService = newsService;
    }

    @Override
    protected String doInBackground(String... strings){
        String url = "https://newsapi.org/v2/top-headlines?sources=" + id + "&language=en&apiKey=" + apiKey;
        builder = Uri.parse(url).buildUpon();
        API();
        if(!isNoData){
            parseJSON(stringBuilder.toString());
        }
        return null;
    }

    public void API(){
        String urlBuild = builder.build().toString();
        stringBuilder = new StringBuilder();
        try{
            URL url = new URL(urlBuild);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            if(con.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND){
                noData = true;
            }else{
                con.setRequestMethod("GET");
                InputStream inputStream = con.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while((line = bufferedReader.readLine()) != null){
                    stringBuilder.append(line).append("\n");
                }
                isNoData = false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void parseJSON(String string){
        try{
            if(!noData){
                JSONObject jsonObject = new JSONObject(string);
                JSONArray jsonArray = jsonObject.getJSONArray("articles");
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject j = (JSONObject) jsonArray.get(i);
                    Article article = new Article();
                    article.setAuthor(j.getString("author"));
                    article.setDate(j.getString("publishedAt"));
                    article.setDescription(j.getString("description"));
                    article.setArticleUrl(j.getString("url"));
                    article.setImageUrl(j.getString("urlToImage"));
                    article.setTitle(j.getString("title"));
                    articles.add(article);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(String string){
        super.onPostExecute(string);
        newsService.setArticles(articles);
    }

}
