package com.nns.graphictown.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.nns.graphictown.Helpers.Constants;
import com.nns.graphictown.Helpers.PreferenceHelper;
import com.nns.graphictown.Model.UnAuthNetworkUtils;
import com.nns.graphictown.Model.user.User;
import com.nns.graphictown.R;
import com.nns.graphictown.models.CartNetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChooseAccountTypeActivity extends AppCompatActivity {

    private String isSocialMediaLogin;
    private Dialog dialog;
    private String isCreateNewAccount;
    private PreferenceHelper preferenceHelper;
    private CartNetworkUtils networkUtils;
    private UnAuthNetworkUtils unAuthNetworkUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_account_type);
        isSocialMediaLogin = getIntent().getStringExtra("isSocialMediaLogin");
        isCreateNewAccount = getIntent().getStringExtra("CreateNewAccount");
        preferenceHelper = new PreferenceHelper(this);
        networkUtils = CartNetworkUtils.getInstance(this);
        unAuthNetworkUtils = UnAuthNetworkUtils.getInstance(this);
        createLoginDialog();
    }

    private void createLoginDialog() {
        dialog = new Dialog(this, R.style.SheetDialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_waiting, null);
        TextView waitingMessage = view.findViewById(R.id.waiting_message);
        waitingMessage.setText(getResources().getString(R.string.Log_in));
        dialog.setContentView(view);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
    }

    public void personalAccount(View view) {
        if (isSocialMediaLogin != null) {
            socialMediaLogin(Constants.PERSONAL_ACCOUNT);
        } else if (isCreateNewAccount != null) {
            Intent intent = new Intent(this, SignUpActivity.class);
            preferenceHelper.setUserAccountType(Constants.PERSONAL_ACCOUNT);
            intent.putExtra(Constants.ACCOUNT_TYPE, Constants.PERSONAL_ACCOUNT);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            preferenceHelper.setToken("79|KNUpSAlaxGTfyMUBhkaRfKXwHNRYgpyj6g573ePc");
            startActivity(intent);
            finish();
        }
    }

    public void companyAccount(View view) {
        if (isSocialMediaLogin != null) {
            socialMediaLogin(Constants.COMMERCIAL_ACCOUNT);
        } else if (isCreateNewAccount != null) {
            Intent intent = new Intent(this, SignUpActivity.class);
            preferenceHelper.setUserAccountType(Constants.COMMERCIAL_ACCOUNT);
            intent.putExtra(Constants.ACCOUNT_TYPE, Constants.COMMERCIAL_ACCOUNT);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            preferenceHelper.setToken("80|gETIscxrsTLsTiY4xgRY18yP2jc8RIntYTs7qGB5");
            startActivity(intent);
            finish();
        }
    }
    private void socialMediaLogin(final String accountType) {
        dialog.show();
        String name = getIntent().getStringExtra(Constants.USER_NAME);
        String email = getIntent().getStringExtra(Constants.USER_EMAIL);
        String imageUrl = getIntent().getStringExtra(Constants.IMAGE_URL);
        Call<User> responseBodyCall = unAuthNetworkUtils.getTownApiInterface().socialMediaLogin(name,accountType,email,imageUrl,"social","123456","123456");
        responseBodyCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    preferenceHelper.setUserInformation(response.body().getUser().getName(), response.body().getUser().getEmail(), response.body().getUser().getMobile(), response.body().getUser().getAccountType());
                    preferenceHelper.setUserImageUrl(response.body().getUser().getImage());
                    preferenceHelper.setLogin(true);
                    preferenceHelper.setToken(response.body().getToken());
                    preferenceHelper.setAddress(response.body().getUser().getAddress());
                    preferenceHelper.setCartCount(String.valueOf(response.body().getCartCount()));
                    dialog.dismiss();
                    startActivity(new Intent(ChooseAccountTypeActivity.this, MainActivity.class));
                    finishAffinity();
                } else {
                    dialog.dismiss();
                    Toast.makeText(ChooseAccountTypeActivity.this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(ChooseAccountTypeActivity.this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
            }
        });

    }
}