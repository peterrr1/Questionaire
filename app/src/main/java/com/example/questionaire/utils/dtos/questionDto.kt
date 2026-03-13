package com.example.questionaire.utils.dtos

import com.example.questionaire.data.local.db.entities.Option
import com.example.questionaire.data.local.db.entities.Question
import com.example.questionaire.model.QuestionCategory

data class OptionDTO(
    val id: String,
    val text: String
) {
    fun toEntity(): Option {
        return Option(
            id = id,
            text = text
        )
    }
}

data class QuestionDTO(
    val category: String,
    val text: String,
    val options: List<OptionDTO>,
    val answer: String
) {
    fun toEntity() : Question {
        return Question(
            category = QuestionCategory.fromDbValue(category),
            text = text,
            options = options.map { it.toEntity() },
            correctOptionId = answer
        )
    }
}