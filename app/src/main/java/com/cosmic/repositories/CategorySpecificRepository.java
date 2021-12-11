package com.cosmic.repositories;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import com.cosmic.interfaces.LoaderSpecificCategory;
import com.cosmic.model.Link;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CategorySpecificRepository {

    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final String currentUserID = firebaseAuth.getCurrentUser().getUid();

    @SuppressLint("StaticFieldLeak")
    static Context mContext;
    static String mPath;
    @SuppressLint("StaticFieldLeak")
    static CategorySpecificRepository categorySpecificRepositoryInstance;
    static LoaderSpecificCategory loaderSpecificCategory;
    ArrayList<Link> categories = new ArrayList<>();

    public static CategorySpecificRepository getInstance(Context context, String path){
        mContext = context;
        mPath = path;
        if (categorySpecificRepositoryInstance == null){
            categorySpecificRepositoryInstance = new CategorySpecificRepository();
        }
        loaderSpecificCategory = (LoaderSpecificCategory) mContext;
        return categorySpecificRepositoryInstance;
    }

    public MutableLiveData<ArrayList<Link>> getSpecificCategory(){
        loadCategory();
        MutableLiveData<ArrayList<Link>> mutableLiveData = new MutableLiveData<>();
        mutableLiveData.setValue(categories);
        return mutableLiveData;
    }

    private void loadCategory(){
        databaseReference.child("links").child(currentUserID).child(mPath).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categories.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    categories.add(dataSnapshot.getValue(Link.class));
                }
                loaderSpecificCategory.onCategoryLoaded();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
