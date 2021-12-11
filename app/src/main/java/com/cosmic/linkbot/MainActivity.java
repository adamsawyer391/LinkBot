package com.cosmic.linkbot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.cosmic.adapter.CategoryAdapter;
import com.cosmic.interfaces.LoaderAllCategories;
import com.cosmic.login.Login;
import com.cosmic.management.RestoreDeleteManager;
import com.cosmic.management.SettingsActivity;
import com.cosmic.model.Category;
import com.cosmic.preferences.PreferenceBuilder;
import com.cosmic.room.db.SearchDatabase;
import com.cosmic.room.model.SearchTerm;
import com.cosmic.search.SearchResultActivity;
import com.cosmic.utils.RecycleItemClick;
import com.cosmic.viewmodels.ViewModelAllCategories;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, LoaderAllCategories {

    private static final String TAG = "MainActivity";

    private Context context;
    private RecyclerView recyclerView;
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final String currentUserID = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
    private ViewModelAllCategories viewModelAllCategories;
    private CategoryAdapter categoryAdapter;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private SearchDatabase database;
    List<SearchTerm> searchTermList;
    private AutoCompleteTextView autoCompleteTextView;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = MainActivity.this;

        executeStartMethods();

        FloatingActionButton floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(this);
    }







    /**
     *
     *
     * ::::::::::::::::::::::::::::::::::BASIC STARTUP METHODS::::::::::::::::::::::::::::::::::::::::::::::::::
     *
     *
     */

    private void executeStartMethods(){
        initViewModel();
        checkSharedPreferences();
        manageMenuBar();
        populateRecyclerView();
        recyclerTouchHelper();
        recyclerSwipeToDelete();
        checkForIncomingIntent();
        instantiateAutoCompleteSearch();
    }

    private void checkForIncomingIntent(){
        Intent intent = getIntent();
        if (intent != null){
            Log.d(TAG, "checkForIncomingIntent: intent itself : " + intent);
            String action = intent.getAction();
            String type = intent.getType();
            if (Intent.ACTION_SEND.equals(action) && type != null){
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    clipboardManager.clearPrimaryClip();
                }else{
                    clipboardManager.setPrimaryClip(new ClipData(null));
                }
                ClipData clipData = intent.getClipData();
                clipboardManager.setPrimaryClip(clipData);
            }
        }
    }

    private void updateSharedPreferences(){
        editor = sharedPreferences.edit();
        editor.putBoolean(PreferenceBuilder.should_check_to_delete_category, true);
        editor.putBoolean(PreferenceBuilder.should_check_to_delete_link, true);
        editor.putBoolean(PreferenceBuilder.retain_deleted_categories, true);
        editor.apply();
    }

    private void checkSharedPreferences(){
        sharedPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        long a = sharedPreferences.getLong(PreferenceBuilder.rateUsTimestamp, 0);
        boolean b = sharedPreferences.getBoolean(PreferenceBuilder.rateUs, false);
        Log.d(TAG, "checkSharedPreferences: a + b : rate us timestamp and can they rate us : " + a + " " + b);
        updateSharedPreferences();
    }

    private void manageMenuBar(){
        ImageView imageView = findViewById(R.id.home_menu);
        imageView.bringToFront();
        imageView.setOnClickListener(this::inflatePopupMenu);
    }










    /**
     *
     *
     * ::::::::::::::::::::::::::::::::::::::::::::SEARCH BAR METHODS:::::::::::::::::::::::::::::::::::::::::::::::::::::
     *
     *
     */

    private void instantiateAutoCompleteSearch(){
        ImageView searchImageView = findViewById(R.id.searchImageView);
        searchImageView.setOnClickListener(v -> {
            saveSearchTerm(autoCompleteTextView.getText().toString().trim());
            conductSearch(autoCompleteTextView.getText().toString().trim().toLowerCase());
        });

        database = Room.databaseBuilder(context, SearchDatabase.class, "searches.db").fallbackToDestructiveMigration().build();
        autoCompleteTextView = findViewById(R.id.autoComplete);
        searchTermList = deliverSearchTerms();
        ArrayList<String> list = new ArrayList<>();
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            for (int i = 0; i < searchTermList.size(); i++){
                list.add(searchTermList.get(i).getSearch_term());
            }
        }, 500);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        autoCompleteTextView.setAdapter(adapter);
    }

    private void conductSearch(String search_query){
        Intent intent = new Intent(context, SearchResultActivity.class);
        intent.putExtra("searched_string", search_query);
        startActivity(intent);
        Animatoo.animateSlideLeft(context);
    }

    private void saveSearchTerm(String search){
        SearchTerm searchTerm = new SearchTerm();
        searchTerm.setSearch_term(search);
        new Thread(() -> {
            List<SearchTerm> savedSearches;
            savedSearches = database.searchDAO().getAllSearchTerms();
            int count = 0;
            for (int i = 0; i < savedSearches.size(); i++){
                if(savedSearches.get(i).getSearch_term().equals(search)){
                    count+=1;
                }
            }
            if (count == 0){
                database.searchDAO().insert(searchTerm);
            }

        }).start();
    }

    private List<SearchTerm> deliverSearchTerms(){
        new Thread(() -> searchTermList = database.searchDAO().getAllSearchTerms()).start();
        return searchTermList;
    }









    /**
     *
     *
     * ::::::::::::::::::::::::::::::::MAIN CATEGORY RECYCLER VIEW WITH SUPPORT METHODS:::::::::::::::::::::::::::::::::::::::::::
     *
     *
     */

    private void initViewModel(){
        viewModelAllCategories = new ViewModelProvider(this).get(ViewModelAllCategories.class);
        viewModelAllCategories.connectToCategoryViewModel(context);
    }

    private void populateRecyclerView(){
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        categoryAdapter = new CategoryAdapter(context, viewModelAllCategories.getCategories().getValue());
        recyclerView.setAdapter(categoryAdapter);
    }

    private void recyclerTouchHelper(){
        recyclerView.addOnItemTouchListener(new RecycleItemClick(this, (view, position) -> activityTransition(CategoryActivity.class, position)));
    }

    private void recyclerSwipeToDelete(){
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                boolean delete_category = sharedPreferences.getBoolean(PreferenceBuilder.should_check_to_delete_category, false);
                boolean retain_category = sharedPreferences.getBoolean(PreferenceBuilder.retain_deleted_categories, false);
                String key = Objects.requireNonNull(viewModelAllCategories.getCategories().getValue()).get(viewHolder.getAdapterPosition()).getKey();
                if (delete_category){
                    inflateAlertDialog(retain_category, key);
                }else{
                    if (retain_category){
                        Log.d(TAG, "onSwiped: retain a copy of the deleted category and all links contained therein");
                    }else{
                        Log.d(TAG, "onSwiped: delete the category and all links without saving");
                    }
                    Log.d(TAG, "onSwiped: you do not need to check before deleting the category, you can delete without AD");
                }
            }
        }).attachToRecyclerView(recyclerView);
    }

