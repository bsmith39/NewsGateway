package com.example.newsgateway;

import java.io.Serializable;

public class Source implements Serializable {
    String id, url, name, catagory;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getCatagory() {
        return catagory;
    }

    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public void setCatagory(String catagory) {
        this.catagory = catagory;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUrl(String url) {
        this.url = url;
    }


}
