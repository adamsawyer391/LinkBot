package com.cosmic.search.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.cosmic.model.Link;
import com.cosmic.preferences.PreferenceBuilder;
import com.cosmic.room.model.SearchTerm;
import com.cosmic.search.loader.SearchResultLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchRepository {

    private static final String TAG = "SearchRepository";

    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final String currentUserID = firebaseAuth.getCurrentUser().getUid();

    static SearchRepository searchRepositoryInstance;
    ArrayList<Link> searchTerms = new ArrayList<>();
    static String term;

    static Context mContext;
    static SearchResultLoader searchResultLoader;

    public static SearchRepository getInstance(Context context, String search_query){
        mContext = context;
        term = search_query;
        if(searchRepositoryInstance == null){
            searchRepositoryInstance = new SearchRepository();
        }
        searchResultLoader = (SearchResultLoader) mContext;
        return searchRepositoryInstance;
    }

    public MutableLiveData<ArrayList<Link>> getSearchResults(){
        loadSearchTerms(term);
        MutableLiveData<ArrayList<Link>> mutableLiveData = new MutableLiveData<>();
        mutableLiveData.setValue(searchTerms);
        return mutableLiveData;
    }

    private void loadSearchTerms(String term){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(mContext.getPackageName(), Context.MODE_PRIVATE);
        String all_links_key = sharedPreferences.getString(PreferenceBuilder.all_link_key, "");
        Query query = databaseReference.child("links").child(currentUserID).child(all_links_key);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                searchTerms.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    String key = dataSnapshot.getKey();
                    String category = snapshot.child(key).child("category_name").getValue().toString().toLowerCase();
                    String description = snapshot.child(key).child("description").getValue().toString().toLowerCase();
                    String title = snapshot.child(key).child("title").getValue().toString().toLowerCase();
                    if (category.equals(term)){
                        searchTerms.add(dataSnapshot.getValue(Link.class));
                    }
                    if (description.equals(term)){
                        searchTerms.add(dataSnapshot.getValue(Link.class));
                    }
                    if (title.equals(term)){
                        searchTerms.add(dataSnapshot.getValue(Link.class));
                    }
                    if(term.contains(" ")){
                        String[] multiple = term.split(" ");
                        for (int i = 0; i < multiple.length; i++){
                            String birdie = multiple[i];
                            if(description.contains(birdie)){
                                searchTerms.add(dataSnapshot.getValue(Link.class));
                            }
                        }
                    }
                }
                searchResultLoader.onSearchTermLoaded();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadCategoryLinks(String key, String term){
        Query query = databaseReference.child("links").child(currentUserID).child(key);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                searchTerms.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    String key = dataSnapshot.getKey();
                    String category = snapshot.child(key).child("category_name").getValue().toString();
                    String description = snapshot.child(key).child("description").getValue().toString();
                    String title = snapshot.child(key).child("title").getValue().toString();
                    if (category.contains(term)){
                        searchTerms.add(dataSnapshot.getValue(Link.class));
                    }
                    if (description.contains(term)){
                        searchTerms.add(dataSnapshot.getValue(Link.class));
                    }
                    if (title.contains(term)){
                        searchTerms.add(dataSnapshot.getValue(Link.class));
                    }
                }
                searchResultLoader.onSearchTermLoaded();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
