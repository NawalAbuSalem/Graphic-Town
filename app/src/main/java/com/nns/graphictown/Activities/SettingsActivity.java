package com.nns.graphictown.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.nns.graphictown.Helpers.PreferenceHelper;
import com.nns.graphictown.Model.AuthNetworkUtils;
import com.nns.graphictown.R;
import com.nns.graphictown.models.NetworkUtils;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.io.IOException;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends AppCompatActivity {
    private ExpandableLayout passwordExpandableLayout, languageExpandableLayout;
    private ImageView passwordImage, languageImage;
    private TextView englishLanguage, arabicLanguage;
    private PreferenceHelper preferenceHelper;
    private String currentLanguage;
    private Dialog waitingDialog, loginDialog;
    private TextInputEditText currentPassword, newPassword, confirmPassword;
    private AuthNetworkUtils authNetworkUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        passwordExpandableLayout = findViewById(R.id.password_expandable_layout);
        passwordImage = findViewById(R.id.password_image);
        languageExpandableLayout = findViewById(R.id.language_expandable_layout);
        arabicLanguage = findViewById(R.id.arabic_textView);
        englishLanguage = findViewById(R.id.english_textView);
        languageImage = findViewById(R.id.language_image);
        currentPassword = findViewById(R.id.current_password);
        newPassword = findViewById(R.id.new_password);
        confirmPassword = findViewById(R.id.confirm_password);
        preferenceHelper = new PreferenceHelper(this);
        authNetworkUtils = AuthNetworkUtils.getInstance(this);
        arabicLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentLanguage = "ar";
                arabicLanguage.setTextColor(getResources().getColor(R.color.colorAccent));
                englishLanguage.setTextColor(Color.BLACK);
            }
        });
        englishLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentLanguage = "en";
                englishLanguage.setTextColor(getResources().getColor(R.color.colorAccent));
                arabicLanguage.setTextColor(Color.BLACK);
            }
        });
        setLanguage();
    }

    private void setLanguage() {
        currentLanguage = preferenceHelper.getLanguage();
        if (preferenceHelper.getLanguage().equals("ar")) {
            arabicLanguage.setTextColor(getResources().getColor(R.color.colorAccent));
        } else {
            englishLanguage.setTextColor(getResources().getColor(R.color.colorAccent));
        }
    }


    public void back(View view) {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    public void changePassword(View view) {
        if (!preferenceHelper.isAppLogin()) {
            createLoginDialog();
            loginDialog.show();
        } else {
            if (isValid()) {
                changeCurrentPassword();
            }
        }

    }

    private void changeCurrentPassword() {
        createWaitingDialog(getResources().getString(R.string.changing_password));
        waitingDialog.show();
        Call<ResponseBody> call = authNetworkUtils.getTownApiInterface().updatePassword(currentPassword.getText().toString(), newPassword.getText().toString(), newPassword.getText().toString());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                waitingDialog.dismiss();
                if (response.isSuccessful()){
                   Toast.makeText(SettingsActivity.this, getResources().getString(R.string.password_updated_successfully), Toast.LENGTH_SHORT).show();
               }else{
                    currentPassword.setText("");
                   currentPassword.setError(getResources().getString(R.string.invalid_current_password));
               }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                waitingDialog.dismiss();
                Toast.makeText(SettingsActivity.this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void showHidePasswordLayoutContent(View view) {
        if (passwordExpandableLayout.isExpanded()) {
            passwordImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_down));
        } else {
            passwordImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_up));
        }
        passwordExpandableLayout.toggle();
    }

    public void showHideLanguagesLayoutContent(View view) {
        if (languageExpandableLayout.isExpanded()) {
            languageImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_down));
        } else {
            languageImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_up));
        }
        languageExpandableLayout.toggle();
    }


    public void changeLanguage(View view) {
        if (currentLanguage.equals("en") && !preferenceHelper.getLanguage().equals("en")) {
            changeCurrentLanguage("en");
        }
        if (currentLanguage.equals("ar") && !preferenceHelper.getLanguage().equals("ar")) {
            changeCurrentLanguage("ar");
        }
    }

    private void changeCurrentLanguage(final String language) {
        createWaitingDialog(getResources().getString(R.string.Changing_Language));
        waitingDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                preferenceHelper.setLanguage(language);
                Locale locale = new Locale(language);
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                getBaseContext().getResources().updateConfiguration(config,
                        getBaseContext().getResources().getDisplayMetrics());
                waitingDialog.dismiss();
                startActivity(getIntent());
                finish();
            }
        }, 2000);

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
        super.onBackPressed();
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

    private boolean isValid() {

        if (currentPassword.getText().toString().isEmpty()) {
            currentPassword.setError(getResources().getString(R.string.this_field_is_required));
            return false;
        }
        if (newPassword.getText().toString().isEmpty()) {
            newPassword.setError(getResources().getString(R.string.this_field_is_required));
            return false;
        }
        if (confirmPassword.getText().toString().isEmpty()) {
            confirmPassword.setError(getResources().getString(R.string.this_field_is_required));
            return false;
        }
        if (!confirmPassword.getText().toString().equals(newPassword.getText().toString())) {
            confirmPassword.setText("");
            confirmPassword.setError(getResources().getString(R.string.doesnt_match));
            return false;
        }
        if (newPassword.getText().toString().length() < 6) {
            newPassword.setText("");
            newPassword.setError(getResources().getString(R.string.password_must_be_at_least_6_character));
            confirmPassword.setText("");
        }
        return true;
    }

    private void createLoginDialog() {
        loginDialog = new Dialog(this, R.style.SheetDialog);
        loginDialog = new Dialog(this, R.style.SheetDialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_login, null);
        TextView waitingMessage = view.findViewById(R.id.dialog_login_text);
        waitingMessage.setText(getResources().getString(R.string.login_change_password));
        Button login = view.findViewById(R.id.dialog_login_button);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
                loginDialog.dismiss();
            }
        });
        loginDialog.setContentView(view);
        loginDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}