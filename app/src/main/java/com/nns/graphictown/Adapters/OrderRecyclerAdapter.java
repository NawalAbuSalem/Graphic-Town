package com.nns.graphictown.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.nns.graphictown.Model.Order.OrderData;
import com.nns.graphictown.R;

import java.util.List;

public class OrderRecyclerAdapter extends RecyclerView.Adapter<OrderRecyclerAdapter.OrderHolder> {

    private List<OrderData> orderItems;
    private Context context;

    public  interface OnOrderClickListener{
        void onClick(OrderData order);
    }
    private OnOrderClickListener onOrderClickListener;
    public OrderRecyclerAdapter(List<OrderData> orderItems, Context context) {
        this.orderItems = orderItems;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);
        return new OrderHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull OrderHolder holder, int position) {
        holder.number.setText("#" + orderItems.get(position).getOrderNumber());
        holder.orderDate.setText(orderItems.get(position).getOrderDate());
        holder.deliveryDate.setText(String.valueOf(orderItems.get(position).getOrderDeliveryDate()));
        holder.status.setText(String.valueOf(orderItems.get(position).getStatusName()));
        String imageUrl=orderItems.get(position).getItems().get(0).getProduct().getImagesUrls().get(0);
        Glide.with(context).load(imageUrl).centerCrop().listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                System.out.println(e.getMessage());
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                holder.spinKitView.setVisibility(View.GONE);
                return false;
            }
        }).into(holder.image);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOrderClickListener.onClick(orderItems.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    public void setOnOrderClickListener(OnOrderClickListener onOrderClickListener) {
        this.onOrderClickListener = onOrderClickListener;
    }

    class OrderHolder extends RecyclerView.ViewHolder {
        private TextView number, orderDate, deliveryDate, status;
        private ImageView image;
        private SpinKitView spinKitView;
        OrderHolder(@NonNull View itemView) {
            super(itemView);
            number = itemView.findViewById(R.id.order_number);
            orderDate = itemView.findViewById(R.id.order_date);
            deliveryDate = itemView.findViewById(R.id.order_delivery_date);
            status = itemView.findViewById(R.id.order_status);
            spinKitView = itemView.findViewById(R.id.spin_kit);
            image = itemView.findViewById(R.id.order_image);
        }
    }
}
