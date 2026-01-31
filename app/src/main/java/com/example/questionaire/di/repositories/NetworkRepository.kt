package com.example.questionaire.di.repositories

import com.example.questionaire.model.Question
import com.example.questionaire.network.QuizApiService
import com.example.questionaire.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.IOException
import javax.inject.Inject

class NetworkRepository @Inject constructor(
    private val quizApiService: QuizApiService
)  {
    suspend fun getAllQuestion(category: String): Result<List<Question>> {
        return withContext(Dispatchers.IO) {
            try {
                val result = quizApiService.getAllQuestion(category).map { it.toDomain() }
                Result.Success(data = result)
            } catch (e: IOException) {
                Result.Error(exception = IOException("Couldn't load question from network: $e"))
            }
        }
    }
}