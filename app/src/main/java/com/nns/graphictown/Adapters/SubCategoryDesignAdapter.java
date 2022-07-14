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
import com.nns.graphictown.Model.Product.Product;
import com.nns.graphictown.Model.Product.ProductData;
import com.nns.graphictown.R;

import java.util.List;

public class SubCategoryDesignAdapter extends RecyclerView.Adapter<SubCategoryDesignAdapter.SubCategoryDesignHolder> {

    private OnSubCategoryDesignClickListener onSubCategoryDesignClickListener;
    private List<ProductData>productList;
    private Context context;
    public interface OnSubCategoryDesignClickListener {
        void onClick(ProductData product);
    }

    public void setOnSubCategoryDesignClickListener(OnSubCategoryDesignClickListener onSubCategoryDesignClickListener) {
        this.onSubCategoryDesignClickListener = onSubCategoryDesignClickListener;
    }

    public SubCategoryDesignAdapter(List<ProductData> productList, Context context) {
        this.productList = productList;
        this.context = context;
    }

    @NonNull
    @Override
    public SubCategoryDesignHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subcategory_design, parent, false);
        return new SubCategoryDesignHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SubCategoryDesignHolder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              onSubCategoryDesignClickListener.onClick(productList.get(position));
          }});
        holder.productName.setText(productList.get(position).getTitle());
        holder.productDimension.setText(productList.get(position).getSize());
        holder.productPrice.setText(productList.get(position).getPrice()+" "+context.getResources().getString(R.string.sar));
        Glide.with(context).load(productList.get(position).getImagesUrls().get(0)).centerCrop().listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }
            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                holder.spinKitView.setVisibility(View.GONE);
                return false;
            }
        }).into(holder.productImage);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class SubCategoryDesignHolder extends RecyclerView.ViewHolder {
       private RoundedImageView productImage;
       private SpinKitView spinKitView;
       private TextView productName, productDimension,productPrice;
        SubCategoryDesignHolder(@NonNull View itemView) {
            super(itemView);
            productImage=itemView.findViewById(R.id.subcategory_design_image);
            spinKitView=itemView.findViewById(R.id.spin_kit);
            productName=itemView.findViewById(R.id.subcategory_design_name);
            productDimension=itemView.findViewById(R.id.subcategory_design_size);
            productPrice=itemView.findViewById(R.id.subcategory_design_salary);
        }
    }
}
