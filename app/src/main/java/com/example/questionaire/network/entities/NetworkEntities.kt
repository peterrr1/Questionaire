package com.example.questionaire.network.entities

import com.example.questionaire.model.Option
import com.example.questionaire.model.Question
import com.example.questionaire.model.QuestionCategory
import com.squareup.moshi.Json

data class MongoID(
    @param:Json(name = $$"$oid")
    val oid: String
)

data class OptionDto(
    @param:Json(name = "_id") val id: MongoID,
    @param:Json(name = "option") val text: String
)

data class QuestionDto(
    @param:Json(name = "_id") val id: MongoID,
    @param:Json(name = "category") val category: String,
    @param:Json(name = "question") val text: String,
    @param:Json(name = "correct_option") val correctOptionId: MongoID,
    @param:Json(name = "options") val options: List<OptionDto>
) {
    fun toDomain() : Question {
        return Question(
            id = id.oid,
            category = stringToCategory(category),
            text = text,
            options = options.map {
                Option(
                    id = it.id.oid,
                    text = it.text
                )
            },
            correctOptionId = correctOptionId.oid
        )
    }
}

private fun stringToCategory(quizType: String): QuestionCategory {
    return when (quizType) {
        "hunting_zoology" -> QuestionCategory.HUNTING_ZOOLOGY
        "law_and_administration" -> QuestionCategory.LAW_AND_ADMINISTRATION
        "hunting_practices" -> QuestionCategory.HUNTING_PRACTICES
        else -> QuestionCategory.HUNTING_PRACTICES
    }
}