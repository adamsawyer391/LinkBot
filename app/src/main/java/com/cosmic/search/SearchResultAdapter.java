package com.cosmic.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.cosmic.linkbot.R;
import com.cosmic.model.Link;
import java.util.List;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.SearchTermViewHolder> {

    private final List<Link> searchTerms;
    private final Context context;

    public SearchResultAdapter(List<Link> searchTerms, Context context) {
        this.searchTerms = searchTerms;
        this.context = context;
    }

    @NonNull
    @Override
    public SearchTermViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_search_result, parent, false);
        return new SearchTermViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchTermViewHolder holder, int position) {
//        String desc = searchTerms.get(position).getDate_added() + "  -  " + searchTerms.get(position).getDescription();
//        String[] split = desc.split("-");
//        SpannableString spannableString = SpannableString.valueOf(desc);
//        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, split[0].length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
//        spannableStringBuilder.append(spannableString);
        //holder.tvDescription.setText(spannableStringBuilder);

        holder.tvLink.setText(searchTerms.get(position).getUrl());
        holder.tvDescription.setText(new StringBuilder(searchTerms.get(position).getDate_added()).append("  - ").append(searchTerms.get(position).getDescription()));
        holder.tvTitle.setText(searchTerms.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return searchTerms.size();
    }

    static class SearchTermViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvLink, tvDescription, tvTitle;

        public SearchTermViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLink = itemView.findViewById(R.id.tvLink);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvTitle = itemView.findViewById(R.id.tvTitle);
        }
    }
}
