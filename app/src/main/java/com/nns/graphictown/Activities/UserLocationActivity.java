package com.nns.graphictown.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nns.graphictown.R;
import com.nns.graphictown.models.CategoryNetworkUtils;

import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserLocationActivity extends FragmentActivity  implements OnMapReadyCallback {


    private Marker MyMarker;
    private GoogleMap mMap;
    private double latitude = 0.0;
    private double longitude = 0.0;
    private String MyAddress;
    public static final int RC_LOCATION_PERMISSION = 1;
    private FusedLocationProviderClient fusedLocation;
    private CategoryNetworkUtils networkUtils;
    private Dialog waitingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_location);
        networkUtils=CategoryNetworkUtils.getInstance(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        fusedLocation = LocationServices.getFusedLocationProviderClient(this);
        createWaitingDialog();
    }
    private void createWaitingDialog() {
        waitingDialog = new Dialog(this, R.style.SheetDialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_waiting, null);
        TextView waitingMessage = view.findViewById(R.id.waiting_message);
        waitingMessage.setText(getResources().getString(R.string.ordering));
        waitingDialog.setContentView(view);
        waitingDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        waitingDialog.setCancelable(false);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setCompassEnabled(false);
        googleMap.clear();


        mMap.setOnMapClickListener(latLng -> {
            latitude = latLng.latitude;
            longitude = latLng.longitude;
            moveMap(latitude, longitude);
            setAddress(latitude, longitude);
        });

        checkGps();
    }


    public void showSettingAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(getResources().getString(R.string.locationGpsTitle));
        alertDialog.setMessage(getResources().getString(R.string.gpsNotEnabled));
        alertDialog.setPositiveButton(getResources().getString(R.string.Settings), (dialog, which) -> {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, 1);
        });
        alertDialog.setNegativeButton(getResources().getString(R.string.Cancel), (dialog, which) -> {
            dialog.cancel();
            finish();
        });

        alertDialog.setOnCancelListener(dialog -> {
            // if from activity
            finish();
            dialog.cancel();
        });

        alertDialog.show();
    }


    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(UserLocationActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(UserLocationActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, RC_LOCATION_PERMISSION);
            return;
        }
        Log.e("state_", "5");
        getCurrentPosition();
    }

    @SuppressLint("MissingPermission")
    private void getCurrentPosition() {
        fusedLocation.getLastLocation().addOnSuccessListener(UserLocationActivity.this, location -> {
            // Got last known location. In some rare situations this can be null.
            if (location != null) {
                longitude = location.getLongitude();
                latitude = location.getLatitude();
                moveMap(latitude, longitude);
                setAddress(latitude, longitude);
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC_LOCATION_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentPosition();
        }
    }


    private void checkGps() {
        int locationMode = 1;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Log.e("state_", "1");
            try {
                locationMode = Settings.Secure.getInt(UserLocationActivity.this.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            boolean b = (locationMode == Settings.Secure.LOCATION_MODE_HIGH_ACCURACY); //check location mode
            if (b) {


                getCurrentLocation();

            } else {
                Log.e("state_", "4");

                showSettingAlert();
            }
        } else {
            Log.e("state_", "2");

            showSettingAlert();

        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            checkGps();
        }
    }


    private void moveMap(final double latitude, final double longitude) {
        Log.e("latitude", latitude + "");
        Log.e("longitude", longitude + "");
        this.latitude=latitude;
        this.longitude=longitude;
        if (MyMarker != null) {
            MyMarker.remove();
        }
        LatLng latLng = new LatLng(latitude, longitude);
        MyMarker = mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).title(MyAddress));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        mMap.getUiSettings().setZoomControlsEnabled(false);
        setAddress(latitude, longitude);

    }

    private void setAddress(double latitude, double longitude) {
        try {
            Geocoder geo = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geo.getFromLocation(latitude, longitude, 1);
            if (addresses.isEmpty()) {
                MyAddress = "Unnamed Road";
            } else {
                if (addresses.size() > 0) {
                    MyAddress = addresses.get(0).getFeatureName() + ", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void back(View view) {
        onBackPressed();
    }

    public void selectLocation(View view) {
        waitingDialog.show();
        Call<ResponseBody>call=networkUtils.getTownApiInterface().updateLocation(getIntent().getIntExtra("id",0),(latitude+","+longitude));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                waitingDialog.dismiss();
                if (response.isSuccessful()){
                    Intent intent=new Intent(UserLocationActivity.this,PaymentMethodsActivity.class);
                    intent.putExtra("id",getIntent().getIntExtra("id",0));
                    intent.putExtra("price",getIntent().getDoubleExtra("price",0));
                    startActivity(intent);
                }else {
                    Toast.makeText(UserLocationActivity.this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                waitingDialog.dismiss();
                Toast.makeText(UserLocationActivity.this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
