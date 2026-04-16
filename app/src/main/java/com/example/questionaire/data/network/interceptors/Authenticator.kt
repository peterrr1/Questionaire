package com.example.questionaire.data.network.interceptors

import com.example.questionaire.data.network.dto.LoginResponseDto
import com.example.questionaire.data.network.services.AuthApiService
import com.example.questionaire.utils.managers.TokenManager
import dagger.Lazy
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val tokenManager: TokenManager,
    private val authApiService: Lazy<AuthApiService>
): Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        val refreshToken = runBlocking { tokenManager.getToken("REFRESH").first() }
            ?: return null
        return try {
            val refreshResponse = runBlocking { authApiService.get().refresh("Bearer $refreshToken") }

            if (refreshResponse.success) {
                val data = refreshResponse.data
                runBlocking {
                    tokenManager.saveToken(data.accessToken, "ACCESS")
                    tokenManager.saveToken(data.refreshToken, "REFRESH")
                }
                response.request.newBuilder()
                    .addHeader("Authorization", "Bearer ${data.accessToken}")
                    .build()
            } else {
                runBlocking {
                    tokenManager.deleteToken("ACCESS")
                    tokenManager.deleteToken("REFRESH")
                }
                null
            }
        } catch (e: Exception) {
            runBlocking {
                tokenManager.deleteToken("ACCESS")
                tokenManager.deleteToken("REFRESH")
            }
            null
        }
    }
}