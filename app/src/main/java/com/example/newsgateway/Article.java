package com.example.newsgateway;

import java.io.Serializable;

public class Article implements Serializable {
    String title, date, author, imageUrl, description, articleUrl;

    public Article(){
        this.title = "";
        this.date = "";
        this.author = "";
        this.imageUrl = "";
        this.description = "";
        this.articleUrl = "";
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public String getArticleUrl() {
        return articleUrl;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setArticleUrl(String articleUrl) {
        this.articleUrl = articleUrl;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void serTitle(String title){
        this.title = title;
    }
}
