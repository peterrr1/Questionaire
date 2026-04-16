package com.example.questionaire.data.network.dto

import com.example.questionaire.model.CompactQuizInfo
import com.example.questionaire.model.DetailedQuizInfo
import com.example.questionaire.model.Option
import com.example.questionaire.model.PublicUserInfo
import com.example.questionaire.model.Question
import com.example.questionaire.utils.constants.AppConstants
import com.squareup.moshi.Json


data class CompactQuizInfoDto(
    @param:Json(name = "id") val id: String,
    @param:Json(name = "name") val name: String,
) {
    fun toDomain(): CompactQuizInfo {
        return CompactQuizInfo(
            id = id,
            name = name,
            displayImageUrl = "${AppConstants.BASE_URL}/files/${id}/image"
        )
    }
}

data class DetailedQuizInfoDto(
    @param:Json(name = "id") val id: String,
    @param:Json(name = "name") val name: String,
    @param:Json(name = "collection_id") val collectionId: String,
    @param:Json(name = "visibility") val visibility: String,
    @param:Json(name = "author") val author: PublicUserInfoDto,
    @param:Json(name = "question_categories") val questionCategories: List<String>
) {
    fun toDomain(): DetailedQuizInfo {
        return DetailedQuizInfo(
            id = id,
            name =  name,
            collectionId = collectionId,
            visibility = visibility,
            author = author.toDomain(),
            questionCategories = questionCategories
        )
    }
}

data class PublicUserInfoDto (
    @param:Json(name = "username") val username: String,
    @param:Json(name = "createdAt") val createdAt: String
) {
    fun toDomain(): PublicUserInfo {
        return PublicUserInfo(
            username = username,
            createdAt = createdAt
        )
    }
}


data class OptionDto(
    @param:Json(name = "_id") val id: String,
    @param:Json(name = "option") val text: String
) {
    fun toDomain(): Option {
        return Option(
            id = id,
            text = text
        )
    }
}

data class QuestionDto(
    @param:Json(name = "_id") val id: String,
    @param:Json(name = "type") val type: String,
    @param:Json(name = "category") val category: String,
    @param:Json(name = "question") val text: String,
    @param:Json(name = "correct_option") val correctOptionId: String,
    @param:Json(name = "options") val options: List<OptionDto>
) {
    fun toDomain() : Question {
        return Question(
            id = id,
            type = type,
            category = category,
            text = text,
            options = options.map {
                Option(
                    id = it.id,
                    text = it.text
                )
            },
            correctOptionId = correctOptionId
        )
    }
}


data class LoginRequestDto(
    @field:Json(name = "email") val email: String,
    @field:Json(name = "password") val password: String
)

data class UserDto(
    @param:Json(name = "username") val username: String
)
data class LoginResponseDto(
    @param:Json(name = "accessToken") val accessToken: String,
    @param:Json(name = "refreshToken") val refreshToken: String,
)