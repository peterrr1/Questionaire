package com.example.questionaire.data.network.dto

data class ApiResponse<T> (
    val success: Boolean,
    val statusCode: Int,
    val data: T? = null,
    val timestamp: String
)