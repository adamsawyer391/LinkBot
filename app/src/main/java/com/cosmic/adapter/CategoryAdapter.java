package com.cosmic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cosmic.linkbot.R;
import com.cosmic.model.Category;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private final Context context;
    private final ArrayList<Category> categories;

    public CategoryAdapter(Context context, ArrayList<Category> categories) {
        this.context = context;
        this.categories = categories;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.category_item, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        holder.categoryName.setText(categories.get(position).getName());
        if (categories.get(position).getUrl().equals("")){
            Glide.with(context).load("").placeholder(R.mipmap.ic_launcher_round).into(holder.categoryPhoto);
        }else{
            Glide.with(context).load(categories.get(position).getUrl()).into(holder.categoryPhoto);
        }
    }

    @Override
    public int getItemCount() {
        if (categories != null){
            return categories.size();
        }else{
            return 0;
        }
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {

        private final TextView categoryName;
        private final CircleImageView categoryPhoto;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryPhoto = itemView.findViewById(R.id.categoryPhoto);
            categoryName = itemView.findViewById(R.id.categoryName);
        }
    }
}
