package com.cosmic.model;

import java.io.Serializable;

public class Category implements Serializable{

    private String name, url, key;

    public Category() {

    }

    public Category(String name, String url, String key) {
        this.name = name;
        this.url = url;
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getKey() {
        return key;
    }
}
