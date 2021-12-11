package com.cosmic.hack;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.cosmic.model.Link;
import com.cosmic.preferences.PreferenceBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HackAttack {

    private static final String TAG = "HackAttack";

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private String currentUserID = firebaseAuth.getCurrentUser().getUid();

    private void getAllCategoryKeys(){
        ArrayList<String> category_keys = new ArrayList<>();
        databaseReference.child("links").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                category_keys.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    String key = dataSnapshot.getKey();
                    category_keys.add(key);
                }
                //viewSnapshotOfEachCategory(category_keys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

//    private void viewSnapshotOfEachCategory(ArrayList<String> catKeys){
//        String method = "extractLinkDataByCategory";
//        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
//        String all_key = sharedPreferences.getString(PreferenceBuilder.all_link_key, "");
//        for (int i = 0; i < catKeys.size(); i++){
//            databaseReference.child("links").child(currentUserID).child(catKeys.get(i)).addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
//                        String cat_key = dataSnapshot.child("category_key").getValue().toString();
//                        String cat_name = dataSnapshot.child("category_name").getValue().toString();
//                        String date_added = dataSnapshot.child("date_added").getValue().toString();
//                        String description = dataSnapshot.child("description").getValue().toString();
//                        String key = dataSnapshot.child("key").getValue().toString();
//                        long timestamp = Long.parseLong(dataSnapshot.child("timestamp").getValue().toString());
//                        String title = dataSnapshot.child("title").getValue().toString();
//                        String url = dataSnapshot.child("url").getValue().toString();
//                        Link link = new Link();
//                        link.setCategory_key(cat_key);
//                        link.setCategory_name(cat_name);
//                        link.setDate_added(date_added);
//                        link.setDescription(description);
//                        link.setKey(key);
//                        link.setTimestamp(timestamp);
//                        link.setTitle(title);
//                        link.setUrl(url);
//                        databaseReference.child("links").child(currentUserID).child(all_key).child(key).setValue(link);
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });
//        }
//    }
}
