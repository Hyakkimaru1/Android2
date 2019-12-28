package com.ygaps.travelapp;

import java.io.File;
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
import retrofit2.http.Query;
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

    @GET("tour/list")
    Call<ResponseBody > getListTour(
            @Header("Authorization") String Authorization,
            @Query( "rowPerPage" ) int rowPerPage,
            @Query( "pageNum" ) int pageNum
            );

    @GET("tour/get/feedback-service")
    Call<ResponseBody > getlistReview(
            @Header("Authorization") String Authorization,
            @Query( "serviceId" ) int serviceId,
            @Query( "pageIndex" ) int pageIndex,
            @Query( "pageSize" ) int pageSize
    );

    @GET("tour/history-user")
    Call<ResponseBody > getHistoryTourUser(
            @Header("Authorization") String Authorization,
            @QueryMap Map<String,String> params
    );

    @GET("user/search")
    Call<ResponseBody> searchFriend(
            @Query( "searchKey" )  String key,
            @Query( "pageIndex" )  int pageIndex,
            @Query( "pageSize" )  int pageSize
    );

    @GET("tour/info")
    Call<ResponseBody> getTourInfo(
            @Header("Authorization") String Authorization,
            @Query( "tourId" )  int tourId
    );

    @GET("tour/get/invitation")
    Call<ResponseBody> getTourInvitation(
            @Header("Authorization") String Authorization,
            @Query( "pageIndex" )  int pageIndex,
            @Query( "pageSize" )  int pageSize
    );

    @GET("tour/comment-list")
    Call<ResponseBody> getCommentList(
            @Header("Authorization") String Authorization,
            @Query( "tourId" )  int tourId,
            @Query( "pageIndex" )  int pageIndex,
            @Query( "pageSize" )  int pageSize
    );

    @GET("/tour/search/service")
    Call<ResponseBody> getDestination(
            @Header("Authorization") String Authorization,
            @Query( "searchKey" )  String searchKey,
            @Query( "pageIndex" )  int pageIndex,
            @Query( "pageSize" )  int pageSize
    );


    @FormUrlEncoded
    @POST("user/login/by-google")
    Call<ResponseBody> logInByGG(
            @Field("accessToken") String accessToken
    );

    @FormUrlEncoded
    @POST("tour/add/feedback-service")
    Call<ResponseBody> sendFeedback(
            @Header("Authorization") String Authorization,
            @Field( "serviceId" ) int serviceId,
            @Field( "feedback" ) String feedback,
             @Field( "point" ) int point
    );

    @FormUrlEncoded
    @POST("tour/comment")
    Call<ResponseBody> sendComment(
            @Header("Authorization") String Authorization,
            @Field( "tourId" ) int tourId,
            @Field( "userId" ) int userId,
            @Field( "comment" ) String comment
    );

    @FormUrlEncoded
    @POST("tour/current-users-coordinate")
    Call<ResponseBody> sendCoordinate (
            @Header("Authorization") String Authorization,
            @Field( "userId" ) int userId,
            @Field( "tourId" ) int tourId,
            @Field( "lat" ) double lat,
            @Field( "long" ) double lng

    );

    @FormUrlEncoded
    @POST("tour/add/notification-on-road")
    Call<ResponseBody> sendNotification (
            @Header("Authorization") String Authorization,
            @Field( "lat" ) double lat,
            @Field( "long" ) double lng,
            @Field( "tourId" ) int tourId,
            @Field( "userId" ) int userId,
            @Field( "notificationType" ) int type,
            @Field( "speed" ) int speed,
            @Field( "note" ) String note
    );
    @FormUrlEncoded
    @POST("tour/add/notification-on-road")
    Call<ResponseBody> sendNotification (
            @Header("Authorization") String Authorization,
            @Field( "lat" ) double lat,
            @Field( "long" ) double lng,
            @Field( "tourId" ) int tourId,
            @Field( "userId" ) int userId,
            @Field( "notificationType" ) int type,
            @Field( "note" ) String note
    );

    @GET("tour/get/noti-on-road")
    Call<ResponseBody> getNotification(
            @Header("Authorization") String Authorization,
            @Query( "tourId" )  int tourId,
            @Query( "pageIndex" )  int pageIndex,
            @Query( "pageSize" )  int pageSize
    );

    @FormUrlEncoded
    @POST("tour/notification")
    Call<ResponseBody> sendChatChat (
            @Header("Authorization") String Authorization,
            @Field( "tourId" ) int tourId,
            @Field( "userId" ) int userId,
            @Field( "noti" ) String noti
    );

    @FormUrlEncoded
    @POST("tour/finish-trip")
    Call<ResponseBody> finshTrip (
            @Header("Authorization") String Authorization,
            @Field( "tourId" ) int tourId
    );

    @GET("tour/notification-list")
    Call<ResponseBody> getChatChat(
            @Header("Authorization") String Authorization,
            @Query( "tourId" )  int tourId,
            @Query( "pageIndex" )  int pageIndex,
            @Query( "pageSize" )  int pageSize
    );

    @GET("tour/get/service-detail")
    Call<ResponseBody> getDetailSP(
            @Header("Authorization") String Authorization,
            @Query( "serviceId" )  int serviceId
    );

    @GET("tour/get/feedback-point-stats")
    Call<ResponseBody> getPointStatusSP(
            @Header("Authorization") String Authorization,
            @Query( "serviceId" )  int serviceId
    );

    @FormUrlEncoded
    @POST("/user/notification/put-token")
    Call<ResponseBody> registerFirebase(
            @Header("Authorization") String Authorization,
            @Field("fcmToken") String fcmToken,
            @Field("deviceId") String deviceId,
            @Field("platform") Number platform,
            @Field("appVersion") String appVersion
    );

    @FormUrlEncoded
    @POST("/tour/recording")
    Call<ResponseBody> sendRecordFile(
            @Header("Authorization") String Authorization,
            @Field( "file" ) File file,
            @Field( "tourId" ) int tourId,
            @Field( "fullName" ) String fullName,
            @Field( "avatar" ) String Avatar,
            @Field( "lat" ) int lat,
            @Field( "long" ) int longitude
    );

    @FormUrlEncoded
    @POST("user/login/by-facebook")
    Call<ResponseBody> logInByFB(
            @Field("accessToken") String accessToken
    );

    @FormUrlEncoded
    @POST("tour/response/invitation")
    Call<ResponseBody> joiningTour(
            @Header("Authorization") String Authorization,
            @Field( "tourId" ) String tourId,
            @Field( "isAccepted" ) int isAccepted

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
    @POST("tour/update-tour")
    Call<ResponseBody> updateTour(
            @Header("Authorization") String accessToken,
            @Field( "id" ) String id,
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

    @POST("/tour/suggested-destination-list")
    Call<ResponseBody> suggest_Stoppoint(
            @Header("Authorization") String token,
            @Body getSuggest_Stoppoint getSuggest_stoppoint
            );


    @FormUrlEncoded
    @POST("tour/update-tour")
    Call<ResponseBody> updateDelTour(
            @Header("Authorization") String token,
            @Field( "id" ) String id,
            @Field( "status" ) int status
    );


    @Headers("Content-Type: application/json")
    @POST("tour/set-stop-points")
    Call<ResponseBody> stopPointsSet(
            @Header("Authorization") String token,
            @Body serviceStopPoints serviceStopPoints


    );

    @FormUrlEncoded
    @POST("user/notification/put-token")
    Call<ResponseBody> register_Firebase(
            @Header("Authorization") String token,
            @Field( "fcmToken" ) String fcmToken,
            @Field( "deviceId" ) String deviceId,
            @Field( "platform" ) int platform,
            @Field( "appVersion" ) String appVersion
    );

    @FormUrlEncoded
    @POST("tour/add/member")
    Call<ResponseBody> invite_friend(
            @Header("Authorization") String Authorization,
            @Field( "tourId" ) String tourId,
            @Field( "invitedUserId" ) String invitedUserId,
            @Field( "isInvited" ) boolean isInvited
    );


}
