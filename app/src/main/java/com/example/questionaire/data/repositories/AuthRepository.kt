package com.example.questionaire.data.repositories

import android.util.Log
import com.example.questionaire.data.network.dto.LoginRequestDto
import com.example.questionaire.data.network.services.AuthApiService
import com.example.questionaire.utils.Result
import com.example.questionaire.utils.managers.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authApiService: AuthApiService,
    private val tokenManager: TokenManager,
) {
    suspend fun login(email: String, password: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                println("LOGIN")
                val response = authApiService.login(LoginRequestDto(email, password))
                if (response.success) {
                    val data = response.data
                    println("RESPONSE TOKEN: ${data.refreshToken}")
                    val token = tokenManager.getToken("ACCESS")
                    Log.d("AUTH", "Emitted token: $token")
                    println("$token")
                    tokenManager.saveToken(data.accessToken, "ACCESS")
                    tokenManager.saveToken(data.refreshToken, "REFRESH")
                    Result.Success(Unit)
                } else {
                    Result.Error(okio.IOException("Login failed: ${response.statusCode}"))
                }
            } catch (error: Exception) {
                Result.Error(error)
            }
        }
    }
}
