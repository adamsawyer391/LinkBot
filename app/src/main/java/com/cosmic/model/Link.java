package com.cosmic.model;

import android.os.Parcelable;

import java.io.Serializable;

public class Link implements Serializable {

    private String url, key, date_added, description, title, category_key, category_name;
    private long timestamp;

    public Link() {

    }

    public Link(String url, String key, String date_added, String description, String title, String category_key, String category_name, long timestamp) {
        this.url = url;
        this.key = key;
        this.date_added = date_added;
        this.description = description;
        this.title = title;
        this.category_key = category_key;
        this.category_name = category_name;
        this.timestamp = timestamp;
    }

    public String getUrl() {
        return url;
    }

    public String getKey() {
        return key;
    }

    public String getDate_added() {
        return date_added;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory_key() {
        return category_key;
    }

    public String getCategory_name() {
        return category_name;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setDate_added(String date_added) {
        this.date_added = date_added;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCategory_key(String category_key) {
        this.category_key = category_key;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
