package com.cosmic.linkbot;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.cosmic.adapter.LinkAdapter;
import com.cosmic.interfaces.LoaderLinks;
import com.cosmic.model.Link;
import com.cosmic.utils.RecycleItemClick;
import com.cosmic.viewmodels.LinkViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@SuppressWarnings("rawtypes")
public class CategoryActivity extends AppCompatActivity implements LoaderLinks, MenuItem.OnMenuItemClickListener {

    private static final String TAG = "CategoryActivity";

    private Context context;
    private LinkViewModel linkViewModel;
    private RecyclerView recyclerView;
    private LinkAdapter linkAdapter;
    private String cat_key;
    private String category_name = "";
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final String currentUserID = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private boolean hasIncomingIntent;
    private String url;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        context = CategoryActivity.this;

        getIntentExtra();

        ImageView imageView = findViewById(R.id.category_menu);
        imageView.setOnClickListener(this::openPopupMenu);

        FloatingActionButton floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddLinkActivity.class);
            intent.putExtra("category", cat_key);
            intent.putExtra("category_name", category_name);
            startActivity(intent);
            Animatoo.animateSlideLeft(context);
        });
    }

    private void getIntentExtra(){
        Intent intent = getIntent();
        if (intent != null){
            category_name = intent.getExtras().getString("category_name");
            cat_key = intent.getExtras().getString("category_key");
//            if (intent.hasExtra("contains_extra")){
//                hasIncomingIntent = intent.getBooleanExtra("contains_extra", false);
//                if (hasIncomingIntent){
//                    url = intent.getExtras().getString("extra_url");
//                }
//            }
            initLinkViewModel();
            populateRecyclerView();

            TextView textView = findViewById(R.id.categoryName);
            textView.setText(category_name);
        }else{
            Log.d(TAG, "getIntentExtra: intent was NULL");
        }
    }

    private void populateRecyclerView(){
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        linkAdapter = new LinkAdapter(context, linkViewModel.getAllLinks().getValue());
        recyclerView.setAdapter(linkAdapter);

        recyclerViewTouchListener();
    }

    private void recyclerViewTouchListener(){
        recyclerView.addOnItemTouchListener(new RecycleItemClick(context, (view, position) -> activityTransition(LinkDetailActivity.class, position)));
    }

    private void initLinkViewModel(){
        linkViewModel = new ViewModelProvider(this).get(LinkViewModel.class);
        linkViewModel.connectToLinkViewModel(context, cat_key);
    }

    private void activityTransition(Class activity, int position){
        System.out.println(activity);
        Intent intent = new Intent(context, activity);
        if (activity.equals(LinkDetailActivity.class)){
            try{
//                Link link = linkViewModel.getAllLinks().getValue().get(position);
                Link link = Objects.requireNonNull(linkViewModel.getAllLinks().getValue()).get(position);
                Bundle bundle = new Bundle();
                bundle.putSerializable("link", link);
                bundle.putInt("link_position", position);
                bundle.putString("category_name", category_name);
                bundle.putString("category_key", cat_key);
                intent.putExtras(bundle);
            }catch (IndexOutOfBoundsException e){
                displayToast(e.getMessage());
            }
        }
        startActivity(intent);
        if (activity.equals(MainActivity.class)){
            finish();
        }
        Animatoo.animateSlideLeft(context);
    }

    private void openPopupMenu(View view){
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.setOnMenuItemClickListener(this::onMenuItemClick);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.category_menu, popupMenu.getMenu());
        popupMenu.show();
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        activityTransition(MainActivity.class, 0);
    }

    @Override
    public void onLinksLoaded() {
        linkViewModel.getAllLinks().observe(this, links -> {
            linkAdapter.notifyDataSetChanged();
            int firstPosition = links.size();
            recyclerView.smoothScrollToPosition(firstPosition);
        });
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if ("Add Category Image".equals(item.getTitle().toString())) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            startActivityForResult(intent, 0);
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK){
            Uri selectedImage = null;
            if (data != null) {
                selectedImage = data.getData();
            }
            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                uploadImageToFirebaseStorage(bitmap);
            }catch (IOException e){
                displayToast(e.getMessage());
            }
        }else{
            displayToast("An error occurred, please try again");
        }
    }

    private void uploadImageToFirebaseStorage(Bitmap bitmap){
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child("category_image").child(currentUserID);
        final String fileName = UUID.randomUUID().toString();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = storageReference.child(fileName).putBytes(data);
        uploadTask.addOnFailureListener(e ->
                displayToast("Something went wrong, please try again")).addOnSuccessListener(taskSnapshot ->
                storageReference.child(fileName).getDownloadUrl().addOnSuccessListener(uri -> {
                    String url = uri.toString();
                    databaseReference.child("categories").child(currentUserID).child(cat_key).child("url").setValue(url).addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            Intent intent = new Intent(context, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    });
                }));
    }

    private void displayToast(String message){
        int duration = Toast.LENGTH_SHORT;
        Toast.makeText(context, message, duration).show();
    }
}
