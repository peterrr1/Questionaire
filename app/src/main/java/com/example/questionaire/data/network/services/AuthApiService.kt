package com.example.questionaire.data.network.services

import com.example.questionaire.data.network.dto.ApiResponse
import com.example.questionaire.data.network.dto.LoginRequestDto
import com.example.questionaire.data.network.dto.LoginResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST


interface AuthApiService {

    @POST("auth/login")
    suspend fun login(@Body credentials: LoginRequestDto): ApiResponse<LoginResponseDto>

    @GET("auth/refresh")
    suspend fun refresh(@Header("Authorization") refreshToken: String): ApiResponse<LoginResponseDto>
}




