package com.cosmic.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.cosmic.linkbot.MainActivity;
import com.cosmic.linkbot.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

@SuppressWarnings("rawtypes")
public class Login extends AppCompatActivity implements View.OnClickListener{

    private Context context;
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private EditText etEmail, etPassword;
    private String mEmail, mPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = Login.this;
        checkLoginStatus();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);

        TextView forgotPassword = findViewById(R.id.forgotPassword);
        forgotPassword.setOnClickListener(this);

        TextView transitionRegister = findViewById(R.id.transitRegister);
        transitionRegister.setOnClickListener(this);
    }

    private void checkForNulls(){
        mEmail = etEmail.getText().toString().trim();
        mPassword = etPassword.getText().toString().trim();
        if (mPassword.equals("") || mEmail.equals("")){
            displayToast("Please fill out all fields");
        }else{
            authenticateUser();
        }
    }

    private void authenticateUser(){
        firebaseAuth.signInWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                activityTransition(MainActivity.class);
            }else{
                String exception = Objects.requireNonNull(task.getException()).toString();
                displayToast(exception);
            }
        });
    }

    private void displayToast(String message){
        int duration = Toast.LENGTH_SHORT;
        Toast.makeText(context, message, duration).show();
    }

    private void activityTransition(Class activity){
        Intent intent = new Intent(context, activity);
        startActivity(intent);
        finish();
        Animatoo.animateCard(context);
    }

    @Override
    public void onClick(View v) {
        switch (v.getTag().toString()){
            case "login":
                checkForNulls();
                break;
            case "register":
                activityTransition(Register.class);
                break;
            case "password":
                //intent to forgot password activity
                break;
            default:
                displayToast("Something has gone wrong, please try again");
                break;
        }
    }

    private void checkLoginStatus(){
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null){
            activityTransition(MainActivity.class);
        }
    }
}
