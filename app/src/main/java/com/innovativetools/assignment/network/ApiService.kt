package com.innovativetools.assignment.network

import com.innovativetools.assignment.model.ApiResponse
import com.innovativetools.assignment.model.RequestBody
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object ApiService {
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(ApiEndpoints.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    private val apiService: ApiServiceInterface by lazy {
        retrofit.create(ApiServiceInterface::class.java)
    }

    suspend fun getDashboardData(accessToken: String): ApiResponse? {
        return try {
            apiService.getDashboardData(accessToken)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun postData(apiUrl: String, requestBody: RequestBody,accessToken: String): ApiResponse? {
        return try {
            apiService.postData(apiUrl, requestBody,accessToken)
        } catch (e: Exception) {
            null
        }
    }

}
