package com.example.questionaire.data.repositories

import com.example.questionaire.model.Question
import com.example.questionaire.data.network.services.QuizApiService
import com.example.questionaire.model.CompactQuizInfo
import com.example.questionaire.model.DetailedQuizInfo
import com.example.questionaire.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.IOException
import javax.inject.Inject

class NetworkRepository @Inject constructor(
    private val quizApiService: QuizApiService
)  {
    suspend fun getQuestions(quizId: String, category: String): Result<List<Question>> {
        return withContext(Dispatchers.IO) {
            try {
                val result = quizApiService.getQuestions(
                    quizId = quizId,
                    category = category).data.map { it.toDomain() }
                Result.Success(data = result)
            } catch (e: IOException) {
                Result.Error(exception = IOException("Couldn't load question from network: $e"))
            }
        }
    }

    suspend fun getQuizTypes(): Result<List<CompactQuizInfo>> {
        return withContext(Dispatchers.IO) {
            try {
                val result = quizApiService.getQuizTypes().data.map { it.toDomain() }
                Result.Success(data = result)
            } catch (e: IOException) {
                Result.Error(exception = IOException("Couldn't load quiz types from the API:", e))
            }
        }
    }

    suspend fun getQuizInfo(quizId: String): Result<DetailedQuizInfo> {
        return withContext(Dispatchers.IO) {
            try {
                // Get data then convert the data transfer object to domain object
                val result = quizApiService.getQuizInfo(quizId).data.toDomain()
                Result.Success(data = result)
            } catch (e: IOException) {
                Result.Error(exception = IOException("Couldn't load quiz information from the API:", e))
            }
        }
    }
}