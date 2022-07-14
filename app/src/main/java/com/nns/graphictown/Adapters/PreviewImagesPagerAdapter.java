package com.nns.graphictown.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.nns.graphictown.R;

import java.util.List;

public class PreviewImagesPagerAdapter extends PagerAdapter {
    private List<String> pages;
    private Context context;

    public PreviewImagesPagerAdapter(List<String> pages, Context context) {
        this.pages = pages;
        this.context = context;
    }
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view= LayoutInflater.from(container.getContext()).inflate(R.layout.preview_images_page,container,false);
        ImageView pagerImage=view.findViewById(R.id.preview_images_item);
        String imageUrl=pages.get(position);
        if (pages.get(position).startsWith("\"")){
            imageUrl=pages.get(position).substring(1,pages.get(position).length()-1);
        }
        Glide.with(context).load(imageUrl).into(pagerImage);
        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return pages.size();
    }
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
