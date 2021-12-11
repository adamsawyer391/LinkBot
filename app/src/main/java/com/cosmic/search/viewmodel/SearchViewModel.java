package com.cosmic.search.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cosmic.model.Link;
import com.cosmic.room.model.SearchTerm;
import com.cosmic.search.repository.SearchRepository;

import java.util.ArrayList;

public class SearchViewModel extends ViewModel {

    MutableLiveData<ArrayList<Link>> searchTerms;

    public void connectToSearchViewModel(Context context, String search_query){
        searchTerms = SearchRepository.getInstance(context, search_query).getSearchResults();
    }

    public LiveData<ArrayList<Link>> getSearchResults(){
        return searchTerms;
    }
}
