package com.nns.graphictown.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.nns.graphictown.Adapters.CategoryRecyclerAdapter;
import com.nns.graphictown.Adapters.HomePagerAdapter;
import com.nns.graphictown.Helpers.Constants;
import com.nns.graphictown.Helpers.PreferenceHelper;
import com.nns.graphictown.Model.UnAuthNetworkUtils;
import com.nns.graphictown.Model.advertisement.Advertisement;
import com.nns.graphictown.Model.category.Category;
import com.nns.graphictown.Model.category.CategoryData;
import com.nns.graphictown.R;
import com.nns.graphictown.models.CategoryNetworkUtils;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import me.relex.circleindicator.CircleIndicator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final int CART_REQUEST_CODE = 200;
    private static final int PROFILE_REQUEST_CODE = 201;
    private DrawerLayout drawer;
    private ViewPager homePager;
    private CircleIndicator pagerIndicator;
    private HomePagerAdapter pagerAdapter;
    private NavigationView navigationView;
    private RecyclerView categoriesRecyclerView;
    private Dialog dialog,loginDialog;
    private PreferenceHelper preferenceHelper;
    private ProgressBar categoryProgressBar;
    private CategoryNetworkUtils networkUtils;
    private LinearLayout errorLayout,mainContentLayout;
    private UnAuthNetworkUtils unAuthNetworkUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawer = findViewById(R.id.drawer_layout);
        homePager = findViewById(R.id.home_viewpager);
        navigationView = findViewById(R.id.nav_view);
        pagerIndicator = findViewById(R.id.home_pager_indicator);
        categoryProgressBar=findViewById(R.id.progress_bar_waiting);
        categoriesRecyclerView = findViewById(R.id.home_category_recycler_view);
        mainContentLayout=findViewById(R.id.main_content_layout);
        errorLayout=findViewById(R.id.no_internet_layout);
        preferenceHelper = new PreferenceHelper(this);
        networkUtils=CategoryNetworkUtils.getInstance(this);
        unAuthNetworkUtils=UnAuthNetworkUtils.getInstance(this);
        createDialog();
        addHomePager();
        loadCategoryItems();
        updateHeaderInformation();
        addCartCount();
        System.out.println(preferenceHelper.getToken());


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

    private void updateHeaderInformation() {
        if (preferenceHelper.isAppLogin()){
            CircularImageView userImage=findViewById(R.id.home_image);
            TextView userName=findViewById(R.id.home_user_name);
            userName.setText(preferenceHelper.getUserName());
            String imageUri=preferenceHelper.getUserImageUrl();
            if (imageUri!=null&&!imageUri.equals("null")){
                Glide.with(this).load(imageUri).into(userImage);
            }
        }
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
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
                loginDialog.dismiss();
            }
        });
        loginDialog.setContentView(view);
        loginDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void loadCategoryItems() {
        Call<Category> listCall=networkUtils.getTownApiInterface().getCategories();
        listCall.enqueue(new Callback<Category>() {
            @Override
            public void onResponse(Call<Category> call, Response<Category> response) {
               categoryProgressBar.setVisibility(View.GONE);
               if (response.isSuccessful()){
                   errorLayout.setVisibility(View.GONE);
                   mainContentLayout.setVisibility(View.VISIBLE);
                   addCategoriesRecyclerItems(response.body().getData());
               }
            }
            @Override
            public void onFailure(Call<Category> call, Throwable t) {
                categoryProgressBar.setVisibility(View.GONE);
                showErrorMessage();
            }
        });
    }

    private void showErrorMessage() {
        errorLayout.setVisibility(View.VISIBLE);
        mainContentLayout.setVisibility(View.GONE);
        Button refreshButton=findViewById(R.id.refresh_button);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryProgressBar.setVisibility(View.VISIBLE);
                errorLayout.setVisibility(View.GONE);
                addHomePager();
                loadCategoryItems();
            }
        });
    }
    private void addCategoriesRecyclerItems(List<CategoryData> categoryList) {
        CategoryRecyclerAdapter categoryRecyclerAdapter = new CategoryRecyclerAdapter(categoryList,this);
        categoryRecyclerAdapter.setOnCategoryClickListener(new CategoryRecyclerAdapter.OnCategoryClickListener() {
            @Override
            public void onClick(CategoryData category) {
                Intent intent=new Intent(MainActivity.this,SubCategoryActivity.class);
                intent.putExtra("Category",category);
                startActivity(intent);
            }
        });
        categoriesRecyclerView.setAdapter(categoryRecyclerAdapter);
        categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void addHomePager() {
        Call<Advertisement>call=unAuthNetworkUtils.getTownApiInterface().getAppAds();
        call.enqueue(new Callback<Advertisement>() {
            @Override
            public void onResponse(Call<Advertisement> call, Response<Advertisement> response) {
                if (response.isSuccessful()){
                    pagerAdapter = new HomePagerAdapter(response.body().getData(),MainActivity.this);
                    homePager.setAdapter(pagerAdapter);
                    pagerIndicator.setViewPager(homePager);
                    automateViewPagerSwiping();
                }
            }
            @Override
            public void onFailure(Call<Advertisement> call, Throwable t) {
                showErrorMessage();
            }
        });

    }

    public void menuItemClickListener(View view) {
        switch (view.getId()) {
            case R.id.menu_account_item:
                if (preferenceHelper.isAppLogin()){
                    startActivityForResult(new Intent(this, ProfileActivity.class),PROFILE_REQUEST_CODE);
                }else {
                    createLoginDialog(getResources().getString(R.string.login_show_profile));
                    loginDialog.show();
                }

                break;
            case R.id.menu_orders_item:
                if (preferenceHelper.isAppLogin()){
                    startActivity(new Intent(this, OrdersActivity.class));
                }else {
                    createLoginDialog(getResources().getString(R.string.login_show_orders));
                    loginDialog.show();
                }
                break;
            case R.id.menu_login_item:
                if (preferenceHelper.isAppLogin()){
                    Toast.makeText(this, getResources().getString(R.string.already_login), Toast.LENGTH_LONG).show();
                }else {
                    startActivity(new Intent(this, LoginActivity.class));
                }
                break;
            case R.id.menu_about_us_item:
                startActivity(new Intent(this, AboutUsActivity.class));
                break;
            case R.id.menu_contact_us_item:
                startActivity(new Intent(this, ContactUsActivity.class));
                break;
            case R.id.menu_settings_item:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            default:
                if (preferenceHelper.isAppLogin()){
                    logout();
                }else {
                    createLoginDialog(getResources().getString(R.string.login_out));
                    loginDialog.show();
                }

        }
        drawer.closeDrawer(navigationView);
    }

    private void logout() {
        preferenceHelper.clear();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finishAffinity();
    }

    private void automateViewPagerSwiping() {
        final long DELAY_MS = 500;//delay in milliseconds before task is to be executed
        final long PERIOD_MS = 2000; // time in milliseconds between successive task executions.
        final Handler handler = new Handler();
        final Runnable update = new Runnable() {
            public void run() {
                if (homePager.getCurrentItem() == pagerAdapter.getCount() - 1) { //adapter is your custom ViewPager's adapter
                    homePager.setCurrentItem(0);
                } else {
                    homePager.setCurrentItem(homePager.getCurrentItem() + 1, true);
                }
            }
        };

        Timer timer = new Timer(); // This will create a new Thread
        timer.schedule(new TimerTask() { // task to be scheduled
            @Override
            public void run() {
                handler.post(update);
            }
        }, DELAY_MS, PERIOD_MS);
    }

    public void openDrawer(View view) {
        if (!drawer.isDrawerOpen(navigationView)) {
            drawer.openDrawer(navigationView);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(navigationView)) {
            drawer.closeDrawer(navigationView);
        } else {
            super.onBackPressed();
        }

    }

    public void viewCart(View view) {
        if (preferenceHelper.isAppLogin()){
            startActivityForResult(new Intent(this, CartActivity.class),CART_REQUEST_CODE);
        }else {
            createLoginDialog(getResources().getString(R.string.login_show_cart_content));
            loginDialog.show();
        }
    }

    private void createDialog() {
        dialog = new Dialog(this, R.style.SheetDialog);
        dialog = new Dialog(this, R.style.SheetDialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_waiting, null);
        TextView waitingMessage = view.findViewById(R.id.waiting_message);
        waitingMessage.setText(getResources().getString(R.string.Logouting));
        dialog.setContentView(view);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==CART_REQUEST_CODE&&resultCode==RESULT_OK){
            addCartCount();
        }
        if (requestCode==PROFILE_REQUEST_CODE&&resultCode==RESULT_OK){
            updateHeaderInformation();
        }
    }
}