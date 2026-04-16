package com.example.questionaire.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.questionaire.data.local.converter.QuizConverters
import com.example.questionaire.data.local.dao.QuizAttemptDao
import com.example.questionaire.data.local.entities.QuizAttemptEntity


@Database(
    version = 1,
    entities = [
        QuizAttemptEntity::class
    ]
)
@TypeConverters(QuizConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun quizAttemptDao(): QuizAttemptDao
}