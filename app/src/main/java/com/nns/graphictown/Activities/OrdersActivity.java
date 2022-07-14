package com.nns.graphictown.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nns.graphictown.Adapters.OrderRecyclerAdapter;
import com.nns.graphictown.Helpers.PreferenceHelper;
import com.nns.graphictown.Model.Order.Order;
import com.nns.graphictown.Model.Order.OrderData;
import com.nns.graphictown.Model.Order.OrderItem;
import com.nns.graphictown.R;
import com.nns.graphictown.models.CategoryNetworkUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrdersActivity extends AppCompatActivity {
    private RecyclerView ordersRecyclerView;
    private PreferenceHelper preferenceHelper;
    private CategoryNetworkUtils networkUtils;
    private ProgressBar progressBar;
    private LinearLayout errorLayout, emptyLayout;
    private List<OrderData> orderItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        errorLayout = findViewById(R.id.no_internet_layout);
        emptyLayout = findViewById(R.id.empty_cart);
        progressBar = findViewById(R.id.progress_bar);
        ordersRecyclerView = findViewById(R.id.orders_recyclerview);
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        preferenceHelper = new PreferenceHelper(this);
        networkUtils = CategoryNetworkUtils.getInstance(this);
        addCartCount();
        showOrders();
    }

    private void showErrorMessage() {
        errorLayout.setVisibility(View.VISIBLE);
        Button refreshButton = findViewById(R.id.refresh_button);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorLayout.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                showOrders();

            }
        });
    }

    private void showOrders() {
        progressBar.setVisibility(View.VISIBLE);
        Call<Order> call = networkUtils.getTownApiInterface().getOrders();
        call.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if (response.isSuccessful()) {
                    errorLayout.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    if (response.body() != null && response.body().getData() != null) {
                        orderItems = response.body().getData();
                        if (!orderItems.isEmpty()) {
                            addOrderRecyclerAdapter();
                        } else {
                            emptyLayout.setVisibility(View.VISIBLE);
                        }
                    } else {
                        emptyLayout.setVisibility(View.VISIBLE);
                    }
                }
            }
            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                System.out.println(t.getMessage());
                showErrorMessage();
            }
        });
    }

    private void addOrderRecyclerAdapter() {
      OrderRecyclerAdapter orderRecyclerAdapter = new OrderRecyclerAdapter(orderItems,this);
      ordersRecyclerView.setAdapter(orderRecyclerAdapter);
      orderRecyclerAdapter.setOnOrderClickListener(new OrderRecyclerAdapter.OnOrderClickListener() {
          @Override
          public void onClick(OrderData order) {
              Intent intent=new Intent(OrdersActivity.this,OrderDetailsActivity.class);
              intent.putExtra("order",order);
              startActivity(intent);
          }
      });
    }

    private void addCartCount() {
        TextView countTextView = findViewById(R.id.cart_item_count);
        String s = preferenceHelper.getCartCount();
        if (s != null) {
            int count = Integer.parseInt(s);
            if (count > 99) {
                countTextView.setText("+" + s);
            } else {
                countTextView.setText(s);
            }
        }
    }

    public void back(View view) {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
        super.onBackPressed();
    }

    public void viewCart(View view) {
        startActivity(new Intent(this,CartActivity.class));
    }
}