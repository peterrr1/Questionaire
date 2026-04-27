package com.example.questionaire.data.network.interceptors

import com.example.questionaire.data.network.services.AuthApiService
import com.example.questionaire.utils.managers.TokenManager
import com.example.questionaire.utils.managers.TokenType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request()

        if (request.url.encodedPath.contains("login") ||
            request.url.encodedPath.contains("register") ||
            request.url.encodedPath.contains("refresh")) {
            return chain.proceed(request)
        }

        val accessToken = runBlocking {
            tokenManager.getToken(TokenType.ACCESS_TOKEN).first()
        }

        val authenticatedRequest = request.newBuilder()
            .apply { if (accessToken != null) header("Authorization", "Bearer $accessToken")}
            .build()

        return chain.proceed(authenticatedRequest)
    }
}
