package com.nns.graphictown.models;

import android.content.Context;

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

public class CategoryNetworkUtils {
    private static final String BASE_URL = "http://tographic.com/api/";
    private static CategoryNetworkUtils networkUtils;
    private TownApiInterface townApiInterface;
    private Retrofit retrofit;
    private CategoryNetworkUtils(Context context) {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(getOkHttpClient(context))
                .build();
        townApiInterface = retrofit.create(TownApiInterface.class);

    }

    public static CategoryNetworkUtils getInstance(Context context) {
        if (networkUtils == null) {
            networkUtils = new CategoryNetworkUtils(context);
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
                    public Response intercept(@NotNull Interceptor.Chain chain) throws IOException {
                        Request.Builder builder = chain.request().newBuilder();
                        builder.addHeader("Accept", "application/json");
                        PreferenceHelper preferenceHelper = new PreferenceHelper(context);
                        builder.addHeader("Authorization", "Bearer " + preferenceHelper.getToken());
                        builder.addHeader("X-localization", preferenceHelper.getLanguage());
                        Request request = builder.build();
                        return chain.proceed(request);
                    }
                }).build();

    }
    public TownApiInterface getTownApiInterface() {
        return townApiInterface;
    }
}


