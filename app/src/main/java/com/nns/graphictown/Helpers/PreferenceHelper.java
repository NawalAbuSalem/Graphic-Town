package com.nns.graphictown.Helpers;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.MutableLiveData;

import static android.content.Context.MODE_PRIVATE;

public class PreferenceHelper {

    private static final String PREF_NAME ="graphic_town_pref" ;
    private static final String FACEBOOK_USER_ID = "facebook user id";
    private static final String FACEBOOK_TOKEN = "facebook user token";
    private static final String APP_TOKEN = "app token";
    private static final String USER_NAME = "user name";
    private static final String USER_EMAIL = "user email";
    private static final String USER_ACCOUNT_TYPE = "account type";
    private static final String USER_PHONE_NUMBER = "phone number";
    private static final String USER_PASSWORD = "user password";
    private static final String USER_ADDRESS = "user address";
    private static final String USER_IMAGE_URL = "user image url";
    private static final String CART_COUNT = "number of cart items";
    private static final String APP_LANGUAGE = "language";
    private static final String APP_LOGIN = "is login";
    private SharedPreferences sharedPreferences;
    public PreferenceHelper(Context context) {
        sharedPreferences=context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
    }
    public void facebookLogin(String userId,String token){
        sharedPreferences.edit().putString(FACEBOOK_USER_ID,userId)
                .putString(FACEBOOK_TOKEN,token)
                .apply();

    }
    public String getFacebookUserID(){
        if (sharedPreferences.contains(FACEBOOK_USER_ID)){
            return sharedPreferences.getString(FACEBOOK_USER_ID,null);
        }
        return null;
    }
    public String getFacebookUserToken(){
        if (sharedPreferences.contains(FACEBOOK_TOKEN)){
            return sharedPreferences.getString(FACEBOOK_TOKEN,null);
        }
        return null;
    }
    public boolean isFacebookLogin(){
        if (sharedPreferences.contains(FACEBOOK_USER_ID)&&sharedPreferences.contains(FACEBOOK_TOKEN)){
            return true;
        }
        return false;
    }
    public void setLogin(boolean isLogin){
        sharedPreferences.edit()
                .putBoolean(APP_LOGIN,isLogin)
                .apply();
    }
    public void setUserInformation(String name,String email,String phoneNumber,String accountType){
        sharedPreferences.edit()
                .putString(USER_NAME,name)
                .putString(USER_EMAIL,email)
                .putString(USER_PHONE_NUMBER,phoneNumber)
                .putString(USER_ACCOUNT_TYPE,accountType)
                .apply();
    }
    public void setUserImageUrl(String imageUrl){
        sharedPreferences.edit()
                .putString(USER_IMAGE_URL,imageUrl)
                .apply();
    }
    public void setUserAccountType(String accountType){
        sharedPreferences.edit()
                .putString(USER_ACCOUNT_TYPE,accountType)
                .apply();
    }
    public void setToken(String token){
        sharedPreferences.edit()
                .putString(APP_TOKEN,token)
                .apply();
    }
    public boolean isAppLogin(){
        if (sharedPreferences.contains(APP_LOGIN)){
            return true;
        }
        return false;
    }

    public String getToken() {
        if (sharedPreferences.contains(APP_TOKEN)){
            return sharedPreferences.getString(APP_TOKEN,null);
        }
        return null;
    }
    public String getUserName() {
        if (sharedPreferences.contains(USER_NAME)){
            return sharedPreferences.getString(USER_NAME,"");
        }
        return "";
    }

    public String getUserEmail() {
        if (sharedPreferences.contains(USER_EMAIL)){
            return sharedPreferences.getString(USER_EMAIL,"");
        }
        return "";
    }
    public String getUserPhoneNumber() {
        if (sharedPreferences.contains(USER_PHONE_NUMBER)){
            return sharedPreferences.getString(USER_PHONE_NUMBER,"");
        }
        return "";
    }
    public String getUserImageUrl() {
        if (sharedPreferences.contains(USER_IMAGE_URL)){
            return sharedPreferences.getString(USER_IMAGE_URL,null);
        }
        return null;
    }
    public String getUserAddress() {
        if (sharedPreferences.contains(USER_ADDRESS)){
            return sharedPreferences.getString(USER_ADDRESS,null);
        }
        return null;
    }
    public String getAccountType() {
        if (sharedPreferences.contains(USER_ACCOUNT_TYPE)){
            return sharedPreferences.getString(USER_ACCOUNT_TYPE,null);
        }
        return null;
    }
    public void updateUserInformation(String name, String email, String phoneNumber, String address){
        sharedPreferences.edit()
                .putString(USER_NAME,name)
                .putString(USER_EMAIL,email)
                .putString(USER_PHONE_NUMBER,phoneNumber)
                .putString(USER_ADDRESS,address)
                .apply();
    }
    public void clear(){
        sharedPreferences.edit().clear().apply();
    }

    public void setAddress(String address) {
        sharedPreferences.edit()
                .putString(USER_ADDRESS,address)
                .apply();
    }
    public String getLanguage() {
        if (sharedPreferences.contains(APP_LANGUAGE)){
            return sharedPreferences.getString(APP_LANGUAGE,"en");
        }
        return "en";
    }
    public void setLanguage(String language) {
        sharedPreferences.edit()
                .putString(APP_LANGUAGE,language)
                .apply();
    }

    public void setCartCount(String count){
        sharedPreferences.edit().putString(CART_COUNT,count)
                .apply();

    }
    public String getCartCount() {
        if (sharedPreferences.contains(CART_COUNT)){
            return sharedPreferences.getString(CART_COUNT,"0");
        }
            return "0";

    }
}