//    private void activityTransition(int position){
//        Intent intent = new Intent(context, CategoryActivity.class);
//        String name = Objects.requireNonNull(viewModelAllCategories.getCategories().getValue()).get(position).getName();
//        String key = viewModelAllCategories.getCategories().getValue().get(position).getKey();
//        intent.putExtra("category_name", name);
//        intent.putExtra("category_key", key);
//        startActivity(intent);
//        Animatoo.animateCard(context);
//    }












    /**
     *
     *
     * ::::::::::::::::::::MENU, ALERT DIALOG, FLOATING ACTION BUTTON, SIGN OUT, AND OTHER UTILITIES, TOAST DISPLAY:::::::::::::::::::::::::
     *
     *
     */

    private void inflateAlertDialog(boolean retain_category, String categoryKey){
        if (retain_category){
            @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.snippet_dialog_layout, null);
            CheckBox checkBox = view.findViewById(R.id.checkbox);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.drawable.ic_alert);
            builder.setTitle(getString(R.string.delete_category));
            builder.setMessage(getString(R.string.delete_category_message));
            builder.setCancelable(true);
            builder.setNegativeButton(getString(R.string.CANCEL), (dialog, which) -> dialog.dismiss());
            builder.setPositiveButton(getString(R.string.DELETE), (dialog, which) -> {
                if (checkBox.isChecked()){
                    editor.putBoolean(PreferenceBuilder.should_check_to_delete_category, false);
                    editor.apply();
                }
                RestoreDeleteManager restoreDeleteManager = new RestoreDeleteManager();
                restoreDeleteManager.copyCategoryToDeletedKeysNode(categoryKey);
            });
            builder.create().show();
        }
    }

    @SuppressWarnings("rawtypes")
    private void activityTransition(Class activity, int position){
        Intent intent = new Intent(context, activity);
        if (position != -1){
            String name = Objects.requireNonNull(viewModelAllCategories.getCategories().getValue()).get(position).getName();
            String key = viewModelAllCategories.getCategories().getValue().get(position).getKey();
            intent.putExtra("category_name", name);
            intent.putExtra("category_key", key);
        }
        startActivity(intent);
        if (activity.equals(Login.class)){
            finish();
        }
        Animatoo.animateSlideLeft(context);
    }

    private void addCategoryFloatingActionButton(){
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_view, null);
        EditText cat = dialogView.findViewById(R.id.etCategory);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        builder.setTitle(getString(R.string.add_new_category));
        builder.setMessage(getString(R.string.new_category_message));
        builder.setCancelable(true);
        builder.setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss());
        builder.setPositiveButton("ADD", (dialog, which) -> {
            String string = cat.getText().toString();
            if (!string.equals("")){
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                String key = databaseReference.push().getKey();
                if (key != null){
                    Category category = new Category(string, "", key);
                    databaseReference.child("categories").child(currentUserID).child(key).setValue(category);
                }else{
                    displayToast("The link did not save, please try again");
                }
            }else{
                displayToast("Please enter a category title");
            }
        });
        builder.create().show();
    }

    private void inflatePopupMenu(View view){
        Log.d(TAG, "inflatePopupMenu: method called : ");
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.home_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            selectMenuItem(item);
            return true;
        });
    }

    private void selectMenuItem(MenuItem item) {
        switch (item.getTitle().toString()){
            case "Home":
                activityTransition(MainActivity.class, -1);
                break;
            case "Settings":
                activityTransition(SettingsActivity.class, -1);
                break;
            case "Sign Out":
                signOut();
                break;
        }
    }

    private void signOut(){
        firebaseAuth.signOut();
        activityTransition(Login.class, -1);
    }

    @Override
    public void onClick(View v) {
        addCategoryFloatingActionButton();
    }

    private void displayToast(String message){
        int duration = Toast.LENGTH_SHORT;
        Toast.makeText(context, message, duration).show();
    }

    @Override
    public void onAllCategoriesLoaded() {
        viewModelAllCategories.getCategories().observe(this, categories -> categoryAdapter.notifyDataSetChanged());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}