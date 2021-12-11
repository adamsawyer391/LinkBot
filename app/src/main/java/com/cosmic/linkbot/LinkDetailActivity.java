package com.cosmic.linkbot;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.cosmic.model.Link;

public class LinkDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private Context context;
    private TextView page_title, page_url, page_description;
    private String category_name = "";
    private String cat_key = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_detail);
        context = LinkDetailActivity.this;

        initUI();
        getIntentExtra();
    }

    private void getIntentExtra(){
        Intent intent = getIntent();
        if (intent != null){
            Bundle bundle = intent.getExtras();
            Link link = (Link) bundle.getSerializable("link");
            page_title.setText(link.getTitle());
            page_url.setText(link.getUrl());
            page_description.setText(link.getDescription());
            category_name = intent.getExtras().getString("category_name");
            cat_key = intent.getExtras().getString("category_key");
        }
    }

    private void initUI(){
        page_title = findViewById(R.id.page_title);
        page_url = findViewById(R.id.page_url);
        page_description = findViewById(R.id.page_description);
        ImageView menu_detail = findViewById(R.id.menu_detail);
        menu_detail.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(context, CategoryActivity.class);
        intent.putExtra("category_name", category_name);
        intent.putExtra("category_key", cat_key);
        startActivity(intent);
        finish();
        Animatoo.animateSlideLeft(context);
    }

    @Override
    public void onClick(View v) {

    }
}
