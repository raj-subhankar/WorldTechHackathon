package com.yellowsoft.worldtechhackathon;

import com.yellowsoft.worldtechhackathon.models.AuthResult;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by subhankar on 11/21/2016.
 */
public interface ApiInterface {

    @FormUrlEncoded
    @POST("authenticate")
    Call<AuthResult> authenticate(@Field("name") String name,
                                  @Field("password") String password);
}
