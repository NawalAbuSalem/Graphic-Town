package com.nns.graphictown.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nns.graphictown.Helpers.Constants;
import com.nns.graphictown.Helpers.PreferenceHelper;
import com.nns.graphictown.Model.UnAuthNetworkUtils;
import com.nns.graphictown.Model.user.User;
import com.nns.graphictown.R;
import com.nns.graphictown.models.CartNetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText emailEditText, passwordEditText;
    private UnAuthNetworkUtils unAuthNetworkUtils;
    private Dialog loginDialog;
    private static final int RG_SIGN_IN = 100;
    private CartNetworkUtils networkUtils;
    private PreferenceHelper preferenceHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailEditText = findViewById(R.id.email_sign_in);
        passwordEditText = findViewById(R.id.password_sign_in);
        unAuthNetworkUtils = UnAuthNetworkUtils.getInstance(this);
        networkUtils=CartNetworkUtils.getInstance(this);
        preferenceHelper=new PreferenceHelper(this);
        createLoginDialog();
    }





    private void createLoginDialog() {
        loginDialog = new Dialog(this, R.style.SheetDialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_waiting, null);
        TextView waitingMessage = view.findViewById(R.id.waiting_message);
        waitingMessage.setText(getResources().getString(R.string.Log_in));
        loginDialog.setContentView(view);
        loginDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loginDialog.setCancelable(false);
    }

    public void goToForgotPasswordActivity(View view) {
        startActivity(new Intent(this, ForgotPasswordActivity.class));
    }

    public void login(View view) {
        if (isValid()) {
            loginDialog.show();
            HashMap<String, String> loginHashMap = new HashMap<>();
            loginHashMap.put(Constants.USER_EMAIL, emailEditText.getText().toString());
            loginHashMap.put(Constants.USER_PASSWORD, passwordEditText.getText().toString());
            Call<User> responseBodyCall = unAuthNetworkUtils.getTownApiInterface().login(loginHashMap);
            responseBodyCall.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful()) {
                        preferenceHelper.setUserInformation(response.body().getUser().getName(),response.body().getUser().getEmail(),response.body().getUser().getMobile(),response.body().getUser().getAccountType());
                        preferenceHelper.setUserImageUrl(response.body().getUser().getImage());
                        preferenceHelper.setLogin(true);
                        preferenceHelper.setToken(response.body().getToken());
                        preferenceHelper.setAddress(response.body().getUser().getAddress());
                        preferenceHelper.setCartCount(String.valueOf(response.body().getCartCount()));
                        loginDialog.dismiss();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finishAffinity();
                    } else {
                        loginDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "invalid Email or password", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    loginDialog.dismiss();
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    public void facebookLogin(View view) {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.FacebookBuilder().build());
        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build(),
                RG_SIGN_IN
        );
    }

    public void googleLogin(View view) {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build());
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build(),
                RG_SIGN_IN
        );
    }

    public void createNewAccount(View view) {
        Intent intent=new Intent(this, ChooseAccountTypeActivity.class);
        intent.putExtra("CreateNewAccount","true");
        startActivity(intent);
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
        if (passwordEditText.getText().toString().isEmpty()) {
            passwordEditText.setError(getResources().getString(R.string.this_field_is_required));
            return false;
        }
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK&&requestCode==RG_SIGN_IN) {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            final Intent intent=new Intent(this,ChooseAccountTypeActivity.class);
            intent.putExtra("isSocialMediaLogin","true");
            intent.putExtra(Constants.USER_NAME,currentUser.getDisplayName());
            intent.putExtra(Constants.IMAGE_URL,currentUser.getPhotoUrl().toString() + "?height=1000");
            System.out.println(currentUser.getPhotoUrl().toString() + "?height=1000");
            intent.putExtra(Constants.USER_EMAIL,currentUser.getEmail());
            AuthUI.getInstance()
                    .signOut(LoginActivity.this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            startActivity(intent);
                            finish();
                        }
                    });

        }

    }

}