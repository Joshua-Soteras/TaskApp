package com.example.quests.data.source.network

import com.skydoves.sandwich.ApiResponse
import retrofit2.http.Body
import retrofit2.http.POST


/**
 * Retrofit service object
 */
interface ApiService {

    @POST("auth/login")
    suspend fun login(@Body user: User): ApiResponse<Response>
}