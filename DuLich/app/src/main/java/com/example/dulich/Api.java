package com.example.dulich;

import java.util.Map;

import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

public interface Api {

    @FormUrlEncoded
    @POST("user/register")
    Call<ResponseBody> createUser(
            @Field("fullName") String name,
            @Field( "email" ) String email,
            @Field( "password" ) String password,
            @Field( "phone" ) String phone
    );


    @FormUrlEncoded
    @POST("user/login")
    Call<ResponseBody> logInUser(
            @Field("emailPhone") String email,
            @Field( "password" ) String password
    );

    @GET("/tour/list")
    Call<ResponseBody > getListTour(
            @Header("Authorization") String Authorization,
            @QueryMap Map<String,String> params
            );

    @FormUrlEncoded
    @POST("user/login/by-google")
    Call<ResponseBody> logInByGG(
            @Field("accessToken") String accessToken
    );

    @FormUrlEncoded
    @POST("user/login/by-facebook")
    Call<ResponseBody> logInByFB(
            @Field("accessToken") String accessToken
    );

}
