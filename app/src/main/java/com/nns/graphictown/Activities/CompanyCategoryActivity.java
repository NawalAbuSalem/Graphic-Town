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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nns.graphictown.Adapters.CompanyCategoryRecyclerAdapter;
import com.nns.graphictown.Helpers.Constants;
import com.nns.graphictown.Helpers.PreferenceHelper;
import com.nns.graphictown.Model.category.Category;
import com.nns.graphictown.Model.category.CategoryData;
import com.nns.graphictown.R;
import com.nns.graphictown.models.CategoryNetworkUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompanyCategoryActivity extends AppCompatActivity {
    private static final int CART_REQUEST_CODE = 200;
    private TextView companyNameTextView;
    private RecyclerView categoriesRecyclerView;
    private ProgressBar progressBar;
    private int companyId;
    private PreferenceHelper preferenceHelper;
    private CategoryNetworkUtils networkUtils;
    private LinearLayout errorLayout;
    private Dialog loginDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_category);
        categoriesRecyclerView = findViewById(R.id.company_category_recycler_view);
        companyNameTextView = findViewById(R.id.company_name);
        progressBar = findViewById(R.id.progress_bar);
        companyId = getIntent().getIntExtra("CompanyID",0);
        preferenceHelper = new PreferenceHelper(this);
        networkUtils = CategoryNetworkUtils.getInstance(this);
        errorLayout=findViewById(R.id.no_internet_layout);
        companyNameTextView.setText(getIntent().getStringExtra("CompanyName"));
        getCompanyCategories();
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
    private void showErrorMessage() {
        errorLayout.setVisibility(View.VISIBLE);
        Button refreshButton=findViewById(R.id.refresh_button);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorLayout.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                getCompanyCategories();
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
                startActivity(new Intent(CompanyCategoryActivity.this,LoginActivity.class));
                loginDialog.dismiss();
            }
        });
        loginDialog.setContentView(view);
        loginDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
    private void getCompanyCategories() {
        progressBar.setVisibility(View.VISIBLE);
        Call<Category> call = networkUtils.getTownApiInterface().getCompanyCategories(companyId);
        call.enqueue(new Callback<Category>() {
            @Override
            public void onResponse(Call<Category> call, Response<Category> response) {
                if (response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.GONE);
                    addCategoriesRecyclerItems(response.body().getData());
                }
            }

            @Override
            public void onFailure(Call<Category> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                showErrorMessage();
            }
        });
    }

    private void addCategoriesRecyclerItems(List<CategoryData> categoryList) {
        CompanyCategoryRecyclerAdapter categoryRecyclerAdapter = new CompanyCategoryRecyclerAdapter(categoryList, this);
        categoryRecyclerAdapter.setOnCompanyCategoryClickListener(new CompanyCategoryRecyclerAdapter.OnCompanyCategoryClickListener() {
            @Override
            public void onClick(CategoryData category) {
                Intent intent=new Intent(CompanyCategoryActivity.this,SubCategoryActivity.class);
                intent.putExtra("Category",category);
                intent.putExtra(Constants.ACCOUNT_TYPE,getIntent().getStringExtra(Constants.ACCOUNT_TYPE));
                startActivity(intent);
            }
        });
        categoriesRecyclerView.setAdapter(categoryRecyclerAdapter);
        categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==CART_REQUEST_CODE&&resultCode==RESULT_OK){
            addCartCount();
        }
    }
}