package com.nns.graphictown.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.nns.graphictown.Helpers.Constants;
import com.nns.graphictown.Helpers.PreferenceHelper;
import com.nns.graphictown.Model.Cart.CartItem;
import com.nns.graphictown.Model.Product.Product;
import com.nns.graphictown.Model.Product.ProductData;
import com.nns.graphictown.R;
import com.nns.graphictown.models.CartNetworkUtils;
import com.nns.graphictown.models.CategoryNetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowSelectedImageActivity extends AppCompatActivity {

    private List<String> selectedImagesList;
    private ImageView selectedImageView;
    private TextView numberOfImagesTextView;
    private int currentImage;
    private PreferenceHelper preferenceHelper;
    private Dialog loginDialog,waitingDialog,cartDialog;
    private CategoryNetworkUtils networkUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_selected_image);
        selectedImagesList = getIntent().getStringArrayListExtra(Constants.SELECTED_IMAGE_LIST);
        selectedImageView = findViewById(R.id.show_selected_image_view);
        numberOfImagesTextView = findViewById(R.id.show_selected_text);
        preferenceHelper = new PreferenceHelper(this);
        networkUtils = CategoryNetworkUtils.getInstance(this);
        showCurrentImage();
        createLoginDialog();
        createWaitingDialog();
        createCartDialog();
    }
    private void createCartDialog() {
        cartDialog = new Dialog(this, R.style.SheetDialog);
        cartDialog = new Dialog(this, R.style.SheetDialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_cart, null);
        Button cartButton = view.findViewById(R.id.dialog_cart_go);
        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ShowSelectedImageActivity.this, CartActivity.class));
                finishAffinity();
                cartDialog.dismiss();
            }
        });
        Button shoppingButton = view.findViewById(R.id.dialog_cart_shopping);
        shoppingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ShowSelectedImageActivity.this, MainActivity.class));
                finishAffinity();
                cartDialog.dismiss();
            }
        });
        cartDialog.setContentView(view);
        cartDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cartDialog.setCancelable(false);
    }
    private void createWaitingDialog() {
        waitingDialog = new Dialog(this, R.style.SheetDialog);
        View view= LayoutInflater.from(this).inflate(R.layout.dialog_waiting,null);
        TextView waitingMessage=view.findViewById(R.id.waiting_message);
        waitingMessage.setText(getResources().getString(R.string.adding_to_cart));
        waitingDialog.setContentView(view);
        waitingDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        waitingDialog.setCancelable(false);
    }

    private void createLoginDialog() {
        loginDialog = new Dialog(this, R.style.SheetDialog);
        loginDialog = new Dialog(this, R.style.SheetDialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_login, null);
        TextView waitingMessage = view.findViewById(R.id.dialog_login_text);
        waitingMessage.setText(getResources().getString(R.string.login_add_to_cart));
        Button login = view.findViewById(R.id.dialog_login_button);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ShowSelectedImageActivity.this, LoginActivity.class));
                loginDialog.dismiss();
            }
        });
        loginDialog.setContentView(view);
        loginDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }


    public void back(View view) {
        finish();
    }

    public void imageBack(View view) {
        if (currentImage > 0) {
            currentImage--;
            showCurrentImage();
        }
    }

    public void imageForward(View view) {
        if (currentImage < selectedImagesList.size() - 1) {
            currentImage++;
            showCurrentImage();
        }
    }

    public void addToCart(View view) {
        if (preferenceHelper.isAppLogin()) {
            storeToCart();
        } else {
            loginDialog.show();
        }
    }

    private void storeToCart() {
        List<String> socialImgUri = new ArrayList<>();
        List<String> galleryImgUri = new ArrayList<>();
        ProductData product = (ProductData) getIntent().getSerializableExtra("product");
        for (int i = 0; i < selectedImagesList.size(); i++) {
            if (selectedImagesList.get(i).startsWith("http")) {
                socialImgUri.add(selectedImagesList.get(i));
            } else {
                galleryImgUri.add(selectedImagesList.get(i));
            }
        }
        MultipartBody.Part[] galleryImagesParts = new MultipartBody.Part[galleryImgUri.size()];
        for (int i = 0; i < galleryImgUri.size(); i++) {
            //String imagePath=FileUtil.getPath(Uri.parse(galleryImgUri.get(i)), this);
            File file = new File(galleryImgUri.get(i));
            RequestBody galleryBody = RequestBody.create(file, MediaType.parse("image/*"));
            galleryImagesParts[i] = MultipartBody.Part.createFormData("upload_images[]", file.getName(), galleryBody);
        }

        InsertNewItemToCart(product, socialImgUri, galleryImagesParts);
    }

    private void InsertNewItemToCart(ProductData product, List<String> socialImgUri, MultipartBody.Part[] galleryImagesParts) {
        waitingDialog.show();
        Call<ResponseBody> call = networkUtils.getTownApiInterface().addItemToCart(product.getId(),1,socialImgUri,galleryImagesParts);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                waitingDialog.dismiss();
                if (response.isSuccessful()){
                    try {
                        String responseString=response.body().string();
                        JSONObject jsonObject = new JSONObject(responseString);
                        JSONArray items=jsonObject.getJSONObject("data").getJSONArray("items");
                        preferenceHelper.setCartCount(String.valueOf(items.length()));
                        cartDialog.show();
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(ShowSelectedImageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                waitingDialog.dismiss();
                Toast.makeText(ShowSelectedImageActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCurrentImage() {
        System.out.println(selectedImagesList.get(currentImage));
        Glide.with(this).load(selectedImagesList.get(currentImage)).centerCrop().into(selectedImageView);
        numberOfImagesTextView.setText((currentImage + 1) + "/" + selectedImagesList.size());
    }
}