package com.tupen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Objects;

public class Details extends AppCompatActivity {
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Toolbar toolbar = findViewById(R.id.note_toolbar);
        setSupportActionBar(toolbar);

        // Add a back button to the toolbar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow);

        // initializing db
        dbHelper = new DBHelper(this);

        Intent intent = getIntent();


//        init vars
        TextView title = findViewById(R.id.detail_title);
        title.setText(intent.getStringExtra("title"));
        TextView body = findViewById(R.id.detail_body);
        body.setText(intent.getStringExtra("body"));
//        ImageView image1 = findViewById(R.id.detail_image_1);
//        image1.setImageURI(Uri.parse(intent.getStringExtra("image_1")));
//        ImageView image2 = findViewById(R.id.detail_image_2);
//        image2.setImageURI(Uri.parse(intent.getStringExtra("image_2")));

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}