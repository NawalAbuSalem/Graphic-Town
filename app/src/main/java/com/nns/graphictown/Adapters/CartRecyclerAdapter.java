package com.nns.graphictown.Adapters;

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
import com.nns.graphictown.Model.Cart.CartItem;
import com.nns.graphictown.Model.Cart.Item;
import com.nns.graphictown.R;

import java.util.List;

public class CartRecyclerAdapter extends RecyclerView.Adapter<CartRecyclerAdapter.CartHolder> {

    public interface OnCartItemClickListener {
        void deleteItem(int position, Item cartItem);

        void updateQuantity(int quantity, int position, Item cartItem);

        void showAllImages(Item cartItem);
    }

    private OnCartItemClickListener onCartItemClickListener;
    private List<Item> cartItems;
    private Context context;

    public CartRecyclerAdapter(List<Item> cartItems, Context context) {
        this.cartItems = cartItems;
        this.context = context;
    }

    public void setOnCartItemClickListener(OnCartItemClickListener onCartItemClickListener) {
        this.onCartItemClickListener = onCartItemClickListener;
    }

    @NonNull
    @Override
    public CartHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartHolder holder, final int position) {
        holder.name.setText(cartItems.get(position).getProduct().getTitle());
        holder.date.setText(cartItems.get(position).getCreatedAt());
        holder.size.setText(cartItems.get(position).getSize());
        double totalPrice = cartItems.get(position).getProduct().getPrice() * cartItems.get(position).getAmount();
        holder.totalPrice.setText(totalPrice + " " +context.getResources().getString(R.string.sar));
        holder.quantity.setText(String.valueOf(cartItems.get(position).getAmount()));
        holder.unitPrice.setText(cartItems.get(position).getProduct().getPrice()+ " " +context.getResources().getString(R.string.sar));
        String imageUrl=cartItems.get(position).getImagesUrls().get(0);
        if (cartItems.get(position).getImagesUrls().get(0).startsWith("\"")){
            imageUrl=cartItems.get(position).getImagesUrls().get(0).substring(1,cartItems.get(position).getImagesUrls().get(0).length()-1);
        }
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
        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCartItemClickListener.updateQuantity((cartItems.get(position).getAmount() + 1), position, cartItems.get(position));
            }
        });
        holder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCartItemClickListener.updateQuantity((cartItems.get(position).getAmount() - 1), position, cartItems.get(position));
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCartItemClickListener.deleteItem(position,cartItems.get(position));
            }
        });
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCartItemClickListener.showAllImages(cartItems.get(position));
            }
        });
    }



    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    class CartHolder extends RecyclerView.ViewHolder {
        private TextView name, date, unitPrice, totalPrice, size, quantity;
        private ImageView image, delete, add, minus;
        private SpinKitView spinKitView;
        public CartHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.cart_item_name);
            date = itemView.findViewById(R.id.cart_item_dare);
            unitPrice = itemView.findViewById(R.id.cart_item_price);
            totalPrice = itemView.findViewById(R.id.cart_item_total_price);
            size = itemView.findViewById(R.id.cart_item_size);
            quantity = itemView.findViewById(R.id.cart_item_quantity);
            image = itemView.findViewById(R.id.cart_item_image);
            delete = itemView.findViewById(R.id.cart_item_delete);
            add = itemView.findViewById(R.id.cart_item_add);
            minus = itemView.findViewById(R.id.cart_item_minus);
            spinKitView = itemView.findViewById(R.id.spin_kit);
        }
    }
}
