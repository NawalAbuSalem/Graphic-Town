package com.nns.graphictown.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.nns.graphictown.R;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }

    public void goToSignUpActivity(View view) {
        startActivity(new Intent(this,ChooseAccountTypeActivity.class));
    }

    public void goToLoginActivity(View view) {
        startActivity(new Intent(this,LoginActivity.class));
    }
}