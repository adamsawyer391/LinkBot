package com.cosmic.viewmodels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cosmic.model.Category;
import com.cosmic.repositories.CategoryRepository;

import java.util.ArrayList;

public class ViewModelAllCategories extends ViewModel {

    MutableLiveData<ArrayList<Category>> categoryList;

    public void connectToCategoryViewModel(Context context){
        categoryList = CategoryRepository.getInstance(context).getCategories();
    }

    public LiveData<ArrayList<Category>> getCategories(){
        return categoryList;
    }
}
