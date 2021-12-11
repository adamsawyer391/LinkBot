package com.cosmic.linkbot;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.cosmic.datetimehelpers.DateTimeHelpers;
import com.cosmic.login.Register;
import com.cosmic.model.Link;
import com.cosmic.preferences.PreferenceBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class AddLinkActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "AddLinkActivity";

    private Context context;
    private EditText urlEditText, title, description;
    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private String category, category_name;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_link);
        context = AddLinkActivity.this;
        urlEditText = findViewById(R.id.url);
        title = findViewById(R.id.title);
        description = findViewById(R.id.description);

        Button clickButton = findViewById(R.id.clickButton);
        clickButton.setOnClickListener(this);

        getIntentExtra();

    }

    private void getIntentExtra(){
        Intent intent = getIntent();
        if (intent != null){
            category = intent.getExtras().getString("category");
            category_name = intent.getExtras().getString("category_name");

            Log.d(TAG, "getIntentExtra: category : " + category);
            Log.d(TAG, "getIntentExtra: category name : " + category_name);
        }
    }

    private void checkForNulls(){
        String mURL, mTitle, mDescription;
        mURL = urlEditText.getText().toString();
        mTitle = title.getText().toString();
        mDescription = description.getText().toString();
        if (mURL.equals("")){
            displayToast("You must add a URL link");
        }else if (mTitle.equals("")){
            displayToast("Please add a title");
        }else if (mDescription.equals("")){
            displayToast("Please add a description");
        }else{
            saveLinkToDataBase(mURL, mTitle, mDescription);
        }
    }

    private void saveLinkToDataBase(String url, String title, String description){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String currentUserID = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        String key = databaseReference.push().getKey();

        String date_added = DateTimeHelpers.DateHelper.getCurrentDate_MMddYY();
        long timestamp = DateTimeHelpers.TimestampHelper.getTimestampOfSelectedDate(date_added);

        Link link = new Link(url, key, date_added, description, title, category, category_name, timestamp);

        if (key != null) {
            SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
            String all_links = sharedPreferences.getString(PreferenceBuilder.all_link_key, "");
            databaseReference.child("links").child(currentUserID).child(all_links).child(key).setValue(link);
            databaseReference.child("links").child(currentUserID).child(category).child(key).setValue(link).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    enableBackNavigation();
                }
            });
        }else{
            displayToast("Error creating new link, please try again.");
        }
    }

    private void enableBackNavigation(){
        Intent intent = new Intent(context, CategoryActivity.class);
        intent.putExtra("category_key", category);
        intent.putExtra("category_name", category_name);
        startActivity(intent);
        finish();
        Animatoo.animateCard(context);
    }

    private void displayToast(String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        checkForNulls();
    }
}
