package com.example.questionaire.di.repositories

import com.example.questionaire.data.local.db.dao.QuizAttemptDao
import com.example.questionaire.data.local.db.entities.QuizAttempt
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class QuizAttemptRepository @Inject constructor(
    private val quizAttemptDao: QuizAttemptDao
) {
    fun getAllAttempts(): Flow<List<QuizAttempt>> = quizAttemptDao.getAllAttempt()

    suspend fun insertAttempt(attempt: QuizAttempt) = quizAttemptDao.insertAttempt(attempt)

    suspend fun deleteAttempt(attemptId: Int) = quizAttemptDao.deleteAttempt(attemptId)
}