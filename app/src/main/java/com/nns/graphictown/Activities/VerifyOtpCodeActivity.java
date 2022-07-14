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
import com.nns.graphictown.Model.UnAuthNetworkUtils;
import com.nns.graphictown.Model.user.User;
import com.nns.graphictown.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyOtpCodeActivity extends AppCompatActivity {
    private TextInputEditText codeEditText;
    private Dialog dialog;
    private UnAuthNetworkUtils networkUtils;
    private PreferenceHelper preferenceHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp_code);
        codeEditText = findViewById(R.id.email);
        networkUtils=UnAuthNetworkUtils.getInstance(this);
        preferenceHelper=new PreferenceHelper(this);
        createDialog();
    }
    private boolean isValid() {
        if (codeEditText.getText().toString().isEmpty()) {
            codeEditText.setError(getResources().getString(R.string.this_field_is_required));
            return false;
        }
        return true;
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
    public void back(View view) {
        finish();
    }

    public void sendVerificationCode(View view) {
        if (isValid()){
            dialog.show();
            Call<User> call=networkUtils.getTownApiInterface().verifyOtp(getIntent().getStringExtra("email"),codeEditText.getText().toString());
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    dialog.dismiss();
                    if (response.isSuccessful()){
                        preferenceHelper.setToken(response.body().getToken());
                        startActivity(new Intent(VerifyOtpCodeActivity.this,ResetPasswordActivity.class));
                    }
                }
                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    dialog.dismiss();
                    System.out.println(t.getMessage());
                    Toast.makeText(VerifyOtpCodeActivity.this, "invalid code", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}