package com.example.questionaire.data.repositories

import com.example.questionaire.data.local.dao.QuizAttemptDao
import com.example.questionaire.data.local.entities.QuizAttemptEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class QuizAttemptRepository @Inject constructor(
    private val quizAttemptDao: QuizAttemptDao
) {
    fun getAllAttempts(): Flow<List<QuizAttemptEntity>> = quizAttemptDao.getAllAttempt()
    suspend fun insertAttempt(attempt: QuizAttemptEntity) = quizAttemptDao.insertAttempt(attempt)
    suspend fun deleteAttempt(attemptId: Int) = quizAttemptDao.deleteAttempt(attemptId)
}