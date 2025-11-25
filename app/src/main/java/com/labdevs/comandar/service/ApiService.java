package com.labdevs.comandar.service;

import com.labdevs.comandar.data.dto.LoginRequest;
import com.labdevs.comandar.data.dto.RegisterRequest;
import com.labdevs.comandar.data.dto.UserResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

    @POST("auth/register")
    Call<UserResponse> register(@Body RegisterRequest request);

    @POST("auth/login")
    Call<UserResponse> login(@Body LoginRequest request);

    @GET("users/{id}")
    Call<UserResponse> getUser(@Path("id") String id);

    @PUT("users/{id}")
    Call<UserResponse> updateUser(@Path("id") String id, @Body RegisterRequest request);
}
