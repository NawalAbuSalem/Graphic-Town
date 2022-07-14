package com.nns.graphictown.models;


import android.content.Context;

import com.bumptech.glide.RequestBuilder;
import com.google.gson.internal.$Gson$Preconditions;
import com.nns.graphictown.Helpers.PreferenceHelper;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class NetworkUtils {
    private static final String BASE_URL = "https://abood-sniper.herokuapp.com/api/";
    private static NetworkUtils networkUtils;
    private TownApiInterface townApiInterface;
    private Retrofit retrofit;
    private NetworkUtils(Context context) {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(getOkHttpClient(context))
                .build();
        townApiInterface = retrofit.create(TownApiInterface.class);

    }

    public static NetworkUtils getInstance(Context context) {
        if (networkUtils == null) {
            networkUtils = new NetworkUtils(context);
        }
        return networkUtils;
    }

    private static OkHttpClient getOkHttpClient(final Context context) {
        return new OkHttpClient.Builder().addInterceptor(new HttpLoggingInterceptor())
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .addNetworkInterceptor(new Interceptor() {
                    @NotNull
                    @Override
                    public Response intercept(@NotNull Chain chain) throws IOException {
                        Request.Builder builder = chain.request().newBuilder();
                        builder.addHeader("Accept", "application/json");
                        PreferenceHelper preferenceHelper = new PreferenceHelper(context);
                        if (preferenceHelper.isAppLogin()) {
                            builder.addHeader("Authorization", "Bearer " + preferenceHelper.getToken());
                        }
                        Request request = builder.build();
                        return chain.proceed(request);
                    }
                }).build();

    }

    public TownApiInterface getTownApiInterface() {
        return townApiInterface;
    }
}

