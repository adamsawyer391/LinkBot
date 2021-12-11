package com.cosmic.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.cosmic.datetimehelpers.DateTimeHelpers;
import com.cosmic.login.Register;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PreferenceBuilder {

    public static String rateUs = "allow_check_for_rate_us";
    public static String rateUsTimestamp = "timestamp_for_last_check_on_rate_us";
    public static String all_link_key = "all_links_key";
    public static String should_check_to_delete_category = "should_check_delete_category";
    public static String should_check_to_delete_link = "should_check_delete_link";
    public static String retain_deleted_categories = "retain_deleted_categories";

    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;

    public static void insertInitialPreferences(Context context){
        sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        String date_added = DateTimeHelpers.DateHelper.getCurrentDate_MMddYY();
        long timestamp = DateTimeHelpers.TimestampHelper.getTimestampOfSelectedDate(date_added);

        editor.putBoolean(should_check_to_delete_category, true);
        editor.putBoolean(should_check_to_delete_link, true);
        editor.putBoolean(retain_deleted_categories, true);
        editor.putBoolean(rateUs, true);
        editor.putLong(rateUsTimestamp, timestamp);
        editor.apply();
    }

}
