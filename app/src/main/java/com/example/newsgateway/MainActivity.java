package com.example.newsgateway;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";
    DrawerLayout drawerLayout;
    ListView drawerList;
    ActionBarDrawerToggle drawerToggle;
    boolean serviceRunning = false;
    ArrayList<String> sourceList = new ArrayList<String>();
    ArrayList<String> categoryList = new ArrayList<String>();
    ArrayList<Source> sources = new ArrayList<Source>();
    ArrayList<Article> articles = new ArrayList<Article>();
    HashMap<String, Source> sourceHashMap = new HashMap<>();
    ArrayList<Drawer> drawers = new ArrayList<>();
    Menu menu;
    private NewsReceiver newsReceiver;
    String curSource;
    DrawerAdapter drawerAdapter;
    PageAdapter pageAdapter;
    List<Fragment> fragmentList;
    ViewPager viewPager;
    boolean flag;
    int curSourcePointer;
    static final String ACTION_MSG_TO_SERVICE = "ACTION_MSG_TO_SERVICE";
    static final String ACTION_NEWS_STORY = "ACTION_NEWS_STORY";
    static final String ARTICLE_LIST = "ARTICLE_LIST";
    static final String SOURCE_ID = "SOURCE_ID";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if(!serviceRunning && savedInstanceState == null){
            Intent intent = new Intent(MainActivity.this, NewsService.class);
            startService(intent);
            serviceRunning = true;
        }

        newsReceiver = new NewsReceiver();
        IntentFilter intentFilter = new IntentFilter(MainActivity.ACTION_NEWS_STORY);
        registerReceiver(newsReceiver, intentFilter);
        drawerLayout = findViewById(R.id.drawerLayout);
        drawerList = findViewById(R.id.drawer);
        drawerAdapter = new DrawerAdapter(this, drawers);
        drawerList.setAdapter(drawerAdapter);

        drawerList.setOnItemClickListener(
                new ListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                        Log.d(TAG, "onItemClick: Source selected");
                        viewPager.setBackground(null);
                        curSourcePointer = pos;
                        curSource = sourceList.get(pos);
                        Intent intent = new Intent(MainActivity.ACTION_MSG_TO_SERVICE);
                        intent.putExtra(SOURCE_ID, curSource);
                        sendBroadcast(intent);
                        drawerLayout.closeDrawer(drawerList);
                        Log.d(TAG, "onItemClick: Finished " + curSource);
                    }
                }
        );


        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        fragmentList = new ArrayList<>();
        pageAdapter = new PageAdapter(getSupportFragmentManager());
        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(pageAdapter);

        if(sourceHashMap.isEmpty() && savedInstanceState == null){
            new NewsSourceDownloaderAsyncTask(this, "").execute();
        }

        Log.d(TAG, "onCreate: Finish");
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
        Log.d(TAG, "onPostCreate: Finish");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu m){
        getMenuInflater().inflate(R.menu.action_menu, m);
        menu = m;
        if(flag){
            for(String s : categoryList){
                menu.add(s);
            }
        }
        Log.d(TAG, "onCreateOptionsMenu: Finish");
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        if(drawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        new NewsSourceDownloaderAsyncTask(this, item.getTitle().toString()).execute();
        colorMenu(item);
        drawerLayout.openDrawer(drawerList);
        Log.d(TAG, "onOptionsItemSelected: Finish");
        return super.onOptionsItemSelected(item);
    }

    private void colorMenu(MenuItem item){
        switch (item.getTitle().toString()){
            case "general":
                setColor(item, Color.YELLOW);
                break;

            case "sports":
                setColor(item, Color.BLUE);
                break;
            case "business":
                setColor(item, Color.GREEN);
                break;
            case "entertainment":
                setColor(item, Color.RED);
                break;

            case "science":
                setColor(item, Color.CYAN);
                break;

            case "health":
                setColor(item, Color.MAGENTA);
                break;

            case "technology":
                setColor(item, Color.LTGRAY);
                break;



        }
    }

    private void setColor(MenuItem item, int color){
        SpannableString spannableString = new SpannableString(item.getTitle());
        spannableString.setSpan(new ForegroundColorSpan(color), 0, spannableString.length(), 0);
        item.setTitle(spannableString);
    }

    public void setSources(ArrayList<Source> sList, ArrayList<String> cList){
        sourceHashMap.clear();
        sourceList.clear();
        sources.clear();
        drawers.clear();
        sources.addAll(sList);
        for (int i = 0; i < sList.size(); i++){
            sourceList.add(sList.get(i).getName());
            sourceHashMap.put(sList.get(i).getName(), sList.get(i));
        }
        categoryList.clear();
        categoryList = cList;
        if(menu != null) {
            menu.add("all");
            Collections.sort(cList);
            for (String s : cList) {
                menu.add(s);
            }
            for (int i = 0; i < menu.size(); i++) {
                colorMenu(menu.getItem(i));
            }
        }


        for (Source s : sList){
            Drawer drawer = new Drawer();
            switch (s.getCatagory()){
                case "general":
                    drawer.setColor(Color.YELLOW);
                    drawer.setName(s.getName());
                    drawers.add(drawer);
                    break;
                case "sports":
                    drawer.setColor(Color.BLUE);
                    drawer.setName(s.getName());
                    drawers.add(drawer);
                    break;
                case "business":
                    drawer.setColor(Color.GREEN);
                    drawer.setName(s.getName());
                    drawers.add(drawer);
                    break;
                case "entertainment":
                    drawer.setColor(Color.RED);
                    drawer.setName(s.getName());
                    drawers.add(drawer);
                    break;
                case "science":
                    drawer.setColor(Color.CYAN);
                    drawer.setName(s.getName());
                    drawers.add(drawer);
                    break;
                case "health":
                    drawer.setColor(Color.MAGENTA);
                    drawer.setName(s.getName());
                    drawers.add(drawer);
                    break;
                case "technology":
                    drawer.setColor(Color.LTGRAY);
                    drawer.setName(s.getName());
                    drawers.add(drawer);
                    break;
            }
        }
        drawerAdapter.notifyDataSetChanged();
    }

    private void setFragments(ArrayList<Article> arts){
        setTitle(curSource);
        for(int i =  0; i < pageAdapter.getCount(); i++){
            pageAdapter.positionChanged(i);
        }
        fragmentList.clear();
        for(int i = 0; i< arts.size(); i++){
            Article a = arts.get(i);
            fragmentList.add(NewsFragment.newInstance(a, i, arts.size()));
        }
        pageAdapter.notifyDataSetChanged();
        viewPager.setCurrentItem(0);
        articles = arts;
        Log.d(TAG, "setFragments: Finish");
    }

    @Override
    protected void onDestroy(){
        try{
            unregisterReceiver(newsReceiver);
        }catch (Exception e){
            e.printStackTrace();
        }
        Intent intent = new Intent(MainActivity.this, NewsReceiver.class);
        stopService(intent);
        super.onDestroy();
    }

    @Override
    protected void onStop(){
        unregisterReceiver(newsReceiver);
        super.onStop();
    }

    @Override
    protected void onResume(){
        IntentFilter intentFilter = new IntentFilter(MainActivity.ACTION_NEWS_STORY);
        registerReceiver(newsReceiver, intentFilter);
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outSave){
        RestoreLayout restoreLayout = new RestoreLayout();
        restoreLayout.setCategories(categoryList);
        restoreLayout.setArticles(articles);
        restoreLayout.setSources(sources);
        restoreLayout.setCurArticle(viewPager.getCurrentItem());
        restoreLayout.setCurSource(curSourcePointer);
        outSave.putSerializable("state", restoreLayout);
        super.onSaveInstanceState(outSave);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        RestoreLayout restoreLayout = (RestoreLayout) savedInstanceState.getSerializable("state");
        flag = true;
        //getMenuInflater().inflate(R.menu.action_menu, menu);
        articles = restoreLayout != null ? restoreLayout.getArticles() : null;
        categoryList = restoreLayout != null ? restoreLayout.getCategories() : null;
        sources = restoreLayout != null ? restoreLayout.getSources() : null;
        curSourcePointer = restoreLayout != null ? restoreLayout.getCurSource() : null;
        curSource = sourceList.size() != 0 ? sourceList.get(curSourcePointer) : null;

        if(restoreLayout != null){
            setSources(sources, categoryList);
            setFragments(articles);
        }
        for(int i = 0; i < (sources != null ? sources.size() : 0); i++){
            sourceList.add(sources.get(i).getName());
            sourceHashMap.put(sources.get(i).getName(), sources.get(i));
        }

        if(curSource == null) {
            setTitle("News Gateway");
        }else{
            setTitle(curSource);
            Intent intent = new Intent(MainActivity.ACTION_MSG_TO_SERVICE);
            intent.putExtra(SOURCE_ID, curSource);
            sendBroadcast(intent);
            drawerLayout.closeDrawer(drawerList);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfiguration){
        super.onConfigurationChanged(newConfiguration);
        drawerToggle.onConfigurationChanged(newConfiguration);
    }

    class NewsReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent){
            switch (intent.getAction()){
                case ACTION_NEWS_STORY:
                    ArrayList<Article> arrayList;
                    if(intent.hasExtra(ARTICLE_LIST)){
                        arrayList = (ArrayList<Article>) intent.getSerializableExtra(ARTICLE_LIST);
                        setFragments(arrayList);
                    }
                    break;
            }
        }
    }

    private class PageAdapter extends FragmentPagerAdapter{
        private long id = 0;

        public PageAdapter(FragmentManager fragmentManager){
            super(fragmentManager);
        }

        @Override
        public int getItemPosition(@NonNull Object object){
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int pos){
            return fragmentList.get(pos);
        }

        @Override
        public int getCount(){
            return fragmentList.size();
        }

        @Override
        public long getItemId(int pos){
            return id + pos;
        }

        public void positionChanged(int n){
            id += getCount() + n;
        }


    }
}
