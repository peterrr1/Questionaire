package com.example.questionaire.network

import com.example.questionaire.network.entities.QuestionDto
import retrofit2.http.GET
import retrofit2.http.Query



interface QuizApiService {
    @GET(value = "questions")
    suspend fun getAllQuestion(@Query("category") category: String): List<QuestionDto>
}


