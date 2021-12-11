package com.cosmic.management;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.cosmic.datetimehelpers.DateTimeHelpers;
import com.cosmic.interfaces.LoaderAllCategories;
import com.cosmic.interfaces.LoaderSpecificCategory;
import com.cosmic.linkbot.MainActivity;
import com.cosmic.linkbot.R;
import com.cosmic.model.Category;
import com.cosmic.model.Link;
import com.cosmic.viewmodels.ViewModelAllCategories;
import com.cosmic.viewmodels.ViewModelSpecificCategory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener, LoaderSpecificCategory, LoaderAllCategories {

    private static final String TAG = "SettingsActivity";

    private ViewModelSpecificCategory viewModelSpecificCategory;
    private ViewModelAllCategories viewModelAllCategories;
    private Context context;
    private String all_categories_key;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        context = SettingsActivity.this;

        TextView saveDocument = findViewById(R.id.saveDocument);
        saveDocument.setOnClickListener(this);

        TextView restoreDeleted = findViewById(R.id.restoreDeleted);
        restoreDeleted.setOnClickListener(this);

        initViewModels();
    }

    private void initViewModels(){
        viewModelAllCategories = new ViewModelProvider(this).get(ViewModelAllCategories.class);
        viewModelAllCategories.connectToCategoryViewModel(context);
    }

    private void downloadAllLinksAndConvertToTextDocument(ArrayList<Link> links){
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < links.size(); i++){
            stringBuilder.append(links.get(i).getTitle())
                    .append("\n")
                    .append(links.get(i).getCategory_name())
                    .append("\n")
                    .append(links.get(i).getDescription())
                    .append("\n")
                    .append(links.get(i).getUrl())
                    .append("\n").append("\n").append("\n");
            Log.d(TAG, "downloadAllLinksAndConvertToTextDocument: string builder : " + stringBuilder);
        }
        writeDocument(stringBuilder);
    }

    private void writeDocument(StringBuilder builder){
        try{
//            File path = Environment.getExternalStorageDirectory();
//            File dir = new File(path + System.lineSeparator() + Environment.DIRECTORY_DOCUMENTS);
//            String filename = "links" + DateTimeHelpers.TimestampHelper.getCurrentTimeStamp();
//            File file = new File(dir, filename);
//
//            FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
//            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
//            bufferedWriter.write(builder.toString());
//            bufferedWriter.close();
//
//            Intent intent = new Intent(Intent.ACTION_SEND);
//            File export = new File(getFilesDir(), filename)

            String name = "Links_";
            String filename = name + new SimpleDateFormat("MM_dd_yyyy", Locale.getDefault()).format(new Date()) + ".txt";
            FileOutputStream fileOutputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            fileOutputStream.write(builder.toString().getBytes());
            fileOutputStream.close();
            File file = new File(getFilesDir(), filename);
            Uri uri = FileProvider.getUriForFile(context, "com.cosmic.linkbot.fileprovider", file);
            Intent exportIntent = new Intent(Intent.ACTION_SEND);
            exportIntent.setType("text/plain");
            exportIntent.putExtra(Intent.EXTRA_SUBJECT, name.toUpperCase() + " Data");
            exportIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            exportIntent.putExtra(Intent.EXTRA_STREAM, uri);
            exportIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(Intent.createChooser(exportIntent, "Share data"));

        }catch (IOException e){
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getTag().toString()){
            case "document":
                viewModelSpecificCategory = new ViewModelProvider(this).get(ViewModelSpecificCategory.class);
                viewModelSpecificCategory.obtainCategory(context, all_categories_key);
                break;
            case "restore":
                //intent to restore deleted links -- a recycler view with deleted links that
                // can be restored on long click
                break;
        }
    }

    @Override
    public void onCategoryLoaded() {
        viewModelSpecificCategory.getCategory().observe(this, new Observer<ArrayList<Link>>() {
            @Override
            public void onChanged(ArrayList<Link> links) {
                downloadAllLinksAndConvertToTextDocument(links);
            }
        });
    }

    @Override
    public void onAllCategoriesLoaded() {
        viewModelAllCategories.getCategories().observe(this, new Observer<ArrayList<Category>>() {
            @Override
            public void onChanged(ArrayList<Category> categories) {
                for (int i = 0; i < categories.size(); i++){
                    String name = categories.get(i).getName();
                    if (name.equals("All Links")){
                        all_categories_key = categories.get(i).getKey();
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(context, MainActivity.class);
        startActivity(intent);
        Animatoo.animateCard(context);
        finish();
    }
}
