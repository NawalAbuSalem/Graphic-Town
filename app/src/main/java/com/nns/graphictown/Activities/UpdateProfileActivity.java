package com.nns.graphictown.Activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.nns.graphictown.Helpers.FileUtil;
import com.nns.graphictown.Helpers.PreferenceHelper;
import com.nns.graphictown.Model.AuthNetworkUtils;
import com.nns.graphictown.Model.user.User;
import com.nns.graphictown.R;

import java.io.File;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateProfileActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE = 100;
    private static final int REQUEST_CODE_PICK_PHOTO = 120;
    private CircularImageView image;
    private TextInputEditText name, email, phoneNumber, address;
    private PreferenceHelper preferenceHelper;
    private Uri imageUri;
    private Dialog dialog;
    private AuthNetworkUtils networkUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        image = findViewById(R.id.eprofile_image);
        name = findViewById(R.id.eprofile_name);
        email = findViewById(R.id.eprofile_email);
        phoneNumber = findViewById(R.id.eprofile_phone_number);
        address = findViewById(R.id.eprofile_address);
        preferenceHelper = new PreferenceHelper(this);
        networkUtils = AuthNetworkUtils.getInstance(this);
        loadProfileData();
        createDialog();
    }

    public void back(View view) {
        finish();
    }

    public void changeAccountImage(View view) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new
                            String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE);
        } else {
            pickImage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImage();
            }
        }
    }

    private void pickImage() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_PICK_PHOTO);
    }

    public void saveUpdates(View view) {
        if (isValid()) {
            if (imageUri != null) {
                updateProfileWithImage(name.getText().toString(), email.getText().toString(), phoneNumber.getText().toString(), address.getText().toString());
            } else {
                updateProfile(name.getText().toString(), email.getText().toString(), phoneNumber.getText().toString(), address.getText().toString());
            }
        }
    }

    private void updateProfile(final String name, final String email, final String phoneNumber, final String address) {
        dialog.show();
        Call<User> responseBodyCall = networkUtils.getTownApiInterface().updateProfile(name, email, phoneNumber, address);
        responseBodyCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                dialog.dismiss();
                if (response.isSuccessful()) {
                    preferenceHelper.updateUserInformation(name, email, phoneNumber, address);
                    Toast.makeText(UpdateProfileActivity.this, getResources().getString(R.string.profile_updated_successfully), Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK, new Intent());
                    finish();
                } else {
                    Toast.makeText(UpdateProfileActivity.this, "The mobile must be between 7 and 10 digits.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(UpdateProfileActivity.this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();

            }
        });

    }
    private void updateProfileWithImage(final String name, final String email, final String phoneNumber, final String address) {
        dialog.show();
        final MutableLiveData<String>liveData=new MutableLiveData<>();
        File file = new File(FileUtil.getPath(imageUri, this));
        RequestBody requestFile = RequestBody.create(file, MediaType.parse("image/*"));
        MultipartBody.Part image = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("name",name);
        hashMap.put("email",email);
        hashMap.put("mobile",phoneNumber);
        hashMap.put("address",address);
        Call<User>call=networkUtils.getTownApiInterface().updateProfileWithImage(hashMap,image);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                dialog.dismiss();
                if (response.isSuccessful()) {
                    preferenceHelper.updateUserInformation(name, email, phoneNumber, address);
                    preferenceHelper.setUserImageUrl(response.body().getUser().getImage());
                    Toast.makeText(UpdateProfileActivity.this, getResources().getString(R.string.profile_updated_successfully), Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK, new Intent());
                    finish();
                } else {
                    Toast.makeText(UpdateProfileActivity.this, "The mobile must be between 7 and 10 digits.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(UpdateProfileActivity.this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();

            }
        });
    }
    private void loadProfileData() {
        name.setText(preferenceHelper.getUserName());
        email.setText(preferenceHelper.getUserEmail());
        String imageUri = preferenceHelper.getUserImageUrl();
        if (!preferenceHelper.getUserAddress().equals("null")) {
            address.setText(preferenceHelper.getUserAddress());
        }
        if (!preferenceHelper.getUserPhoneNumber().equals("null")) {
            phoneNumber.setText(preferenceHelper.getUserPhoneNumber());
        }
        if (imageUri != null && !imageUri.equals("null")) {
            Glide.with(this).load(imageUri).into(image);
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_PHOTO && resultCode == RESULT_OK) {
            imageUri = data.getData();
            image.setImageURI(imageUri);
            System.out.println(imageUri.getPath());
        }
    }

    private boolean isValid() {
        String regex = "^(.+)@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email.getText().toString());
        if (name.getText().toString().isEmpty()) {
            name.setError(getResources().getString(R.string.this_field_is_required));
            return false;
        }
        if (email.getText().toString().isEmpty()) {
            email.setError(getResources().getString(R.string.this_field_is_required));
            return false;
        }
        if (!matcher.matches()) {
            email.setText("");
            email.setError(getResources().getString(R.string.Enter_valid_email));
            return false;
        }

        if (phoneNumber.getText().toString().isEmpty()) {
            phoneNumber.setError(getResources().getString(R.string.this_field_is_required));
            return false;
        }
        if (address.getText().toString().isEmpty()) {
            address.setError(getResources().getString(R.string.this_field_is_required));
            return false;
        }
        return true;
    }

    private void createDialog() {
        dialog = new Dialog(this, R.style.SheetDialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_waiting, null);
        TextView waitingMessage = view.findViewById(R.id.waiting_message);
        waitingMessage.setText(getResources().getString(R.string.updating_profile));
        dialog.setContentView(view);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
    }
}