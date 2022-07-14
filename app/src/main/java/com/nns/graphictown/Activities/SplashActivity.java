package com.nns.graphictown.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;

import com.nns.graphictown.Helpers.PreferenceHelper;
import com.nns.graphictown.R;
import com.nns.graphictown.models.NetworkUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {
   private PreferenceHelper preferenceHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        preferenceHelper=new PreferenceHelper(this);
        changeCurrentLanguage(preferenceHelper.getLanguage());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (preferenceHelper.isAppLogin()){
                    startActivity(new Intent(getBaseContext(),MainActivity.class));
                }else {
                    startActivity(new Intent(getBaseContext(),ChooseAccountTypeActivity.class));
                }
                finish();
            }
        }, 2000);
    }
    private void changeCurrentLanguage(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }
}