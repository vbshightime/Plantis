package com.example.webczar.plantis;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.webczar.plantis.Fragments.SoilMonitor;

import java.io.ByteArrayOutputStream;

public class FrontActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front2);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
            finish();
                Intent intent = new Intent(FrontActivity.this, NavigateActivity.class);
                startActivity(intent);
            }
        }, 5000);
    }
}
