package com.example.quests.data.source.network

import com.example.quests.data.source.network.model.QuestsRequest
import com.example.quests.data.source.network.model.QuestsResponse
import com.example.quests.data.source.network.model.User
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * Retrofit service object
 */
interface ApiService {

    @POST("auth/login")
    suspend fun login(@Body user: User): ApiResponse<QuestsResponse>

    @POST("auth/register")
    suspend fun register(@Body user: User): ApiResponse<Void>

    @POST("auth/refresh")
    suspend fun refresh(@Header("Authorization") bearerAuth: String): ApiResponse<QuestsResponse>

    @POST("api/v1/users/data")
    suspend fun saveData(
        @Header("Authorization") bearerAuth: String,
        @Body data: QuestsRequest
    ): ApiResponse<QuestsResponse>

    @GET("api/v1/users/data")
    suspend fun getData(@Header("Authorization") bearerAuth: String): ApiResponse<QuestsResponse>
}