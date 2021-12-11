package com.cosmic.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.cosmic.linkbot.MainActivity;
import com.cosmic.linkbot.R;
import com.cosmic.model.Link;
import com.cosmic.search.loader.SearchResultLoader;
import com.cosmic.search.viewmodel.SearchViewModel;

import java.util.List;

public class SearchResultActivity extends AppCompatActivity implements SearchResultLoader {

    private static final String TAG = "SearchResultActivity";

    private Context context;
    private RelativeLayout searchResultsNotFound;
    private RecyclerView recyclerView;
    private SearchViewModel searchViewModel;
    private ProgressBar progressBar;
    private List<Link> list;
    private SearchResultAdapter searchResultAdapter;
    private String term;
    private TextView tvSorry, tvCount;
    private RelativeLayout countLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        context = SearchResultActivity.this;

        updateUI();
        getIncomingIntent();
    }

    private void updateUI(){
        searchResultsNotFound = findViewById(R.id.searchResultsNotFound);
        recyclerView = findViewById(R.id.recycler_view);
        progressBar = findViewById(R.id.progress_bar);
        tvSorry = findViewById(R.id.tvSorry);
        countLayout = findViewById(R.id.resultCountBar);
        tvCount = findViewById(R.id.count);
    }

    private void getIncomingIntent(){
        Intent incomingIntent = getIntent();
        if(incomingIntent != null){
            term = incomingIntent.getExtras().getString("searched_string");
            searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);
            searchViewModel.connectToSearchViewModel(context, term);
        }
    }

    private void startRecyclerView(List<Link> list) {
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        searchResultAdapter = new SearchResultAdapter(list, context);
        recyclerView.setAdapter(searchResultAdapter);
    }

    @Override
    public void onSearchTermLoaded() {
        progressBar.setVisibility(View.GONE);
        searchViewModel.getSearchResults().observe(this, searchTerms -> {
            int count = 0;
            for (int i = 0; i < searchTerms.size(); i++){
                count++;
            }
            if (count == 0){
                Log.d(TAG, "onSearchTermLoaded: nothing returned");
                recyclerView.setVisibility(View.GONE);
                searchResultsNotFound.setVisibility(View.VISIBLE);
                countLayout.setVisibility(View.GONE);
                tvSorry.setText(new StringBuilder("Sorry, but we found no results that matched your search for ").append("'").append(term).append("'"));
            }else{
                Log.d(TAG, "onSearchTermLoaded: something was found");
                list = searchTerms;
                recyclerView.setVisibility(View.VISIBLE);
                countLayout.setVisibility(View.VISIBLE);
                tvCount.setText(String.valueOf(count));
                searchResultsNotFound.setVisibility(View.GONE);
                startRecyclerView(list);
                searchResultAdapter.notifyDataSetChanged();
            }
            Log.d(TAG, "onSearchTermLoaded: search results : " + searchTerms);
//            if (searchTerms == null){
//                Log.d(TAG, "onSearchTermLoaded: array list was empty");
//                recyclerView.setVisibility(View.GONE);
//                searchResultsNotFound.setVisibility(View.VISIBLE);
//                //searchResultsNotFound.bringToFront();
//            }else{
//                Log.d(TAG, "onSearchTermLoaded: array list contained objects");
//                list = searchTerms;
//                recyclerView.setVisibility(View.VISIBLE);
//                searchResultsNotFound.setVisibility(View.GONE);
//                startRecyclerView(list);
//                searchResultAdapter.notifyDataSetChanged();
//            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(context, MainActivity.class);
        startActivity(intent);
        finish();
        Animatoo.animateSlideLeft(context);
    }
}
