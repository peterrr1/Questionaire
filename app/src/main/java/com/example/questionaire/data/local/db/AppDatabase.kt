package com.example.questionaire.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.questionaire.data.local.db.converter.QuizConverters
import com.example.questionaire.data.local.db.dao.QuizAttemptDao
import com.example.questionaire.data.local.db.dao.QuizQuestionDao
import com.example.questionaire.data.local.db.entities.Question
import com.example.questionaire.data.local.db.entities.QuizAttempt


@Database(
    version = 1,
    entities = [
        QuizAttempt::class,
        Question::class
    ]
)
@TypeConverters(QuizConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun quizAttemptDao(): QuizAttemptDao
    abstract fun questionsDao(): QuizQuestionDao
}