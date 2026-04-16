package com.example.questionaire.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.questionaire.data.local.entities.QuizAttemptEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface QuizAttemptDao {

    @Insert
    suspend fun insertAttempt(attempt: QuizAttemptEntity)

    @Query("SELECT * FROM quiz_attempt")
    fun getAllAttempt(): Flow<List<QuizAttemptEntity>>

    @Query("DELETE FROM quiz_attempt WHERE attemptId = :attemptId" )
    suspend fun deleteAttempt(attemptId: Int)

}