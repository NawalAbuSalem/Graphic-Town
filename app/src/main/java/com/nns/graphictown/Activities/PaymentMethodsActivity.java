package com.nns.graphictown.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.nns.graphictown.Helpers.PreferenceHelper;
import com.nns.graphictown.Model.PayPal.PayPalConfig;
import com.nns.graphictown.R;
import com.nns.graphictown.models.CategoryNetworkUtils;
import com.nns.graphictown.models.NetworkUtils;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentMethodsActivity extends AppCompatActivity {
   private CategoryNetworkUtils networkUtils;
   private PreferenceHelper preferenceHelper;
   private Dialog waitingDialog;
    //Paypal intent request code to track onActivityResult method
    public static final int PAYPAL_REQUEST_CODE = 123;


    //Paypal Configuration Object
    private static PayPalConfiguration config = new PayPalConfiguration()
            // Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
            // or live (ENVIRONMENT_PRODUCTION)
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(PayPalConfig.PAYPAL_CLIENT_ID);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_methods);
        networkUtils=CategoryNetworkUtils.getInstance(this);
        preferenceHelper=new PreferenceHelper(this);
        Intent intent = new Intent(this, PayPalService.class);
       intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);
    }

    private void createWaitingDialog(String message) {
        waitingDialog = new Dialog(this, R.style.SheetDialog);
        View view= LayoutInflater.from(this).inflate(R.layout.dialog_waiting,null);
        TextView waitingMessage=view.findViewById(R.id.waiting_message);
        waitingMessage.setText(message);
        waitingDialog.setContentView(view);
        waitingDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        waitingDialog.setCancelable(false);
    }

    public void back(View view) {
        finish();
    }

    public void payPalPayment(View view) {
        //Creating a paypalpayment
        String totalPrice=String.valueOf(getIntent().getDoubleExtra("price",0));
        System.out.println("///////////////");
        System.out.println(totalPrice);
        PayPalPayment payment = new PayPalPayment(new BigDecimal(totalPrice), "USD", "Simplified Coding Fee",
                PayPalPayment.PAYMENT_INTENT_SALE);

        //Creating Paypal Payment activity intent
        Intent intent = new Intent(this, PaymentActivity.class);

        //putting the paypal configuration to the intent
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        //Puting paypal payment to the intent
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

        //Starting the intent activity for result
        //the request code will be used on the method onActivityResult
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }

    public void cashPayment(View view) {
        createWaitingDialog(getResources().getString(R.string.ordering));
        waitingDialog.show();
        Call<ResponseBody> call=networkUtils.getTownApiInterface().cashOrder(getIntent().getIntExtra("id",0));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                waitingDialog.dismiss();
                if (response.isSuccessful()){
                    preferenceHelper.setCartCount("0");
                    startActivity(new Intent(PaymentMethodsActivity.this,OrderCompletedActivity.class));
                    finishAffinity();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                waitingDialog.dismiss();
                Toast.makeText(PaymentMethodsActivity.this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //If the result is from paypal
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYPAL_REQUEST_CODE) {
            //If the result is OK i.e. user has not canceled the payment
            if (resultCode == Activity.RESULT_OK) {
                //Getting the payment confirmation
                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                //if confirmation is not null
                if (confirm != null) {
                    try {
                        //Getting the payment details
                        JSONObject paymentDetails = confirm.toJSONObject();
                        String id=paymentDetails.getJSONObject("response").getString("id");
                        System.out.println("paymentExample"+ id);
                        confirmPayPalOrder("transactionId");
                        //Starting a new activity for the payment details and also putting the payment details with intent
                    } catch (JSONException e) {
                        Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("paymentExample", "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        }
    }

    private void confirmPayPalOrder(String transactionId) {
        createWaitingDialog(getResources().getString(R.string.ordering));
        waitingDialog.show();
        Call<ResponseBody> call=networkUtils.getTownApiInterface().payPalOrder(getIntent().getIntExtra("id",0),transactionId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                waitingDialog.dismiss();
                if (response.isSuccessful()){
                    preferenceHelper.setCartCount("0");
                    startActivity(new Intent(PaymentMethodsActivity.this,OrderCompletedActivity.class));
                    finishAffinity();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                waitingDialog.dismiss();
                Toast.makeText(PaymentMethodsActivity.this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
            }
        });
    }
}