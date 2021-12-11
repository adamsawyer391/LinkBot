package com.cosmic.viewmodels;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.cosmic.model.Link;
import com.cosmic.repositories.CategorySpecificRepository;

import java.util.ArrayList;

public class ViewModelSpecificCategory extends ViewModel {

    MutableLiveData<ArrayList<Link>> categories;

    public void obtainCategory(Context context, String path){
        categories = CategorySpecificRepository.getInstance(context, path).getSpecificCategory();
    }

    public LiveData<ArrayList<Link>> getCategory(){
        return categories;
    }
}
