package com.example.questionaire.model

import com.example.questionaire.utils.ErrorMessage
import java.util.Date


data class Question(
    val id: String,
    val type: String,
    val category: String,
    val text: String,
    val options: List<Option>,
    val correctOptionId: String
)

data class Option(
    val id: String,
    val text: String
)


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
    val questionCategories: List<String> = emptyList()
)

data class CompactQuizInfo(
    val id: String,
    val name: String,
    val displayImageUrl: String
)




