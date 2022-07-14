package com.nns.graphictown.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.ybq.android.spinkit.SpinKitView;
import com.nns.graphictown.Model.advertisement.AdvertisementData;
import com.nns.graphictown.R;

import java.util.List;

public class HomePagerAdapter extends PagerAdapter {

    private List<AdvertisementData>pagers;
    private Context context;

    public HomePagerAdapter(List<AdvertisementData> pagers, Context context) {
        this.pagers = pagers;
        this.context = context;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view= LayoutInflater.from(container.getContext()).inflate(R.layout.item_home_pager,container,false);
        ImageView pagerImage=view.findViewById(R.id.home_pager_image);
        final SpinKitView  spinKitView = view.findViewById(R.id.spin_kit);;
        Glide.with(context).load(pagers.get(position).getImageUrl()).centerCrop().listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }
            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                spinKitView.setVisibility(View.GONE);
                return false;
            }
        }).into(pagerImage);
        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return pagers.size();
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
