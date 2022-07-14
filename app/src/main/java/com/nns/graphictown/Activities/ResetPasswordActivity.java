package com.nns.graphictown.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.nns.graphictown.Helpers.PreferenceHelper;
import com.nns.graphictown.Model.AuthNetworkUtils;
import com.nns.graphictown.R;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {
    private TextInputEditText passwordEditText;
    private Dialog dialog;
    private AuthNetworkUtils networkUtils;
    private PreferenceHelper preferenceHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        passwordEditText = findViewById(R.id.email);
        networkUtils=AuthNetworkUtils.getInstance(this);
        preferenceHelper=new PreferenceHelper(this);
        createDialog();
    }
    private void createDialog() {
        dialog = new Dialog(this, R.style.SheetDialog);
        View view= LayoutInflater.from(this).inflate(R.layout.dialog_waiting,null);
        TextView waitingMessage=view.findViewById(R.id.waiting_message);
        waitingMessage.setText(getResources().getString(R.string.loading));
        dialog.setContentView(view);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
    }

    private boolean isValid() {
        if (passwordEditText.getText().toString().isEmpty()) {
            passwordEditText.setError(getResources().getString(R.string.this_field_is_required));
            return false;
        }
        if (passwordEditText.getText().toString().length()<6) {
            passwordEditText.setError(getResources().getString(R.string.password_must_be_at_least_6_character));
            return false;
        }
        return true;
    }
    public void back(View view) {
        preferenceHelper.clear();
        finish();

    }
    @Override
    public void onBackPressed() {
        preferenceHelper.clear();
        super.onBackPressed();
    }

    public void resetPassword(View view) {
        if (isValid()){
            dialog.show();
            Call<ResponseBody> call=networkUtils.getTownApiInterface().resetPassword(passwordEditText.getText().toString(),passwordEditText.getText().toString());
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    dialog.dismiss();
                    if (response.isSuccessful()){
                        preferenceHelper.clear();
                        startActivity(new Intent(ResetPasswordActivity.this,LoginActivity.class));
                        finishAffinity();
                    }else{
                        Toast.makeText(ResetPasswordActivity.this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    dialog.dismiss();
                    Toast.makeText(ResetPasswordActivity.this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
}