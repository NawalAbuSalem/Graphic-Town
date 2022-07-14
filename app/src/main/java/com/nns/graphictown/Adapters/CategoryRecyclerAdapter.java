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

public class CategoryRecyclerAdapter extends RecyclerView.Adapter<CategoryRecyclerAdapter.CategoryHolder> {
    public interface OnCategoryClickListener {
        void onClick(CategoryData category);
    }

    private OnCategoryClickListener onCategoryClickListener;
    private List<CategoryData> categoryList;
    private Context context;

    public CategoryRecyclerAdapter(List<CategoryData> categoryList, Context context) {
        this.categoryList = categoryList;
        this.context = context;
    }

    @NonNull
    @Override
    public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_main, parent, false);
        return new CategoryHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CategoryHolder holder, final int position) {
        holder.categoryName.setText(categoryList.get(position).getTitle());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCategoryClickListener.onClick(categoryList.get(position));
            }
        });
        Glide.with(context).load(categoryList.get(position).getImageUrl()).centerCrop().listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }
            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                holder.spin_kit.setVisibility(View.GONE);
                return false;
            }
        }).into(holder.categoryImage);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public void setOnCategoryClickListener(OnCategoryClickListener onCategoryClickListener) {
        this.onCategoryClickListener = onCategoryClickListener;
    }

    class CategoryHolder extends RecyclerView.ViewHolder {
        private TextView categoryName;
        private RoundedImageView categoryImage;
        private SpinKitView spin_kit;

        public CategoryHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.category_name_item);
            categoryImage = itemView.findViewById(R.id.category_image_item);
            spin_kit = itemView.findViewById(R.id.spin_kit);
        }
    }
}
