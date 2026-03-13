package com.example.questionaire.data.local.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.questionaire.model.QuestionCategory
import com.squareup.moshi.JsonClass

@Entity(tableName = "questions")
data class Question(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val category: QuestionCategory,
    val text: String,
    val options: List<Option>,
    val correctOptionId: String
)

@JsonClass(generateAdapter = true)
data class Option(
    val id: String,
    val text: String
)
