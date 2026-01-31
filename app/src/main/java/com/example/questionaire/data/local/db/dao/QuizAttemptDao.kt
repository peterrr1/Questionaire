package com.example.questionaire.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.questionaire.data.local.db.entities.QuizAttempt
import kotlinx.coroutines.flow.Flow


@Dao
interface QuizAttemptDao {

    @Insert
    suspend fun insertAttempt(attempt: QuizAttempt)

    @Query("SELECT * FROM quiz_attempt")
    fun getAllAttempt(): Flow<List<QuizAttempt>>

    @Query("DELETE FROM quiz_attempt WHERE attemptId = :attemptId" )
    suspend fun deleteAttempt(attemptId: Int)

}