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

import com.google.android.material.textfield.TextInputEditText;
import com.hbb20.CountryCodePicker;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.nns.graphictown.Helpers.Constants;
import com.nns.graphictown.Helpers.FileUtil;
import com.nns.graphictown.Helpers.PreferenceHelper;
import com.nns.graphictown.Model.user.User;
import com.nns.graphictown.R;
import com.nns.graphictown.Model.UnAuthNetworkUtils;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK_PHOTO = 100;
    private static final int MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE = 120;
    private TextInputEditText emailEditText, nameEditText, phoneNumberEditText, passwordEditText, confirmPasswordEditText;
    private UnAuthNetworkUtils networkUtils;
    private Dialog dialog;
    private String accountType;
    private CountryCodePicker countryCodePicker;
    private Uri imageUri;
    private CircularImageView userImageView;
    private PreferenceHelper preferenceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        emailEditText = findViewById(R.id.email_sign_up);
        nameEditText = findViewById(R.id.user_name_sign_up);
        phoneNumberEditText = findViewById(R.id.phone_number_sign_up);
        passwordEditText = findViewById(R.id.password_sign_up);
        confirmPasswordEditText = findViewById(R.id.confirm_password_sign_up);
        userImageView = findViewById(R.id.sign_up_image);
        accountType = getIntent().getStringExtra(Constants.ACCOUNT_TYPE);
        countryCodePicker = findViewById(R.id.country_code_picker);
        networkUtils = UnAuthNetworkUtils.getInstance(this);
        preferenceHelper=new PreferenceHelper(this);
        createSignUpDialog();
    }

    private void createSignUpDialog() {
        dialog = new Dialog(this, R.style.SheetDialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_waiting, null);
        TextView waitingMessage = view.findViewById(R.id.waiting_message);
        waitingMessage.setText(getResources().getString(R.string.creating_new_account));
        dialog.setContentView(view);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
    }

    public void back(View view) {
        finish();
    }

    public void chooseAccountImage(View view) {
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

    public void createNewAccount(View view) {
        if (isValid()) {
            dialog.show();
            String phone = "+" + countryCodePicker.getFullNumber() + phoneNumberEditText.getText().toString();
            File file = new File(FileUtil.getPath(imageUri, this));
            RequestBody requestFile = RequestBody.create(file, MediaType.parse("image/*"));
            MultipartBody.Part imageFile = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
            Call<User> responseBodyCall = networkUtils.getTownApiInterface().signUp(imageFile, nameEditText.getText().toString(), accountType, phone, emailEditText.getText().toString(), passwordEditText.getText().toString(), passwordEditText.getText().toString(), "normal");
            responseBodyCall.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    dialog.dismiss();
                    if (response.isSuccessful()) {
                        preferenceHelper.setUserInformation(response.body().getUser().getName(),response.body().getUser().getEmail(),response.body().getUser().getMobile(),response.body().getUser().getAccountType());
                        preferenceHelper.setUserImageUrl(response.body().getUser().getImage());
                        preferenceHelper.setLogin(true);
                        preferenceHelper.setToken(response.body().getToken());
                        startActivity(new Intent(SignUpActivity.this,MainActivity.class));
                        finishAffinity();
                    } else {
                        Toast.makeText(SignUpActivity.this, "Email or phone number is already taken", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    dialog.dismiss();
                    Toast.makeText(SignUpActivity.this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }


    private boolean isValid() {
        if (imageUri == null) {
            Toast.makeText(this, getResources().getString(R.string.choose_profile_image), Toast.LENGTH_SHORT).show();
            return false;
        }
        String regex = "^(.+)@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(emailEditText.getText().toString());
        if (emailEditText.getText().toString().isEmpty()) {
            emailEditText.setError(getResources().getString(R.string.this_field_is_required));
            return false;
        }
        if (!matcher.matches()) {
            emailEditText.setText("");
            emailEditText.setError(getResources().getString(R.string.Enter_valid_email));
            return false;
        }
        if (nameEditText.getText().toString().isEmpty()) {
            nameEditText.setError(getResources().getString(R.string.this_field_is_required));
            return false;
        }
        if (phoneNumberEditText.getText().toString().isEmpty()) {
            phoneNumberEditText.setError(getResources().getString(R.string.this_field_is_required));
            return false;
        }
        if (passwordEditText.getText().toString().isEmpty()) {
            passwordEditText.setError(getResources().getString(R.string.this_field_is_required));
            return false;
        }
        if (confirmPasswordEditText.getText().toString().isEmpty()) {
            confirmPasswordEditText.setError(getResources().getString(R.string.this_field_is_required));
            return false;
        }
        if (!confirmPasswordEditText.getText().toString().equals(passwordEditText.getText().toString())) {
            confirmPasswordEditText.setText("");
            confirmPasswordEditText.setError(getResources().getString(R.string.doesnt_match));
            return false;
        }
        if (passwordEditText.getText().toString().length() < 6) {
            passwordEditText.setText("");
            passwordEditText.setError(getResources().getString(R.string.password_must_be_at_least_6_character));
            confirmPasswordEditText.setText("");
        }
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_PHOTO && resultCode == RESULT_OK) {
            imageUri = data.getData();
            System.out.println(imageUri.getPath());
            userImageView.setImageURI(imageUri);
        }
    }
}
