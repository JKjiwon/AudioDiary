package com.jwsoft.diary.feed;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.jwsoft.diary.R;

import java.io.File;

public class FullImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);

        getSupportActionBar().hide();
        Intent intent = getIntent();
        String photoPath = intent.getStringExtra("photoPath");

        ImageView photo = (ImageView) findViewById(R.id.fl_photo_image);
        File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Mydiary/picture/" + photoPath + ".jpeg");
        Glide.with(this)
                .load(Uri.fromFile(f))
                .apply(new RequestOptions().signature(new ObjectKey("signature string"))
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE))
                .into(photo);
        ImageView closeBtn = (ImageView) findViewById(R.id.fl_close_btn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}