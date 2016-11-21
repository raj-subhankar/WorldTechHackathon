package com.yellowsoft.worldtechhackathon.network;

import com.yellowsoft.worldtechhackathon.models.AuthResult;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by subhankar on 11/21/2016.
 */
public interface ApiInterface {

    @FormUrlEncoded
    @POST("authenticate")
    Call<AuthResult> authenticate(@Field("name") String name,
                                  @Field("password") String password);

    @FormUrlEncoded
    @POST("users/add")
    Call<AuthResult> register(@Field("email") String email,
                              @Field("password") String password);

    @Multipart
    @POST("posts/add")
    Call<ResponseBody> upload(@Part("user") RequestBody name,
                              @Part MultipartBody.Part file,
                              @Part("postBody") RequestBody postBody,
                              @Part("lat") RequestBody lat,
                              @Part("lng") RequestBody lng);
}
