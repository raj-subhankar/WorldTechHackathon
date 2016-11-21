package com.yellowsoft.worldtechhackathon.network;

import com.yellowsoft.worldtechhackathon.models.AuthResult;
import com.yellowsoft.worldtechhackathon.models.Post2;
import com.yellowsoft.worldtechhackathon.models.Result;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

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
    @PUT("users/{id}")
    Call<AuthResult> createProfile(@Path("id") String id,
                                   @Part("name") RequestBody name,
                                   @Part MultipartBody.Part file);

    @Multipart
    @POST("posts/add")
    Call<ResponseBody> upload(@Part("user") RequestBody name,
                              @Part MultipartBody.Part file,
                              @Part("postBody") RequestBody postBody,
                              @Part("lat") RequestBody lat,
                              @Part("lng") RequestBody lng);

    @GET("posts/all")
    Call<List<Post2>> getNewposts(@Query("lat") String lat,
                                  @Query("lng") String lng);

    @GET("posts/all")
    Call<List<Post2>> getMoreNewposts(@Query("lat") String lat,
                                      @Query("lng") String lng,
                                      @Query("lastid") String id);

    @FormUrlEncoded
    @POST("posts/like")
    Call<Result> like(@Field("post_id") String id,
                      @Field("userId") String user);
}
