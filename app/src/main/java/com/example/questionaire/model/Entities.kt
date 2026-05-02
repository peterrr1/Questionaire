package com.example.questionaire.model

import com.example.questionaire.data.network.dto.OptionDraftDto
import com.example.questionaire.data.network.dto.QuestionDraftDto
import com.example.questionaire.data.network.dto.QuizDraftDto
import com.example.questionaire.data.network.dto.QuizInformationDraftDto

enum class QuestionType(val label: String) {
    SINGLE_OPTION("SINGLE_OPTION"),
    MULTI_OPTION("MULTI_OPTION")
}


data class Question(
    val id: String,
    val type: String,
    val category: String,
    val categoryDisplayName: String,
    val text: String,
    val options: List<Option>,
    val correctOptionId: String
)

data class Option(
    val id: String,
    val text: String
)

/*
    These two data classes are used to create question/quiz drafts
    at the creation screen
    Other values (e.g. id, author, ...) are added at the server side
*/
data class QuizDraft(
    val quizInformation: QuizInformationDraft = QuizInformationDraft(),
    val questions: List<QuestionDraft> = emptyList()
) {
    fun toDto(): QuizDraftDto {
        return QuizDraftDto(
            quizInformation.toDto(),
            questions.map { it.toDto() }
        )
    }
}

data class QuizInformationDraft(
    val name: String = "",
    val visibility: String = "Public",
    val questionCategories: List<String> = emptyList()
) {
    fun toDto(): QuizInformationDraftDto {
        return QuizInformationDraftDto(
            name = name,
            visibility = visibility,
            questionCategories = questionCategories
        )
    }
}

data class QuestionDraft(
    val type: String = "",
    val category: String = "",
    val text: String = "",
    val options: List<OptionDraft> = emptyList(),
    val correctOptionId: Int = -1
) {
    fun toDto(): QuestionDraftDto {
        return QuestionDraftDto(
            type = type,
            category = category,
            text = text,
            options = options.map { it.toDto() },
            correctOptionId = correctOptionId
        )
    }
}

data class OptionDraft(
    val text: String = ""
) {
    fun toDto(): OptionDraftDto {
        return OptionDraftDto(
            option = text
        )
    }
}
// ================================

data class PublicUserInfo (
    val username: String = "USERNAME",
    val createdAt: String = "DATE_CREATION"
)

data class DetailedQuizInfo(
    val id: String = "ID",
    val name: String = "NAME",
    val collectionId: String = "COLLECTION_ID",
    val visibility: String = "PUBLIC",
    val displayImageUrl: String = "",
    val author: PublicUserInfo = PublicUserInfo(),
    val questionCategories: List<String> = emptyList(),
    val categoriesDisplayName: List<String> = emptyList()
)

data class CompactQuizInfo(
    val id: String,
    val name: String,
    val displayImageUrl: String
)




