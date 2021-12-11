package com.cosmic.management;

import androidx.annotation.NonNull;

import com.cosmic.model.Link;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class RestoreDeleteManager {

    private static final String TAG = "RestoreDeleteManager";
    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final String currentUserID = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

    public void copyCategoryToDeletedKeysNode(String key){
        databaseReference.child("links").child(currentUserID).child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    String cat_key = Objects.requireNonNull(dataSnapshot.child("category_key").getValue()).toString();
                    String cat_name = Objects.requireNonNull(dataSnapshot.child("category_name").getValue()).toString();
                    String date_added = Objects.requireNonNull(dataSnapshot.child("date_added").getValue()).toString();
                    String description = Objects.requireNonNull(dataSnapshot.child("description").getValue()).toString();
                    String key = Objects.requireNonNull(dataSnapshot.child("key").getValue()).toString();
                    long timestamp = Long.parseLong(Objects.requireNonNull(dataSnapshot.child("timestamp").getValue()).toString());
                    String title = Objects.requireNonNull(dataSnapshot.child("title").getValue()).toString();
                    String url = Objects.requireNonNull(dataSnapshot.child("url").getValue()).toString();
                    Link link = new Link();
                    link.setCategory_key(cat_key);
                    link.setCategory_name(cat_name);
                    link.setDate_added(date_added);
                    link.setDescription(description);
                    link.setKey(key);
                    link.setTimestamp(timestamp);
                    link.setTitle(title);
                    link.setUrl(url);
                    databaseReference.child("deleted").child(currentUserID).child(cat_key).child(key).setValue(link);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
