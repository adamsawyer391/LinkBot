package com.cosmic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.cosmic.linkbot.R;
import com.cosmic.model.Link;
import java.util.ArrayList;

public class LinkAdapter extends RecyclerView.Adapter<LinkAdapter.LinkViewHolder> {

    private final Context context;
    private final ArrayList<Link> linkItems;

    public LinkAdapter(Context context, ArrayList<Link> linkItems) {
        this.context = context;
        this.linkItems = linkItems;
    }

    @NonNull
    @Override
    public LinkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.link_item, parent, false);
        return new LinkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LinkViewHolder holder, int position) {
        holder.title.setText(linkItems.get(position).getTitle());
        holder.date.setText(linkItems.get(position).getDate_added());
        holder.url.setText(linkItems.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        if (linkItems != null){
            return linkItems.size();
        }else{
            return 0;
        }
    }

    static class LinkViewHolder extends RecyclerView.ViewHolder {

        private final TextView url, date, title;

        public LinkViewHolder(@NonNull View itemView) {
            super(itemView);
            url = itemView.findViewById(R.id.url);
            title = itemView.findViewById(R.id.title);
            date = itemView.findViewById(R.id.date_added);
        }
    }
}
