package com.cosmic.repositories;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.cosmic.interfaces.LoaderAllCategories;
import com.cosmic.model.Category;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CategoryRepository {

    private static final String TAG = "CategoryRepository";

    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final String currentUserID = firebaseAuth.getCurrentUser().getUid();

    static CategoryRepository categoryRepositoryInstance;

    ArrayList<Category> categories = new ArrayList<>();
    static LoaderAllCategories loaderAllCategoriesListener;
    static Context mContext;

    public static CategoryRepository getInstance(Context context){
        mContext = context;
        Log.d(TAG, "getInstance: mContext : " + mContext);
        Log.d(TAG, "getInstance: context : " + context);
        if (categoryRepositoryInstance == null){
            categoryRepositoryInstance = new CategoryRepository();
        }
        loaderAllCategoriesListener = (LoaderAllCategories) mContext;
        Log.d(TAG, "getInstance: loader : " + loaderAllCategoriesListener.toString());
        return categoryRepositoryInstance;
    }

    public MutableLiveData<ArrayList<Category>> getCategories(){
        loadCategories();
        MutableLiveData<ArrayList<Category>> mutableLiveData = new MutableLiveData<>();
        mutableLiveData.setValue(categories);
        return mutableLiveData;
    }

    private void loadCategories(){
        databaseReference.child("categories").child(currentUserID).orderByChild("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categories.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    categories.add(dataSnapshot.getValue(Category.class));
                }
                Log.d(TAG, "onDataChange: categories : " + categories);
                loaderAllCategoriesListener.onAllCategoriesLoaded();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
