package com.example.webczar.plantis;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

public class SearchActivity extends AppCompatActivity {

    private ImageView imageView;
    private static final String TAG = SearchActivity.class.getSimpleName();
private CollapsingToolbarLayout collapsingToolbarLayout = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        imageView = findViewById(R.id.iv_pic_id);
        Toolbar toolbar = findViewById(R.id.z_toolbar);
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        collapsingToolbarLayout.setTitle(SearchActivity.class.getSimpleName());

        Bitmap bm = myBitmap();
        if (bm != null){
            imageView.setImageBitmap(bm);
        }else{
            Toast.makeText(getApplicationContext(), "empty", Toast.LENGTH_SHORT).show();
        }

        dynamicToolbarColor(bm);
    }


    private void dynamicToolbarColor(Bitmap bm) {
        Palette.from(bm).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                collapsingToolbarLayout.setContentScrimColor(palette.getMutedColor(R.attr.colorPrimary));
                collapsingToolbarLayout.setStatusBarScrimColor(palette.getMutedColor(R.attr.colorPrimaryDark));
            }
        });
    }

    private Bitmap myBitmap() {
        Bundle b;
        b = getIntent().getExtras();
        Bitmap bitmap = null;
        if (b != null){
            byte[] image_bytes = b.getByteArray("imageByte");
            bitmap = BitmapFactory.decodeByteArray(image_bytes,0,image_bytes.length);
        }
        return bitmap;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SearchActivity.this,NavigateActivity.class));
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home){
            startActivity(new Intent(SearchActivity.this,NavigateActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
