package com.example.questionaire.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.questionaire.model.Option
import com.squareup.moshi.JsonClass
import java.util.Date

//data class QuizAttemptSnapshot(
//    val attemptId: Int,
//    val quizCategory: String,
//    val startTime: Date,
//    var endTime: Date?,
//    var result: List<QuizResultSnapshot>,
//    var score: Int
//)

@Entity(tableName = "quiz_attempt")
data class QuizAttemptEntity(
    @PrimaryKey(autoGenerate = true) val attemptId: Int = 0,
    val quizCategory: String,
    val startTime: Date,
    var endTime: Date?, // Can't be null, it's only saved if the quiz has finished
    var result: List<QuizResultEntity> = emptyList(),
    var score: Int = 0
)

@JsonClass(generateAdapter = true)
data class QuizResultEntity(
    val questionId: String,
    val questionText: String,
    val correctOptionId: String,
    val chosenOptionId: String?,
    val optionSnapshots: List<OptionEntity>,
    val questionOrder: Int
)

@JsonClass(generateAdapter = true)
data class OptionEntity(
    val id: String,
    val text: String,
)