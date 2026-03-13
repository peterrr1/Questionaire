package com.example.questionaire.di.repositories

import com.example.questionaire.data.local.db.dao.QuizQuestionDao
import com.example.questionaire.data.local.db.entities.Question
import com.example.questionaire.model.QuestionCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class QuizQuestionRepository @Inject constructor(
    private val questionDao: QuizQuestionDao
) {
    suspend fun getAllQuestions(): List<Question> {
        return withContext(Dispatchers.IO) {
            questionDao.getAllQuestions()
        }
    }
    suspend fun getCategories(): List<QuestionCategory> {
        return withContext(Dispatchers.IO) {
            questionDao.getCategories()
        }
    }
}