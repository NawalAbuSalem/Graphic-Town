package com.nns.graphictown.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.nns.graphictown.Helpers.PreferenceHelper;
import com.nns.graphictown.R;

public class ProfileActivity extends AppCompatActivity {
    private static final int UPDATE_PROFILE_REQUEST_CODE = 120;
    private CircularImageView image;
    private TextView name, email, phoneNumber, address;
    private PreferenceHelper preferenceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        image = findViewById(R.id.profile_image);
        name = findViewById(R.id.profile_name);
        email = findViewById(R.id.profile_email_address);
        phoneNumber = findViewById(R.id.profile_phone_number);
        address = findViewById(R.id.profile_address);
        preferenceHelper = new PreferenceHelper(this);
        loadProfileData();
    }
    private void loadProfileData() {
        name.setText(preferenceHelper.getUserName());
        if (!preferenceHelper.getUserPhoneNumber().equals("null")) {
            phoneNumber.setText(preferenceHelper.getUserPhoneNumber());
        }
        if (preferenceHelper.getUserAddress() != null && !preferenceHelper.getUserAddress().equals("null")) {
            address.setText(preferenceHelper.getUserAddress());
        }
        email.setText(preferenceHelper.getUserEmail());
        String imageUri = preferenceHelper.getUserImageUrl();
        if (imageUri != null && !imageUri.equals("null")) {
            Glide.with(this).load(imageUri).into(image);
        }
    }
    public void back(View view) {
        setResult(RESULT_OK);
        finish();
    }
    public void updateProfile(View view) {
        startActivityForResult(new Intent(this, UpdateProfileActivity.class), UPDATE_PROFILE_REQUEST_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPDATE_PROFILE_REQUEST_CODE && resultCode == RESULT_OK) {
            loadProfileData();
        }
    }
    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }
}