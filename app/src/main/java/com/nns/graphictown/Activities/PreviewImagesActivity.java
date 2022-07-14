package com.nns.graphictown.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;

import com.nns.graphictown.Adapters.PreviewImagesPagerAdapter;
import com.nns.graphictown.R;

import java.util.List;

public class PreviewImagesActivity extends AppCompatActivity {
   private ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_images);
        viewPager=findViewById(R.id.preview_images_pager);
        List<String>imagesList=getIntent().getStringArrayListExtra("imagesList");
        PreviewImagesPagerAdapter adapter=new PreviewImagesPagerAdapter(imagesList,this);
        viewPager.setAdapter(adapter);
    }

    public void back(View view) {
        finish();
    }
}