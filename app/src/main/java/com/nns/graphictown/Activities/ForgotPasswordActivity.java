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
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.google.android.material.textfield.TextInputEditText;
import com.nns.graphictown.Helpers.Constants;
import com.nns.graphictown.Model.UnAuthNetworkUtils;
import com.nns.graphictown.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {
    private TextInputEditText emailEditText;
    private Dialog dialog;
    private UnAuthNetworkUtils networkUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        emailEditText = findViewById(R.id.email);
        networkUtils=UnAuthNetworkUtils.getInstance(this);
        createDialog();
    }
    public void back(View view) {
        finish();
    }

    public void sendPassword(View view) {
        if (isValid()){
            dialog.show();
            String email=emailEditText.getText().toString();
            Call<ResponseBody>call=networkUtils.getTownApiInterface().forgotPassword(email);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    dialog.dismiss();
                    if (response.isSuccessful()){
                        try {
                            String  responseString = response.body().string();
                            JSONObject jsonObject=new JSONObject(responseString);
                            int status=jsonObject.getInt("status");
                            if (status==1){
                                Toast.makeText(ForgotPasswordActivity.this, getResources().getString(R.string.message_is_sent), Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(ForgotPasswordActivity.this,VerifyOtpCodeActivity.class);
                                intent.putExtra("email",email);
                                startActivity(intent);
                            }else {
                                Toast.makeText(ForgotPasswordActivity.this, "Email is doesn't exist", Toast.LENGTH_SHORT).show();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                         catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    dialog.dismiss();
                    Toast.makeText(ForgotPasswordActivity.this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private boolean isValid() {
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
        return true;
    }
    private void createDialog() {
        dialog = new Dialog(this, R.style.SheetDialog);
        View view= LayoutInflater.from(this).inflate(R.layout.dialog_waiting,null);
        TextView waitingMessage=view.findViewById(R.id.waiting_message);
        waitingMessage.setText(getResources().getString(R.string.sending_message));
        dialog.setContentView(view);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
    }
}