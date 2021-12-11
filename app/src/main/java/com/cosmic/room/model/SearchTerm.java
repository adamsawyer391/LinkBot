package com.cosmic.room.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "searches")
public class SearchTerm {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "term")
    public String search_term;

    public SearchTerm() {

    }

    public SearchTerm(int id, String search_term) {
        this.id = id;
        this.search_term = search_term;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSearch_term() {
        return search_term;
    }

    public void setSearch_term(String search_term) {
        this.search_term = search_term;
    }

    @Override
    public String toString() {
        return "SearchTerm{" +
                "id=" + id +
                ", search_term='" + search_term + '\'' +
                '}';
    }
}
