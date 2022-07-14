package com.nns.graphictown.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.ybq.android.spinkit.SpinKitView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nns.graphictown.Model.category.CategoryData;
import com.nns.graphictown.R;

import java.util.List;

public class CompanyCategoryRecyclerAdapter extends RecyclerView.Adapter<CompanyCategoryRecyclerAdapter.CompanyCategoryHolder> {

    public interface OnCompanyCategoryClickListener {
        void onClick(CategoryData category);
    }

    private OnCompanyCategoryClickListener onCompanyCategoryClickListener;
    private List<CategoryData> categoryList;
    private Context context;

    public CompanyCategoryRecyclerAdapter(List<CategoryData> categoryList, Context context) {
        this.categoryList = categoryList;
        this.context = context;
    }

    @NonNull
    @Override
    public CompanyCategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_company, parent, false);
        return new CompanyCategoryHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CompanyCategoryHolder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCompanyCategoryClickListener.onClick(categoryList.get(position));
            }
        });
        holder.name.setText(categoryList.get(position).getTitle());
        Glide.with(context).load(categoryList.get(position).getImageUrl()).centerCrop().listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }
            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                holder.spinKitView.setVisibility(View.GONE);
                return false;
            }
        }).into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public void setOnCompanyCategoryClickListener(OnCompanyCategoryClickListener onCompanyCategoryClickListener) {
        this.onCompanyCategoryClickListener = onCompanyCategoryClickListener;
    }

    class CompanyCategoryHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private RoundedImageView imageView;
        private SpinKitView spinKitView;
        public CompanyCategoryHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.company_category_name_item);
            imageView=itemView.findViewById(R.id.category_image_item);
            spinKitView=itemView.findViewById(R.id.spin_kit);

        }
    }
}
