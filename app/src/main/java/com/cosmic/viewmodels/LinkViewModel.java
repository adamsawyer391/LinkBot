package com.cosmic.viewmodels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cosmic.model.Link;
import com.cosmic.repositories.LinkRepository;

import java.util.ArrayList;

public class LinkViewModel extends ViewModel {

    public MutableLiveData<ArrayList<Link>> listMutableLiveData;

    public void connectToLinkViewModel(Context context, String key){
        System.out.println(context.toString());
        listMutableLiveData = LinkRepository.getInstance(context, key).getAllLinksFromCategory();
    }

    public LiveData<ArrayList<Link>> getAllLinks(){
        return listMutableLiveData;
    }
}
