package com.example.dulich;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

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

}
