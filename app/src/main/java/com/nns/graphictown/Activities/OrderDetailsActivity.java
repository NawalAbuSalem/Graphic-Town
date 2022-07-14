package com.nns.graphictown.Activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nns.graphictown.Adapters.OrderItemsRecyclerAdapter;
import com.nns.graphictown.Model.Order.OrderData;
import com.nns.graphictown.R;
import com.nns.graphictown.models.CategoryNetworkUtils;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailsActivity extends AppCompatActivity {
    private OrderData currentOrder;
    private TextView orderNumber, orderDate, orderStatus, paymentMethodName, orderPrice, deliveryPrice, orderTotalAmount, taxAmount, taxNumber;
    private ImageView paymentMethodImage;
    private RecyclerView recyclerView;
    private RatingBar ratingBar;
    private Dialog waitingDialog, ratingDialog;
    private CategoryNetworkUtils networkUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        currentOrder = (OrderData) getIntent().getSerializableExtra("order");
        orderNumber = findViewById(R.id.order_number);
        orderDate = findViewById(R.id.order_date);
        orderStatus = findViewById(R.id.order_status);
        orderPrice = findViewById(R.id.order_price);
        deliveryPrice = findViewById(R.id.delivery_price);
        orderTotalAmount = findViewById(R.id.order_total_price);
        taxAmount = findViewById(R.id.tax_amount);
        taxNumber = findViewById(R.id.tax_number);
        ratingBar = findViewById(R.id.rating_bar);
        recyclerView = findViewById(R.id.orders_recyclerview);
        paymentMethodName = findViewById(R.id.payment_method_name);
        paymentMethodImage = findViewById(R.id.payment_method_image);
        networkUtils = CategoryNetworkUtils.getInstance(this);
        showOrderDetails();
        createWaitingDialog();


    }

    private void createWaitingDialog() {
        waitingDialog = new Dialog(this, R.style.SheetDialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_waiting, null);
        TextView waitingMessage = view.findViewById(R.id.waiting_message);
        waitingMessage.setText(getResources().getString(R.string.ordering));
        waitingDialog.setContentView(view);
        waitingDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        waitingDialog.setCancelable(false);
    }

    private void createRatingDialog() {
        ratingDialog = new Dialog(this, R.style.SheetDialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_rating, null);
        ratingDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Button cancel = view.findViewById(R.id.cancel);
        Button confirm = view.findViewById(R.id.ok);
        RatingBar newRatingBar = view.findViewById(R.id.rating_bar_2);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ratingDialog.dismiss();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ratingDialog.dismiss();
                if (newRatingBar.getProgress()!= 0) {
                    waitingDialog.show();
                    Call<ResponseBody> call = networkUtils.getTownApiInterface().rateOrder(currentOrder.getId(), newRatingBar.getProgress());
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            waitingDialog.dismiss();
                            if (response.isSuccessful()) {
                                ratingBar.setRating(newRatingBar.getRating());
                            }
                        }
                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            waitingDialog.dismiss();
                            Toast.makeText(OrderDetailsActivity.this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });
        ratingDialog.setContentView(view);

    }


    @SuppressLint("SetTextI18n")
    private void showOrderDetails() {
        orderNumber.setText("#" + currentOrder.getOrderNumber());
        orderDate.setText(currentOrder.getOrderDate());
        orderStatus.setText(currentOrder.getStatusName());
        OrderItemsRecyclerAdapter orderItemsRecyclerAdapter = new OrderItemsRecyclerAdapter(currentOrder.getItems(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(OrderDetailsActivity.this, RecyclerView.HORIZONTAL, false));
        recyclerView.setAdapter(orderItemsRecyclerAdapter);
        orderItemsRecyclerAdapter.setOnOrderItemListener(new OrderItemsRecyclerAdapter.OnOrderItemListener() {
            @Override
            public void onClick(List<String> imagesUrls) {
                ArrayList<String> images = new ArrayList<>();
                images.addAll(imagesUrls);
                Intent intent = new Intent(OrderDetailsActivity.this, PreviewImagesActivity.class);
                intent.putStringArrayListExtra("imagesList", images);
                startActivity(intent);
            }
        });
        if (currentOrder.getPaymentMethod().toLowerCase().equalsIgnoreCase("cash") || currentOrder.getPaymentMethod().toLowerCase().equalsIgnoreCase("دفع نقدي")) {
            paymentMethodImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_cash));
            paymentMethodName.setText(getResources().getString(R.string.Cash));
        }
        orderPrice.setText(currentOrder.getTotalAmount() + " " + getResources().getString(R.string.sar));
        deliveryPrice.setText(currentOrder.getDeliveryAmount() + " " + getResources().getString(R.string.sar));
        double total = currentOrder.getTotalAmount() + currentOrder.getDeliveryAmount() + Math.ceil((currentOrder.getTotalAmount() * (currentOrder.getFees() / 100)));
        orderTotalAmount.setText(total + " " + getResources().getString(R.string.sar));
        taxAmount.setText("*(" + getResources().getString(R.string.including_fee) + " " + currentOrder.getFees() + "%: " + Math.ceil((currentOrder.getTotalAmount() * (currentOrder.getFees() / 100))) + " " + getResources().getString(R.string.sar) + ")");
        taxNumber.setText(getResources().getString(R.string.tax_number) + " " + currentOrder.getVatNumber());
        if (currentOrder.getRating() != null) {
            ratingBar.setRating((float) currentOrder.getRating());
        }
    }

    public void back(View view) {
        finish();
    }

    public void rateOrder(View view) {
            createRatingDialog();
            ratingDialog.show();
    }

}