package com.cosmic.room.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.room.Room;

import com.cosmic.room.db.SearchDatabase;
import com.cosmic.room.model.SearchTerm;

import java.util.List;

public class SearchViewModel extends AndroidViewModel {

    private SearchDatabase searchDatabase;
    private LiveData<List<SearchTerm>> all_searches;

    public SearchViewModel(@NonNull Application application) {
        super(application);
        searchDatabase = Room.databaseBuilder(application, SearchDatabase.class, "searches.db").fallbackToDestructiveMigration().build();
    }

    public LiveData<List<SearchTerm>> getAll_searches(){
        return all_searches;
    }
}
