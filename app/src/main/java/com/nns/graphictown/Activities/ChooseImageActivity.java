package com.nns.graphictown.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.nns.graphictown.Adapters.ImageSelectedAdapter;
import com.nns.graphictown.Helpers.Constants;
import com.nns.graphictown.Helpers.PreferenceHelper;
import com.nns.graphictown.Model.Product.Product;
import com.nns.graphictown.Model.Product.ProductData;
import com.nns.graphictown.R;
import com.nns.graphictown.fragments.FacebookFragment;
import com.nns.graphictown.fragments.GalleryFragment;
import com.nns.graphictown.fragments.InstagramFragment;
import com.nns.graphictown.fragments.OnImageClickListener;

import java.util.ArrayList;
import java.util.Arrays;

public class ChooseImageActivity extends AppCompatActivity implements OnImageClickListener, FacebookFragment.FacebookActionsListener {
    private static final int MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE = 100;
    private View galleryIndicator,facebookIndicator,instagramIndicator;
    private GalleryFragment galleryFragment;
    private boolean isBottomSheetShown;
    private BottomSheetBehavior bottomSheetBehavior;
    private ArrayList<String> selectedImagesList;
    private RecyclerView selectedImageRecyclerView;
    ImageSelectedAdapter imageSelectedAdapter;
    private LinearLayout bottomSheetView;
    private FrameLayout galleryFrame,facebookFrame,instagramFrame;
    private TextView numberOfSelectedImage;
    private FacebookFragment facebookFragment;
    private CallbackManager facebookCallbackManager;
    private PreferenceHelper preferenceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_image);
        numberOfSelectedImage=findViewById(R.id.number_of_photos_text_view);
        galleryIndicator=findViewById(R.id.gallery_indicator);
        facebookIndicator=findViewById(R.id.facebook_indicator);
        instagramIndicator=findViewById(R.id.instagram_indicator);
        bottomSheetView=findViewById(R.id.images_bottom_sheet);
        galleryFrame=findViewById(R.id.gallery_container);
        facebookFrame=findViewById(R.id.Facebook_container);
        instagramFrame=findViewById(R.id.instagram_container);
        bottomSheetBehavior=BottomSheetBehavior.from(bottomSheetView);
        preferenceHelper=new PreferenceHelper(this);
        facebookFragment=new FacebookFragment();
        int heightInPixels= (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,90,getResources().getDisplayMetrics());
        bottomSheetBehavior.setPeekHeight(heightInPixels,true);
        addGalleryFragment();
        addImagesBottomSheet();
        getSupportFragmentManager().beginTransaction().replace(R.id.Facebook_container,facebookFragment).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.instagram_container,new InstagramFragment()).commit();
    }
    private void addImagesBottomSheet() {
        selectedImagesList=new ArrayList<>();
        imageSelectedAdapter=new ImageSelectedAdapter(selectedImagesList,this);
        selectedImageRecyclerView=findViewById(R.id.selected_image_recycler_view);
        selectedImageRecyclerView.setLayoutManager(new GridLayoutManager(this,3));
        selectedImageRecyclerView.setAdapter(imageSelectedAdapter);

    }
    private void addGalleryFragment() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new
                            String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE);
        } else {
           galleryFragment= new GalleryFragment(getAllShownImagesPath(this));
           getSupportFragmentManager().beginTransaction().replace(R.id.gallery_container,galleryFragment).commit();
        }
    }
    public void back(View view) {
        finish();
    }
    public void showGalleryImage(View view) {
        updateGalleryIndicator();
        if (galleryFragment!=null){
            getSupportFragmentManager().beginTransaction().replace(R.id.gallery_container,galleryFragment).commit();
        }
    }
    public void showFacebookImage(View view) {
        updateFacebookIndicator();

    }
    public void showInstagramImage(View view) {
        updateInstagramIndicator();

    }
    private void updateGalleryIndicator() {
        galleryIndicator.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        facebookIndicator.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        instagramIndicator.setBackgroundColor(getResources().getColor(android.R.color.transparent));
       galleryFrame.setVisibility(View.VISIBLE);
       facebookFrame.setVisibility(View.GONE);
       instagramFrame.setVisibility(View.GONE);
    }
    private void updateFacebookIndicator() {
        galleryIndicator.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        facebookIndicator.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        instagramIndicator.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        galleryFrame.setVisibility(View.GONE);
        facebookFrame.setVisibility(View.VISIBLE);
        instagramFrame.setVisibility(View.GONE);
    }
    private void updateInstagramIndicator() {
        galleryIndicator.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        facebookIndicator.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        instagramIndicator.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        galleryFrame.setVisibility(View.GONE);
        facebookFrame.setVisibility(View.GONE);
        instagramFrame.setVisibility(View.VISIBLE);
    }
    private ArrayList<String> getAllShownImagesPath(Activity activity) {
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        ArrayList<String> listOfAllImages = new ArrayList<>();
        String absolutePathOfImage = null;
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = { MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME };
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new
                            String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE);
        } else {
            cursor = activity.getContentResolver().query(uri, projection, null,
                    null, null);
            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            column_index_folder_name = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            while (cursor.moveToNext()) {
                absolutePathOfImage = cursor.getString(column_index_data);

                listOfAllImages.add(absolutePathOfImage);
            }
        }
        return listOfAllImages;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if(requestCode == MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                galleryFragment= new GalleryFragment(getAllShownImagesPath(this));
                getSupportFragmentManager().beginTransaction().replace(R.id.gallery_container,galleryFragment).commit();
            }
        }
    }
    @Override
    public void onInsertNewImageListener(String imageUri) {
        if (!isBottomSheetShown){
            bottomSheetView.setVisibility(View.VISIBLE);
            isBottomSheetShown=true;
        }
        selectedImagesList.add(imageUri);
        imageSelectedAdapter.notifyItemInserted(selectedImagesList.size()-1);
        imageSelectedAdapter.notifyItemRangeInserted(selectedImagesList.size()-1, 1);
        updateNumberOfSelectedImages();
    }
    @Override
    public void onDeleteImageListener(String imageUri) {
        selectedImagesList.remove(imageUri);
        imageSelectedAdapter.notifyDataSetChanged();
        if (isBottomSheetShown&&selectedImagesList.isEmpty()){
            bottomSheetView.setVisibility(View.GONE);
            isBottomSheetShown=false;
        }
        updateNumberOfSelectedImages();
    }
    private void updateNumberOfSelectedImages() {
        if (!selectedImagesList.isEmpty()){
            switch (selectedImagesList.size()){
                case 1: numberOfSelectedImage.setText(getResources().getString(R.string.one_image)); break;
                case 2:  numberOfSelectedImage.setText(getResources().getString(R.string.two_image));break;
                default: numberOfSelectedImage.setText(String.format("%d ",selectedImagesList.size())+getResources().getString(R.string.images));break;
            }
        }
    }
    public void showSelectedImage(View view) {
        Intent intent=new Intent(this,ShowSelectedImageActivity.class);
        intent.putStringArrayListExtra(Constants.SELECTED_IMAGE_LIST,selectedImagesList);
       ProductData product= (ProductData) getIntent().getSerializableExtra("product");
        intent.putExtra("product",product);
        startActivity(intent);
    }
    @Override
    public void facebookLogin(LoginButton loginButton) {
        facebookCallbackManager = CallbackManager.Factory.create();
        loginButton.setReadPermissions("user_photos");
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("user_photos"));
        // If using in a fragment
        loginButton.setFragment(facebookFragment);
        // Callback registration
        loginButton.registerCallback(facebookCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
               preferenceHelper.facebookLogin(loginResult.getAccessToken().getUserId(),loginResult.getAccessToken().getToken());
               facebookFragment.loadLoginData();
            }
            @Override
            public void onCancel() {
                // App code
                Toast.makeText(ChooseImageActivity.this, "cancel", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onError(FacebookException exception) {
                // App code
                Toast.makeText(ChooseImageActivity.this, exception.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}