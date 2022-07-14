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
import com.nns.graphictown.Model.Order.OrderItem;
import com.nns.graphictown.R;

import java.util.List;

public class OrderItemsRecyclerAdapter  extends RecyclerView.Adapter<OrderItemsRecyclerAdapter.OrderItemHolder>{


    private List<OrderItem>orderItems;
    private Context context;
    private OnOrderItemListener onOrderItemListener;

    public interface  OnOrderItemListener{
        void onClick(List<String>imagesUrls);
    }
    public OrderItemsRecyclerAdapter(List<OrderItem> orderItems, Context context) {
        this.orderItems = orderItems;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_item, parent, false);
        return new OrderItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemHolder holder, int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOrderItemListener.onClick(orderItems.get(position).getImagesUrls());
            }});
        holder.productName.setText(orderItems.get(position).getProduct().getTitle());
        holder.productDimension.setText(orderItems.get(position).getProduct().getSize());
        holder.productPrice.setText(orderItems.get(position).getProduct().getPrice()+" "+context.getResources().getString(R.string.sar));
        Glide.with(context).load(orderItems.get(position).getProduct().getImagesUrls().get(0)).centerCrop().listener(new RequestListener<Drawable>() {
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
        return orderItems.size();
    }

    public void setOnOrderItemListener(OnOrderItemListener onOrderItemListener) {
        this.onOrderItemListener = onOrderItemListener;
    }

    class OrderItemHolder extends RecyclerView.ViewHolder {
        private RoundedImageView productImage;
        private SpinKitView spinKitView;
        private TextView productName, productDimension,productPrice;
        OrderItemHolder(@NonNull View itemView) {
            super(itemView);
            productImage=itemView.findViewById(R.id.subcategory_design_image);
            spinKitView=itemView.findViewById(R.id.spin_kit);
            productName=itemView.findViewById(R.id.subcategory_design_name);
            productDimension=itemView.findViewById(R.id.subcategory_design_size);
            productPrice=itemView.findViewById(R.id.subcategory_design_salary);
        }
    }
}
