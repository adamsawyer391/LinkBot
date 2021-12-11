package com.cosmic.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cosmic.linkbot.R;
import com.cosmic.room.model.SearchTerm;

import java.util.ArrayList;
import java.util.List;

public class LinkSearchTermAdapter extends RecyclerView.Adapter<LinkSearchTermAdapter.SearchTermViewHolder> {

    private static final String TAG = "LinkSearchTermAdapter";

    private final List<SearchTerm> searchTerms;
    private final Context context;

    public LinkSearchTermAdapter(List<SearchTerm> searchTerms, Context context) {
        this.searchTerms = searchTerms;
        this.context = context;
    }

    @NonNull
    @Override
    public SearchTermViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_item, parent, false);
        return new SearchTermViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchTermViewHolder holder, int position) {
        holder.tvSearch.setText(searchTerms.get(position).getSearch_term());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: item clicked : " + searchTerms.get(position).getSearch_term());
            }
        });
    }

    @Override
    public int getItemCount() {
        if(searchTerms != null){
            return searchTerms.size();
        }else{
            return 0;
        }
    }

    static class SearchTermViewHolder extends RecyclerView.ViewHolder {

        final TextView tvSearch;

        public SearchTermViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSearch = itemView.findViewById(R.id.tvSearch);
        }

    }

}
