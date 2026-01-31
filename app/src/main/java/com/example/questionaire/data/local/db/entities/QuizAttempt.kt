package com.example.questionaire.data.local.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.questionaire.model.QuestionCategory
import com.squareup.moshi.JsonClass
import java.util.Date

@Entity(tableName = "quiz_attempt")
data class QuizAttempt(
    @PrimaryKey(autoGenerate = true) val attemptId: Int = 0,
    val quizType: QuestionCategory,
    val startTime: Date,
    var endTime: Date?, // Can't be null, it's only saved if the quiz has finished
    var result: List<QuizResultSnapshot> = emptyList()
)

@JsonClass(generateAdapter = true)
data class QuizResultSnapshot(
    val questionId: String,
    val questionText: String,
    val correctOptionId: String,
    val chosenOptionId: String?,
    val optionSnapshots: List<OptionSnapshot>,
    val questionOrder: Int
)

@JsonClass(generateAdapter = true)
data class OptionSnapshot(
    val id: String,
    val text: String,
)