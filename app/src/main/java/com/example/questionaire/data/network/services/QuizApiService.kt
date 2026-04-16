package com.example.questionaire.data.network.services


import com.example.questionaire.data.network.dto.ApiResponse
import com.example.questionaire.data.network.dto.CompactQuizInfoDto
import com.example.questionaire.data.network.dto.DetailedQuizInfoDto
import com.example.questionaire.data.network.dto.QuestionDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface QuizApiService {
    @GET(value = "quiz/list")
    suspend fun getQuizTypes(): ApiResponse<List<CompactQuizInfoDto>>

    @GET(value = "quiz/info/{quiz_id}")
    suspend fun getQuizInfo(@Path("quiz_id") quizId: String): ApiResponse<DetailedQuizInfoDto>

    @GET(value = "quiz/questions/{quiz_id}")
    suspend fun getQuestions(
        @Path("quiz_id") quizId: String,
        @Query("category") category: String
    ) : ApiResponse<List<QuestionDto>>
}