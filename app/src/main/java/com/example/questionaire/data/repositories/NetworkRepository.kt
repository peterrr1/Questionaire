package com.example.questionaire.data.repositories

import android.net.http.NetworkException
import android.os.Build
import androidx.annotation.RequiresExtension
import com.example.questionaire.data.network.dto.QuizDraftDto
import com.example.questionaire.model.Question
import com.example.questionaire.data.network.services.QuizApiService
import com.example.questionaire.model.CompactQuizInfo
import com.example.questionaire.model.DetailedQuizInfo
import com.example.questionaire.model.QuizDraft
import com.example.questionaire.utils.Result
import com.example.questionaire.utils.safeApiCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.IOException
import javax.inject.Inject

class NetworkRepository @Inject constructor(
    private val quizApiService: QuizApiService
)  {

    suspend fun createQuiz(quizDraft: QuizDraft): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {

                val response = quizApiService.createQuiz(quizDraft.toDto())
                if (response.success) {
                    Result.Success(data = Unit)
                }
                else {
                    Result.Error(exception = IOException("An error occurred during quiz creation."))
                }
            } catch (e: Exception) {
                Result.Error(exception = IOException("An error occurred during quiz creation: ${e.message}"))
            }
        }
    }

    suspend fun deleteQuiz(quizId: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {

                val response = quizApiService.deleteQuiz(quizId)
                if (response.success) {
                    Result.Success(data = Unit)
                }
                else {
                    Result.Error(exception = IOException("An error occurred during quiz deletion."))
                }
            } catch (e: Exception) {
                Result.Error(exception = IOException("An error occurred during quiz deletion: ${e.message}"))
            }
        }
    }

    suspend fun getQuestions(collectionId: String, category: String?): Result<List<Question>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = quizApiService.getQuestions(
                    collectionId = collectionId,
                    category = category)
                val data = response.data ?: throw IOException("Data field was not specified in the api response object.")
                val result = data.map { it.toDomain() }

                Result.Success(data = result)
            } catch (e: IOException) {
                Result.Error(exception = IOException("Couldn't load question from network: $e"))
            }
        }
    }


    suspend fun getQuizTypes(): Result<List<CompactQuizInfo>> {
        return withContext(Dispatchers.IO) {

            safeApiCall {
                val response = quizApiService.getQuizTypes()
                val data = response.data ?: throw IOException("Data field was not specified in the api response object.")
                data.map { it.toDomain() } }
        }
    }

    suspend fun getQuizInfo(quizId: String): Result<DetailedQuizInfo> {
        return withContext(Dispatchers.IO) {
            try {
                // Get data then convert the data transfer object to domain object
                val response = quizApiService.getQuizInfo(quizId)
                val data = response.data ?: throw IOException("Data field was not specified in the api response object.")
                val result = data.toDomain()

                Result.Success(data = result)
            } catch (e: IOException) {
                Result.Error(exception = IOException("Couldn't load quiz information from the API:", e))
            }
        }
    }
}