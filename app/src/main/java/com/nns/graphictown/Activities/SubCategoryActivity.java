package com.nns.graphictown.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nns.graphictown.Adapters.SubCategoryDesignAdapter;
import com.nns.graphictown.Adapters.SubCategoryFilterAdapter;
import com.nns.graphictown.Helpers.PreferenceHelper;
import com.nns.graphictown.Model.Product.Product;
import com.nns.graphictown.Model.Product.ProductData;
import com.nns.graphictown.Model.category.CategoryData;
import com.nns.graphictown.Model.subscategory.SubCategory;
import com.nns.graphictown.Model.subscategory.SubCategoryData;
import com.nns.graphictown.R;
import com.nns.graphictown.models.CategoryNetworkUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubCategoryActivity extends AppCompatActivity {
    private static final int CART_REQUEST_CODE = 200;
    private RecyclerView filterRecyclerView, designRecyclerView;
    private CategoryData category;
    private PreferenceHelper preferenceHelper;
    private CategoryNetworkUtils networkUtils;
    private ProgressBar progressBar;
    private LinearLayout errorLayout;
    private Dialog loginDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category);
        filterRecyclerView = findViewById(R.id.subcategory_filter_recyclerview);
        designRecyclerView = findViewById(R.id.subcategory_designs_recyclerview);
        errorLayout = findViewById(R.id.no_internet_layout);
        progressBar = findViewById(R.id.progress_bar_waiting);
        category = (CategoryData) getIntent().getSerializableExtra("Category");
        TextView categoryNameTextView = findViewById(R.id.category_name);
        categoryNameTextView.setText(category.getTitle());
        preferenceHelper = new PreferenceHelper(this);
        networkUtils = CategoryNetworkUtils.getInstance(this);
        addFilterAdapter();
        getAllCategoryProduct();
        addCartCount();
    }
    private void addCartCount() {
        TextView countTextView=findViewById(R.id.cart_item_count);
        String s=preferenceHelper.getCartCount();
        if (s!=null){
            int count =Integer.parseInt(s);
            if (count>99){
                countTextView.setText("+" + s);
            } else {
                countTextView.setText(s);
            }
        }
    }
    private void getAllCategoryProduct() {
        progressBar.setVisibility(View.VISIBLE);
        designRecyclerView.setVisibility(View.VISIBLE);
        Call<Product> call = networkUtils.getTownApiInterface().getAllCategoryProduct(category.getId());
        call.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.GONE);
                   addProductAdapter(response.body().getData());
                }
            }
            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                showCategoryProductsErrorMessage();
            }
        });
    }

    private void addProductAdapter(List<ProductData> products) {
        SubCategoryDesignAdapter designAdapter = new SubCategoryDesignAdapter(products, this);
        designRecyclerView.setAdapter(designAdapter);
        designRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        designAdapter.setOnSubCategoryDesignClickListener(new SubCategoryDesignAdapter.OnSubCategoryDesignClickListener() {
            @Override
            public void onClick(ProductData product) {
                Intent intent=new Intent(SubCategoryActivity.this,DesignDetailsActivity.class);
                                 intent.putExtra("product",product);
                                 startActivity(intent);


            }
        });
    }

    private void addFilterAdapter() {
        Call<SubCategory> listCall = networkUtils.getTownApiInterface().getSubCategoryFilter(category.getId());
        listCall.enqueue(new Callback<SubCategory>() {
            @Override
            public void onResponse(Call<SubCategory> call, Response<SubCategory> response) {
                if (response.isSuccessful() && response.body() != null) {
                    errorLayout.setVisibility(View.GONE);
                    List<SubCategoryData> filters = new ArrayList<>();
                    filters.add(new SubCategoryData(-100,-100, getResources().getString(R.string.show_all)));
                    filters.addAll(response.body().getData());
                    SubCategoryFilterAdapter filterAdapter = new SubCategoryFilterAdapter(filters, SubCategoryActivity.this);
                    filterRecyclerView.setLayoutManager(new LinearLayoutManager(SubCategoryActivity.this, RecyclerView.HORIZONTAL, false));
                    filterRecyclerView.setAdapter(filterAdapter);
                    filterAdapter.setOnSubCategoryFilterClickListener(new SubCategoryFilterAdapter.OnSubCategoryFilterClickListener() {
                        @Override
                        public void onClick(int subCategoryId) {
                            addProductAdapter(new ArrayList<ProductData>());
                            if (subCategoryId == -100) {
                                getAllCategoryProduct();
                            } else {
                               getSubCategoryProducts(subCategoryId);
                            }
                        }
                    });

                }
            }
            @Override
            public void onFailure(Call<SubCategory> call, Throwable t) {
                showFilterErrorMessage();
            }
        });


    }

    private void getSubCategoryProducts(final int subCategoryId) {
        progressBar.setVisibility(View.VISIBLE);
        designRecyclerView.setVisibility(View.VISIBLE);
        Call<Product> call = networkUtils.getTownApiInterface().getSubCategoryProducts(subCategoryId);
        call.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.GONE);
                    addProductAdapter(response.body().getData());
                }
            }
            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                showSubCategoryProductsErrorMessage(subCategoryId);
            }
        });
    }

    public void back(View view) {
        finish();
    }

    public void viewCart(View view) {
        if (preferenceHelper.isAppLogin()){
            startActivityForResult(new Intent(this, CartActivity.class),CART_REQUEST_CODE);
        }else {
            createLoginDialog(getResources().getString(R.string.login_show_cart_content));
            loginDialog.show();
        }

    }

    private void showCategoryProductsErrorMessage() {
        errorLayout.setVisibility(View.VISIBLE);
        Button refreshButton = findViewById(R.id.refresh_button);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorLayout.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                getAllCategoryProduct();

            }
        });
    }

    private void showFilterErrorMessage() {
        errorLayout.setVisibility(View.VISIBLE);
        Button refreshButton = findViewById(R.id.refresh_button);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorLayout.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                addFilterAdapter();

            }
        });
    }
    private void showSubCategoryProductsErrorMessage(final int subCategoryId) {
        errorLayout.setVisibility(View.VISIBLE);
        Button refreshButton = findViewById(R.id.refresh_button);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorLayout.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                getSubCategoryProducts(subCategoryId);

            }
        });
    }

    private void createLoginDialog(String message) {
        loginDialog = new Dialog(this, R.style.SheetDialog);
        loginDialog = new Dialog(this, R.style.SheetDialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_login, null);
        TextView waitingMessage = view.findViewById(R.id.dialog_login_text);
        waitingMessage.setText(message);
        Button login=view.findViewById(R.id.dialog_login_button);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SubCategoryActivity.this,LoginActivity.class));
                loginDialog.dismiss();
            }
        });
        loginDialog.setContentView(view);
        loginDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==CART_REQUEST_CODE&&resultCode==RESULT_OK){
            addCartCount();
        }
    }
}