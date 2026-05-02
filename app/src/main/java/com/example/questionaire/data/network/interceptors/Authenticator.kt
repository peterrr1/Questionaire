package com.example.questionaire.data.network.interceptors

import android.util.Log
import com.example.questionaire.data.network.dto.LoginResponseDto
import com.example.questionaire.data.network.services.AuthApiService
import com.example.questionaire.utils.managers.TokenManager
import com.example.questionaire.utils.managers.TokenType
import dagger.Lazy
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import okio.IOException
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val tokenManager: TokenManager,
    private val authApiService: Lazy<AuthApiService>
): Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        Log.d("TOKEN_AUTH", "This is the token authenticator!")
        val refreshToken = runBlocking { tokenManager.getToken(TokenType.REFRESH_TOKEN).first() }
            ?: run {
                runBlocking { forceLogout() }
            }
        return try {
            val refreshResponse = runBlocking { authApiService.get().refresh("Bearer $refreshToken") }

            if (refreshResponse.success) {
                val data = refreshResponse.data ?: throw IOException("No data was sent.")
                Log.d("TOKEN_AUTH", "Request is successful.")
                runBlocking {
                    tokenManager.saveToken(data.accessToken, TokenType.ACCESS_TOKEN)
                    tokenManager.saveToken(data.refreshToken, TokenType.REFRESH_TOKEN)
                }
                response.request.newBuilder()
                    .header("Authorization", "Bearer ${data.accessToken}")
                    .build()
            } else {
                Log.d("TOKEN_AUTH", "Request is not successful.")
                runBlocking { forceLogout() }
                null
            }
        } catch (e: Exception) {
            Log.d("TOKEN_AUTH", "An exemption occurred.")
            runBlocking { forceLogout() }
            null
        }
    }

    private suspend fun forceLogout() {
        tokenManager.deleteToken(TokenType.ACCESS_TOKEN)
        tokenManager.deleteToken(TokenType.REFRESH_TOKEN)
    }
}