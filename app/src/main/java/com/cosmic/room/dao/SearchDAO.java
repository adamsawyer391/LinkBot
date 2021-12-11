package com.cosmic.room.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.cosmic.room.model.SearchTerm;

import java.util.List;

@Dao
public interface SearchDAO {

    @Query("SELECT * FROM searches")
    List<SearchTerm> getAllSearchTerms();

    /**
     * This worked reasonably will when combined with an edittext, textwatcher and
     * a recycler view. But I opted to go with an autocomplete text view and won't be
     * using this method any more
     */
    @Query("SELECT * FROM searches WHERE term LIKE :word||'%%' ")
    List<SearchTerm> getSelected(String word);

    @Insert
    void insertAll(List<SearchTerm> searchTerms);

    @Insert
    void insert(SearchTerm searchTerm);

    @Update
    void update(SearchTerm searchTerm);

    @Delete
    void delete(SearchTerm searchTerm);

}
