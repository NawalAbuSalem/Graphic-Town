package com.nns.graphictown.models;

import com.nns.graphictown.Model.Cart.CartItem;
import com.nns.graphictown.Model.Order.Order;
import com.nns.graphictown.Model.Product.Product;
import com.nns.graphictown.Model.advertisement.Advertisement;
import com.nns.graphictown.Model.category.Category;
import com.nns.graphictown.Model.subscategory.SubCategory;
import com.nns.graphictown.Model.user.User;
import java.util.HashMap;
import java.util.List;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;

public interface TownApiInterface {

    @POST("user/login")
    Call<User> login(@Body HashMap<String, String> hashMap);

    @Multipart
    @POST("user/register")
    Call<User> signUp(@Part MultipartBody.Part img, @Part("name") String name, @Part("account_type") String userType,
                      @Part("mobile") String phone, @Part("email") String email, @Part("password") String password, @Part("password_confirmation") String passwordConfirmation, @Part("login_method") String normal);



    @FormUrlEncoded
    @POST("user/profile")
    Call<User> updateProfile(@Field("name") String name, @Field("email") String email,
                                     @Field("mobile") String phone, @Field("address") String address);

    @Multipart
    @POST("user/profile")
    Call<User> updateProfileWithImage(@PartMap HashMap<String, String> hashMap, @Part MultipartBody.Part img);



    @FormUrlEncoded
    @POST("setting/contact_us")
    Call<ResponseBody> sendMessageToHost(@Field("name") String name, @Field("email") String email,
                                         @Field("title") String title, @Field("description") String description);


    @FormUrlEncoded
    @POST("user/register")
    Call<User> socialMediaLogin( @Field("name") String name, @Field("account_type") String userType
                                , @Field("email") String email, @Field("image") String image, @Field("login_method") String social
            , @Field("password") String password, @Field("password_confirmation") String passwordConfirmation);

    @FormUrlEncoded
    @POST("user/password/update")
    Call<ResponseBody> updatePassword(@Field("current_password") String currentPassword, @Field("password") String newPassword,
                                      @Field("password_confirmation") String confirmPassword);

    @FormUrlEncoded
    @POST("user/otp/send")
    Call<ResponseBody> forgotPassword(@Field("email") String email);

    @FormUrlEncoded
    @POST("user/otp/verify")
    Call<User> verifyOtp(@Field("email") String email,@Field("otp_code") String otpCode);

    @FormUrlEncoded
    @POST("user/password/forgot/update")
    Call<ResponseBody> resetPassword(@Field("password") String password,@Field("password_confirmation") String passwordConfirmation);

    @GET("category")
    Call<Category> getCategories();

    @GET("category/{category_id}")
    Call<SubCategory> getSubCategoryFilter(@Path("category_id") int categoryId);


    @GET("category/{category_id}/subcategories/all/products")
    Call<Product> getAllCategoryProduct(@Path("category_id") int categoryId);


    @GET("category/subcategory/{subcategory_id}/products")
    Call<Product> getSubCategoryProducts(@Path("subcategory_id") int subCategoryId);


    @GET("category/agent/{company_id}")
    Call<Category> getCompanyCategories(@Path("company_id") int companyId);

    @Multipart
    @POST("cart")
    Call<ResponseBody> addItemToCart(@Part("product_id")int productId, @Part("amount")int amount,
                                     @Part("facebook_images[]") List<String> socialImagesUri, @Part MultipartBody.Part[] galleryImages);

    @GET("cart")
    Call<CartItem>getCartItems();

    @FormUrlEncoded
    @POST("cart/item/{id}/update")
    Call<ResponseBody> updateCartQuantity(@Path("id") int id,@Field("amount") int amount);

    @DELETE("cart/{id}")
    Call<ResponseBody> deleteCartItem(@Path("id") int id);


    @GET("setting/advertisements")
    Call<Advertisement> getAppAds();

    @FormUrlEncoded
    @POST("cart/{id}/delivery/location")
    Call<ResponseBody> updateLocation(@Path("id") int id,@Field("delivery_location") String location);

    @POST("cart/{id}/pay/cash")
    Call<ResponseBody> cashOrder(@Path("id") int id);

    @FormUrlEncoded
    @POST("cart/{id}/pay/paypal")
    Call<ResponseBody> payPalOrder(@Path("id") int id,@Field("transaction_id") String transactionId);


    @GET("order")
    Call<Order> getOrders();

    @FormUrlEncoded
    @POST("order/{id}/rate")
    Call<ResponseBody> rateOrder(@Path("id") int id,@Field("rate_value") int rate);

}

