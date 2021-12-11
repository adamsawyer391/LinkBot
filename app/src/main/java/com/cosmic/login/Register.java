package com.cosmic.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.cosmic.datetimehelpers.DateTimeHelpers;
import com.cosmic.linkbot.MainActivity;
import com.cosmic.linkbot.R;
import com.cosmic.model.Category;
import com.cosmic.model.Link;
import com.cosmic.model.User;
import com.cosmic.preferences.PreferenceBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

@SuppressWarnings("rawtypes")
public class Register extends AppCompatActivity {

    private Context context;
    private EditText etEmail, etPassword, etConfirmPassword;
    private String mEmail, mPassword, mPasswordConfirm;
    int duration = Toast.LENGTH_SHORT;
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private int size = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        context = Register.this;

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        Button btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(v -> {
            if (checkForNulls()){
                if (mPassword.equals(mPasswordConfirm)){
                    if (mPassword.length() >= 6){
                        if (mEmail.contains("@")){
                            doesEmailExist();
                            if (size <= 0){
                                registerUserWithFirebase();
                            }else{
                                displayToast("That email is already in use");
                            }
                        }else{
                            displayToast("This does not appear to be a valid email address");
                        }
                    }else{
                        displayToast("The password must be at least six characters long");
                    }
                }else{
                    displayToast("The passwords do not match.");
                }
            }else{
                displayToast("Please fill out all fields");
            }
        });
    }

    private void registerUserWithFirebase(){
        firebaseAuth.createUserWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                createNewUser();
            }else{
                displayToast(Objects.requireNonNull(task.getException()).toString());
            }
        });
    }

    private void createNewUser(){
        String link = "http://www.playstorelink.com"; //replace this upon live production if it comes to that
        String link_key = databaseReference.push().getKey();
        String category_key = databaseReference.push().getKey();
        String currentUserID = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        String string = "This is the link to this application on the Google Play Store.";

        User user = new User();
        user.setEmail(mEmail);
        user.setFullname("");
        user.setUserID(currentUserID);
        user.setUsername("");

        String date = DateTimeHelpers.DateHelper.getCurrentDate_MMddYY();
        long timestamp = DateTimeHelpers.TimestampHelper.getTimestampOfSelectedDate(date);

        assert link_key != null;
        assert category_key != null;
        Link startLink = new Link(link, link_key, date, string, "Our App", category_key, "All Links", timestamp);
        Category category = new Category("All Links",
                "https://firebasestorage.googleapis.com/v0/b/linkbot-19492.appspot.com/o/links.jpg?alt=media&token=cd74fb5d-2b1f-47ed-9481-62a678ebd835", category_key);

        SharedPreferences sharedPreferences = getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PreferenceBuilder.all_link_key, category_key);
        editor.apply();

        databaseReference.child("categories").child(currentUserID).child(category_key).setValue(category);
        databaseReference.child("links").child(currentUserID).child(category_key).child(link_key).setValue(startLink);
        databaseReference.child("users").child(currentUserID).setValue(user).addOnCompleteListener(task -> activityTransition(MainActivity.class));
    }

    private void activityTransition(Class activity){
        PreferenceBuilder.insertInitialPreferences(context);
        Intent intent = new Intent(context, activity);
        startActivity(intent);
        finish();
        Animatoo.animateCard(context);
    }

    private boolean checkForNulls(){
        mEmail = etEmail.getText().toString().trim();
        mPassword = etPassword.getText().toString().trim();
        mPasswordConfirm = etConfirmPassword.getText().toString().trim();
        return !TextUtils.isEmpty(mEmail) && !TextUtils.isEmpty(mPassword) && !TextUtils.isEmpty(mPasswordConfirm);
    }

    private void doesEmailExist(){
        firebaseAuth.fetchSignInMethodsForEmail(mEmail).addOnCompleteListener(task -> size = task.getResult().getSignInMethods().size());
    }

    private void displayToast(String message) {
        Toast.makeText(context, message, duration).show();
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        activityTransition(Login.class);
    }
}
