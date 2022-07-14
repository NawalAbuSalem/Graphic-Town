package com.nns.graphictown.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nns.graphictown.Adapters.CartRecyclerAdapter;
import com.nns.graphictown.Helpers.PreferenceHelper;
import com.nns.graphictown.Model.Cart.CartItem;
import com.nns.graphictown.Model.Cart.Item;
import com.nns.graphictown.R;
import com.nns.graphictown.models.CategoryNetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity {
    private RecyclerView cartRecyclerView;
    private CategoryNetworkUtils networkUtils;
    private LinearLayout errorLayout, emptyCartLayout;
    private ProgressBar progressBar;
    private List<Item> cartItems;
    private FrameLayout cartMainContent;
    private CartRecyclerAdapter cartRecyclerAdapter;
    private PreferenceHelper preferenceHelper;
    private Dialog waitingDialog, deletionDialog;
    private TextView totalPriceTextView;
    private int cartId;
    private double totalAmountWithFee;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        errorLayout = findViewById(R.id.no_internet_layout);
        cartRecyclerView = findViewById(R.id.cart_recyclerview);
        cartMainContent = findViewById(R.id.cart_main_content);
        emptyCartLayout = findViewById(R.id.empty_cart);
        totalPriceTextView = findViewById(R.id.total_price_cart_items);
        progressBar = findViewById(R.id.progress_bar);
        preferenceHelper = new PreferenceHelper(this);
        cartRecyclerAdapter = new CartRecyclerAdapter(cartItems, this);
        networkUtils = CategoryNetworkUtils.getInstance(this);
        cartItems = new ArrayList<>();
        loadCartItems();

    }

    private void createWaitingDialog(String message) {
        waitingDialog = new Dialog(this, R.style.SheetDialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_waiting, null);
        TextView waitingMessage = view.findViewById(R.id.waiting_message);
        waitingMessage.setText(message);
        waitingDialog.setContentView(view);
        waitingDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        waitingDialog.setCancelable(false);
    }

    private void createDeletionDialog(final int position, Item cartItem) {
        deletionDialog = new Dialog(this, R.style.SheetDialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_cart_item_deletion, null);
        Button cancelDeletion = view.findViewById(R.id.dialog_cart_deletion_no);
        Button confirmDeletion = view.findViewById(R.id.dialog_cart_deletion_yes);
        cancelDeletion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletionDialog.dismiss();
            }
        });
        confirmDeletion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createWaitingDialog(getResources().getString(R.string.deleting_item));
                deleteCartItem(position, cartItem);
            }
        });
        deletionDialog.setContentView(view);
        deletionDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        deletionDialog.setCancelable(false);
    }

    private void deleteCartItem(final int position, Item cartItem) {
        deletionDialog.dismiss();
        waitingDialog.show();
        Call<ResponseBody> call = networkUtils.getTownApiInterface().deleteCartItem(cartItem.getId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                waitingDialog.dismiss();
                if (response.isSuccessful()) {
                    try {
                        String responseString = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseString);
                        JSONArray items = jsonObject.getJSONObject("data").getJSONArray("items");
                        preferenceHelper.setCartCount(String.valueOf(items.length()));
                        Toast.makeText(CartActivity.this, getResources().getString(R.string.the_design_is_deleted_successfully), Toast.LENGTH_SHORT).show();
                        cartItems.remove(position);
                        cartRecyclerAdapter.notifyItemRemoved(position);
                        if (cartItems.isEmpty()){
                            emptyCartLayout.setVisibility(View.VISIBLE);
                            cartMainContent.setVisibility(View.GONE);
                        }
                        double amount = jsonObject.getJSONObject("data").getDouble("total_amount");
                        totalAmountWithFee = jsonObject.getJSONObject("data").getDouble("total_amount_with_fees");
                        totalPriceTextView.setText(amount + " " + getResources().getString(R.string.sar));
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        System.out.println(e.getMessage());
                        Toast.makeText(CartActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                waitingDialog.dismiss();
                Toast.makeText(CartActivity.this, getResources().getString(R.string.the_operation_is_failed), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void calculateTotalPrice(CartItem item) {

        totalPriceTextView.setText(item.getData().get(0).getTotalAmount() + " " + getResources().getString(R.string.sar));
        totalAmountWithFee=item.getData().get(0).getTotalAmountWithFee();
    }

    private void showErrorMessage() {
        errorLayout.setVisibility(View.VISIBLE);
        Button refreshButton = findViewById(R.id.refresh_button);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorLayout.setVisibility(View.GONE);
                cartMainContent.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                loadCartItems();
            }
        });
    }

    private void loadCartItems() {
        Call<CartItem> call = networkUtils.getTownApiInterface().getCartItems();
        call.enqueue(new Callback<CartItem>() {
            @Override
            public void onResponse(Call<CartItem> call, Response<CartItem> response) {
                if (response.isSuccessful()) {
                    errorLayout.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    if (response.body() != null && response.body().getData() != null) {
                        cartItems = response.body().getData().get(0).getItems();
                        if (!cartItems.isEmpty()){
                            cartId=response.body().getData().get(0).getId();
                            totalAmountWithFee=response.body().getData().get(0).getTotalAmountWithFee();
                            addCartRecyclerAdapter();
                            calculateTotalPrice(response.body());
                        }else {
                            emptyCartLayout.setVisibility(View.VISIBLE);
                        }
                    } else {
                        emptyCartLayout.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<CartItem> call, Throwable t) {
                System.out.println(t.getMessage());
                progressBar.setVisibility(View.GONE);
                showErrorMessage();
            }
        });
    }

    private void addCartRecyclerAdapter() {
        cartMainContent.setVisibility(View.VISIBLE);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartRecyclerAdapter = new CartRecyclerAdapter(cartItems, this);
        cartRecyclerAdapter.setOnCartItemClickListener(new CartRecyclerAdapter.OnCartItemClickListener() {
            @Override
            public void deleteItem(int position, Item cartItem) {
                createDeletionDialog(position, cartItem);
                deletionDialog.show();
            }

            @Override
            public void updateQuantity(int quantity, int position, Item cartItem) {
                if (quantity >= 1) {
                    createWaitingDialog(getResources().getString(R.string.cart_upadating));
                    updateCartItemQuantity(quantity, position, cartItem);
                } else {
                    Toast.makeText(CartActivity.this, getResources().getString(R.string.invalid_quantity), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void showAllImages(Item cartItem) {
                ArrayList<String> images = new ArrayList<>();
                images.addAll(cartItem.getImagesUrls());
                Intent intent = new Intent(CartActivity.this, PreviewImagesActivity.class);
                intent.putStringArrayListExtra("imagesList", images);
                startActivity(intent);
            }
        });
        cartRecyclerView.setAdapter(cartRecyclerAdapter);


    }

    private void updateCartItemQuantity(final int quantity, final int position, Item cartItem) {
        waitingDialog.show();
        Call<ResponseBody> call = networkUtils.getTownApiInterface().updateCartQuantity(cartItem.getId(), quantity);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                waitingDialog.dismiss();
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        try {
                            String responseString = response.body().string();
                            JSONObject jsonObject = new JSONObject(responseString);
                            JSONArray items = jsonObject.getJSONObject("data").getJSONArray("items");
                            preferenceHelper.setCartCount(String.valueOf(items.length()));
                            Toast.makeText(CartActivity.this, getResources().getString(R.string.the_quantity_is_updated_successflly), Toast.LENGTH_SHORT).show();
                            cartItems.get(position).setAmount(quantity);
                            cartRecyclerAdapter.notifyItemChanged(position);
                            double totalAmount = jsonObject.getJSONObject("data").getDouble("total_amount");
                            totalAmountWithFee=jsonObject.getJSONObject("data").getDouble("total_amount_with_fees");
                            totalPriceTextView.setText(totalAmount + " " + getResources().getString(R.string.sar));
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(CartActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                waitingDialog.dismiss();
                Toast.makeText(CartActivity.this, getResources().getString(R.string.the_operation_is_failed), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void back(View view) {
        setResult(RESULT_OK);
        finish();
    }

    public void pay(View view) {
        Intent intent=new Intent(this, UserLocationActivity.class);
        intent.putExtra("id",cartId);
        intent.putExtra("price",totalAmountWithFee);
        startActivity(intent);

    }

    public void keepShopping(View view) {
        startActivity(new Intent(this, MainActivity.class));
        finishAffinity();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }
}
