package com.example.questionaire.utils

import androidx.annotation.StringRes

data class ErrorMessage(
    val id: Long = System.nanoTime(),
    val messageId: String
)