package com.example.questionaire.data.network.services


import com.example.questionaire.data.network.dto.ApiResponse
import com.example.questionaire.data.network.dto.CompactQuizInfoDto
import com.example.questionaire.data.network.dto.DetailedQuizInfoDto
import com.example.questionaire.data.network.dto.QuestionDto
import com.example.questionaire.data.network.dto.QuizDraftDto
import com.example.questionaire.model.QuizDraft
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface QuizApiService {
    @GET(value = "quiz/list")
    suspend fun getQuizTypes(): ApiResponse<List<CompactQuizInfoDto>>

    @GET(value = "quiz/info/{quiz_id}")
    suspend fun getQuizInfo(@Path("quiz_id") quizId: String): ApiResponse<DetailedQuizInfoDto>

    @GET(value = "quiz/questions/{collection_id}")
    suspend fun getQuestions(
        @Path("collection_id") collectionId: String,
        @Query("category") category: String? = null
    ) : ApiResponse<List<QuestionDto>>

    @POST(value = "quiz/create")
    suspend fun createQuiz(@Body quizDraft: QuizDraftDto): ApiResponse<Any?>

    @DELETE(value = "quiz/delete/{quiz_id}")
    suspend fun deleteQuiz(@Path("quiz_id") quizId: String): ApiResponse<Any?>
}