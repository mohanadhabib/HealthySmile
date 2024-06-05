package com.buc.gradution.View.Activity.User;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.buc.gradution.R;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class UserScanImageActivity extends AppCompatActivity {
    private ImageView back,image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_scan_image);
        initComponents();
        String imageUrl = getIntent().getStringExtra("image");
        back.setOnClickListener(v -> finish());
        Picasso.get().load(imageUrl).into(image);
    }
    private void initComponents(){
        back = findViewById(R.id.back);
        image = findViewById(R.id.image);
    }
}