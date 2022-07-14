package com.nns.graphictown.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.nns.graphictown.R;

public class OrderCompletedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_completed);
    }

    public void checkTheOrder(View view) {
     startActivity(new Intent(this,OrdersActivity.class));
     finish();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this,MainActivity.class));
        super.onBackPressed();
    }
}