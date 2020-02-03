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

import javax.xml.transform.SourceLocator;

public class NewsSourceDownloaderAsyncTask extends AsyncTask<String, Integer, String> {

    StringBuilder stringBuilder;
    MainActivity mainActivity;
    String category;
    Uri.Builder builder = null;
    ArrayList<Source> sources = new ArrayList<>();
    ArrayList<String> categoryList = new ArrayList<>();
    String ApiKey = "43c03b3b7f2349c498e20baeadf2b3c2";
    String NewsUrl;
    boolean noData = false;
    boolean isNoData = true;


    public NewsSourceDownloaderAsyncTask(MainActivity ma, String category){
        mainActivity = ma;
        if(category.equalsIgnoreCase("all") || category.equalsIgnoreCase("")){
            this.category = "";
            NewsUrl = "https://newsapi.org/v2/sources?language=en&country=us&category=&apiKey=" + ApiKey;
        }else{
            this.category = category;
            NewsUrl = "https://newsapi.org/v2/sources?language=en&country=us&category=" + this.category + "&apiKey=" + ApiKey;
        }

    }

    @Override
    protected String doInBackground(String... strings){
        builder = Uri.parse(NewsUrl).buildUpon();
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
                JSONArray jsonArray = jsonObject.getJSONArray("sources");
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject j = (JSONObject) jsonArray.get(i);
                    Source source = new Source();
                    source.setCatagory(j.getString("category"));
                    source.setId(j.getString("id"));
                    source.setName(j.getString("name"));
                    source.setUrl(j.getString("url"));
                    sources.add(source);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(String string){
        super.onPostExecute(string);
        for(int i = 0; i < sources.size(); i++){
            String cat = sources.get(i).getCatagory();
            if(!categoryList.contains(cat)){
                categoryList.add(cat);
            }
        }
        mainActivity.setSources(sources, categoryList);
    }
}
