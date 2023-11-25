package com.example.quests.data.source.network

import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.onSuccess
import com.skydoves.sandwich.retrofit.apiMessage
import com.skydoves.sandwich.retrofit.errorBody
import com.skydoves.sandwich.retrofit.serialization.deserializeErrorBody
import com.skydoves.sandwich.retrofit.serialization.onErrorDeserialize
import kotlinx.serialization.json.Json
import javax.inject.Inject

class AuthNetworkDataSource @Inject constructor(
    private val apiService: ApiService
) : AuthDataSource {

    override suspend fun login(username: String, password: String) {
        val response: ApiResponse<Response> = apiService.login(User(username, password))
        println(response)
        response.onSuccess {
            println("on success")
            println(data)
            println(data?.accessToken)
            println(data?.refreshToken)

        }.onError {
            println("on error")
            println(this)
            println(this.payload)
            println(this.errorBody)
            println(this.apiMessage)
            var s: String = ""
            if (this.apiMessage != null) {
                s = this.apiMessage!!
            }
            println("testetetstestestt")
            val t = errorBody?.string()
            println(t == null)
            println(t != null)
            if (t != null) {
                println("after condition")
                val r: ErrorMessage = Json.decodeFromString(t)
                println("in here")
                println(r)
                println(r == null)
            }
            println(Json.decodeFromString<Response>(s))
            println(this.apiMessage?.let { Json.decodeFromString<Response>(it) })
        }.onException {
            println("on exception")
            println(this)
        }

    }
}