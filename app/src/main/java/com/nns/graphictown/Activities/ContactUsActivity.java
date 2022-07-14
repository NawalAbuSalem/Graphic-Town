package com.nns.graphictown.Activities;

import android.app.Dialog;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.nns.graphictown.Model.UnAuthNetworkUtils;
import com.nns.graphictown.R;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class ContactUsActivity extends AppCompatActivity {
    private EditText nameEditText, emailEditText, titleEditText, messageEditText;
    private Dialog dialog;
    private UnAuthNetworkUtils networkUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        nameEditText = findViewById(R.id.contact_us_name);
        emailEditText = findViewById(R.id.contact_us_email);
        titleEditText = findViewById(R.id.contact_us_address);
        messageEditText = findViewById(R.id.contact_us_message);
        messageEditText.setMovementMethod(new ScrollingMovementMethod());
        networkUtils = UnAuthNetworkUtils.getInstance(this);
        createWaitingDialog();
    }

    public void back(View view) {
        finish();
    }

    public void sendMessage(View view) {
        if (isValid()) {
            dialog.show();
            Call<ResponseBody> responseBodyCall=networkUtils.getTownApiInterface().sendMessageToHost(nameEditText.getText().toString(),emailEditText.getText().toString(),titleEditText.getText().toString(),messageEditText.getText().toString());
            responseBodyCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    dialog.dismiss();
                    if (response.isSuccessful()){
                        showSuccessMessage();
                    }else {
                        Toast.makeText(ContactUsActivity.this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    dialog.dismiss();
                    Toast.makeText(ContactUsActivity.this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    private boolean isValid() {
        String regex = "^(.+)@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(emailEditText.getText().toString());
        if (nameEditText.getText().toString().isEmpty()) {
            nameEditText.setError(getResources().getString(R.string.this_field_is_required));
            return false;
        }
        if (emailEditText.getText().toString().isEmpty()) {
            emailEditText.setError(getResources().getString(R.string.this_field_is_required));
            return false;
        }
        if (!matcher.matches()) {
            emailEditText.setText("");
            emailEditText.setError(getResources().getString(R.string.Enter_valid_email));
            return false;
        }
        if (titleEditText.getText().toString().isEmpty()) {
            titleEditText.setError(getResources().getString(R.string.this_field_is_required));
            return false;
        }
        if (messageEditText.getText().toString().isEmpty()) {
            messageEditText.setError(getResources().getString(R.string.this_field_is_required));
            return false;
        }

        return true;
    }

    private void createWaitingDialog() {
        dialog = new Dialog(this, R.style.SheetDialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_waiting, null);
        TextView waitingMessage = view.findViewById(R.id.waiting_message);
        waitingMessage.setText(getResources().getString(R.string.sending_message));
        dialog.setContentView(view);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
    }

    private void showSuccessMessage() {
        Dialog contactUsDialog = new Dialog(this, R.style.SheetDialog);
        contactUsDialog.setContentView(R.layout.dialog_contact_us_message);
        contactUsDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        contactUsDialog.show();
    }
}