package com.example.dulich;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.POST;

public interface Api {

    @POST("user/register")
    Call<ResponseBody> createUser(

    );
}
