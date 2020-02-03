package com.example.newsgateway;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;

public class NewsService extends Service {

    private static final String TAG = "NewsService";
    private boolean running = true;
    private ServiceReceiver serviceReceiver;
    private final ArrayList<Article> articles = new ArrayList<>();

    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int id){

        Log.d(TAG, "onStartCommand: Started");
        serviceReceiver = new ServiceReceiver();
        IntentFilter intentFilter = new IntentFilter(MainActivity.ACTION_MSG_TO_SERVICE);
        registerReceiver(serviceReceiver, intentFilter);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(running){
                    while(articles.isEmpty()){
                        try{
                            Thread.sleep(250);
                        }catch(Exception e){
                        }
                    }
                    Log.d(TAG, "run: Has Articles");
                    Intent intent = new Intent();
                    intent.setAction(MainActivity.ACTION_NEWS_STORY);
                    intent.putExtra(MainActivity.ARTICLE_LIST, articles);
                    sendBroadcast(intent);
                    articles.clear();
                }
            }
        }).start();
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(serviceReceiver);
        running = false;
        super.onDestroy();

    }

    public void setArticles(ArrayList<Article> arts){
        articles.clear();
        articles.addAll(arts);
    }

    class ServiceReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent){
            Log.d(TAG, "onReceive: Start");
            switch(intent.getAction()){
                case MainActivity.ACTION_MSG_TO_SERVICE:
                    String id = "";
                    String retId = "";
                    if(intent.hasExtra(MainActivity.SOURCE_ID)){
                        id = intent.getStringExtra(MainActivity.SOURCE_ID);
                        retId = id.replaceAll(" ", "-");
                    }
                    new NewsArticleDownloaderAsyncTask(NewsService.this, retId).execute();
                    break;
            }
        }
    }

}
