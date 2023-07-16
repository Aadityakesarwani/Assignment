package com.innovativetools.assignment.network

import com.innovativetools.assignment.model.ApiResponse
import com.innovativetools.assignment.model.RequestBody
import retrofit2.http.*

interface ApiServiceInterface {
    @GET(ApiEndpoints.DASHBOARD)
    suspend fun getDashboardData(@Header("Authorization") accessToken: String): ApiResponse

    @POST
    suspend fun postData(@Url url: String, @Body requestBody: RequestBody,@Header("Authorization") accessToken: String): ApiResponse
}