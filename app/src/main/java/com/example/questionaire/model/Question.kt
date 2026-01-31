package com.example.questionaire.model


enum class QuestionCategory {
    LAW_AND_ADMINISTRATION,
    HUNTING_ZOOLOGY,
    HUNTING_PRACTICES
}

data class Question(
    val id: String,
    val category: QuestionCategory,
    val text: String,
    val options: List<Option>,
    val correctOptionId: String
)


data class Option(
    val id: String,
    val text: String
)




