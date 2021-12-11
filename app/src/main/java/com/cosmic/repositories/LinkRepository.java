package com.cosmic.repositories;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.cosmic.interfaces.LoaderLinks;
import com.cosmic.model.Link;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class LinkRepository {

    private static final String TAG = "LinkRepository";

    private final  FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final String currentUserID = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    //@SuppressLint("StaticFieldLeak")
    static LinkRepository linkRepositoryInstance;

    ArrayList<Link> links = new ArrayList<>();

    //@SuppressLint("StaticFieldLeak")
    static Context mContext;
    static String path;
    static LoaderLinks loaderLinks;
    
    public static LinkRepository getInstance(Context context, String key){
        mContext = context;
        System.out.println(mContext);
        System.out.println(context);
        path = key;
        Log.d(TAG, "getInstance: path : " + path);
        if (linkRepositoryInstance == null){
            linkRepositoryInstance = new LinkRepository();
        }
        loaderLinks = (LoaderLinks) mContext;
        return linkRepositoryInstance;
    }

    public MutableLiveData<ArrayList<Link>> getAllLinksFromCategory(){
        loadLinks();
        MutableLiveData<ArrayList<Link>> mutableLiveData = new MutableLiveData<>();
        mutableLiveData.setValue(links);
        return mutableLiveData;
    }

    private void loadLinks(){
        Query query = databaseReference.child("links").child(currentUserID).child(path).orderByChild("timestamp");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                links.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    links.add(dataSnapshot.getValue(Link.class));
                }
                loaderLinks.onLinksLoaded();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
