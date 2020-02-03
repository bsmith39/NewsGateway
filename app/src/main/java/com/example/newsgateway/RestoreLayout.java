package com.example.newsgateway;

import java.io.Serializable;
import java.util.ArrayList;

public class RestoreLayout implements Serializable {

    ArrayList<Source> sources = new ArrayList<Source>();
    ArrayList<Article> articles = new ArrayList<Article>();
    ArrayList<String> categories = new ArrayList<String>();
    int curSource, curArticle;

    public ArrayList<Article> getArticles() {
        return articles;
    }

    public ArrayList<Source> getSources() {
        return sources;
    }

    public ArrayList<String> getCategories() {
        return categories;
    }

    public void setArticles(ArrayList<Article> articles) {
        this.articles = articles;
    }

    public void setCategories(ArrayList<String> categories) {
        this.categories = categories;
    }

    public void setSources(ArrayList<Source> sources) {
        this.sources = sources;
    }

    public int getCurArticle() {
        return curArticle;
    }

    public int getCurSource() {
        return curSource;
    }

    public void setCurArticle(int curArticle) {
        this.curArticle = curArticle;
    }

    public void setCurSource(int curSource) {
        this.curSource = curSource;
    }
}
