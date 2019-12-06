package com.example.dulich;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

public interface Api<I extends Number> {

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

    @GET("/tour/history-user")
    Call<ResponseBody > getHistoryTourUser(
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

    @FormUrlEncoded
    @POST("tour/create")
    Call<ResponseBody> createTour(
            @Header("Authorization") String accessToken,
            @Field( "name" ) String name,
            @Field("startDate") long startDate,
            @Field( "endDate" ) long endDate,
            @Field("sourceLat") double sourceLat,
            @Field( "sourceLong" ) double sourceLong,
            @Field("desLat") double desLat,
            @Field( "desLong" ) double desLong,
            @Field( "isPrivate" ) boolean isPrivate,
            @Field( "adults" ) int  adults,
            @Field( "childs" ) int  childs,
            @Field( "minCost" ) long minCost,
            @Field( "maxCost" ) long maxCost
            //@Field("avatar") String avatar

    );

    @FormUrlEncoded
    @POST("/tour/set-stop-points")
    Call<ResponseBody> addStopPoint(
            @Header("Authorization") String token,
            @Field( "tourID" ) String tourID,
            @Field("stopPoints") List<String> stopPoints
            //@Field( "deleteIds" ) id[] deleteIds
    );

<<<<<<< HEAD
    @FormUrlEncoded
    @POST("/tour/update-tour")
    Call<ResponseBody> updateDelTour(
            @Header("Authorization") String token,
            @Field( "id" ) String password,
            @Field( "status" ) int status
    );

    @FormUrlEncoded
    @POST("/tour/set-stop-points")
    Call<Api<Integer>> stopPointsSet(@Body serviceStopPoints serviceStopPoints);
=======
    @Headers("Content-Type: application/json")
    @POST("tour/set-stop-points")
    Call<ResponseBody> stopPointsSet(
            @Header("Authorization") String token,
            @Body serviceStopPoints serviceStopPoints
>>>>>>> b9dd82c28b282a2d4a8e5dd2e6cf3b3287b00180

    );
}
