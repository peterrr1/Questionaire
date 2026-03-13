package com.example.questionaire.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.questionaire.data.local.db.entities.Question

import com.example.questionaire.model.QuestionCategory
import com.example.questionaire.utils.Result
import kotlinx.coroutines.flow.Flow


@Dao
interface QuizQuestionDao {

    @Insert
    suspend fun insertQuestions(question: List<Question>)

    @Query("SELECT COUNT(*) FROM questions")
    suspend fun getCount(): Int

    @Query("SELECT DISTINCT category FROM questions")
    suspend fun getCategories(): List<QuestionCategory>

    @Query("SELECT * FROM questions")
    suspend fun getAllQuestions(): List<Question>

    @Query("SELECT * FROM questions WHERE category = :category")
    suspend fun getQuestionCategory(category: QuestionCategory): List<Question>
}