package com.example.questionaire.data.network.dto

import com.example.questionaire.model.CompactQuizInfo
import com.example.questionaire.model.DetailedQuizInfo
import com.example.questionaire.model.Option
import com.example.questionaire.model.PublicUserInfo
import com.example.questionaire.model.Question
import com.example.questionaire.utils.constants.AppConstants
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


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
    @param:Json(name = "question_categories") val questionCategories: List<String>,
    @param:Json(name = "categories_display_name") val categoriesDisplayName: List<String>
) {
    fun toDomain(): DetailedQuizInfo {
        return DetailedQuizInfo(
            id = id,
            name =  name,
            collectionId = collectionId,
            visibility = visibility,
            author = author.toDomain(),
            questionCategories = questionCategories,
            categoriesDisplayName = categoriesDisplayName
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
    @param:Json(name = "category_display_name") val categoryDisplayName: String,
    @param:Json(name = "question") val text: String,
    @param:Json(name = "correct_option") val correctOptionId: String,
    @param:Json(name = "options") val options: List<OptionDto>
) {
    fun toDomain() : Question {
        return Question(
            id = id,
            type = type,
            category = category,
            categoryDisplayName = categoryDisplayName,
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

@JsonClass(generateAdapter = true)
data class QuizDraftDto(
    @Json(name = "quiz_information") val quizName: QuizInformationDraftDto,
    @Json(name = "questions") val questions: List<QuestionDraftDto>
)

@JsonClass(generateAdapter = true)
data class QuizInformationDraftDto(
    @Json(name = "quiz_name") val name: String,
    @Json(name = "visibility") val visibility: String,
    @Json(name = "question_categories") val questionCategories: List<String>
)
@JsonClass(generateAdapter = true)
data class QuestionDraftDto(
    @Json(name = "type") val type: String,
    @Json(name = "category") val category: String,
    @Json(name = "question") val text: String,
    @Json(name = "options") val options: List<OptionDraftDto>,
    @Json(name = "correct_option") val correctOptionId: Int,
)

@JsonClass(generateAdapter = true)
data class OptionDraftDto(
    @Json(name = "option") val option: String
)

// "data":[
// {"_id":"69f5b91ec746de9fea916320",
// "type":"SINGLE_OPTION",
// "category":"NONE",
// "question":"dasasd",
// "correct_option":"1",
// "options":[{"option":"adsadasd"}]}],
// "timestamp":"2026-05-02T08:43:10.280Z"}

//"data":[
// {"_id":"69f5b9c5c746de9fea91632a",
// "type":"SINGLE_OPTION",
// "category":"JOGI_ÉS_IGAZGATASI_KERDESEK",
// "question":"Kié hazánkban a vad tulajdonjoga?",
// "correct_option":"69f5ad3a5a3c5e886c3d88b6",
// "options":[{"_id":"69f5ad3a5a3c5e886c3d88b4",
// "option":"Vadászatra jogosult"},{"_id":"69f5ad3a5a3c5e886c3d88b5",
// "option":"Földtulajdonhoz kötött"},{"_id":"69f5ad3a5a3c5e886c3d88b6",
// "option":"Állam"}]},{"_id":"69f5b9c5c746de9fea91632b",
// "type":"SINGLE_OPTION","category":"JOGI_ÉS_IGAZGATASI_KERDESEK",
// "question":"Milyen formában gyakorolható a vadászati jog?","correct_option":"69f5ad3a5a3c5e886c3d88bc","options":[{"_id":"69f5ad3a5a3c5e886c3d88ba","option":"Önálló vadászati jog"},{"_id":"69f5ad3a5a3c5e886c3d88bb","option":"Társult vadászati jog"},{"_id":"69f5ad3a5a3c5e886c3d88bc","option":"Mindkettő"}]},{"_id":"69f5b9c5c746de9fea91632c","type":"SINGLE_OPTION","category":"JOGI_ÉS_IGAZGATASI_KERDESEK","question":"Mekkora hazánkban a vadászterület minimális nagysága?","correct_option":"69f5ad3a5a3c5e886c3d88be","options":[{"_id":"69f5ad3a5a3c5e886c3d88bd","option":"300 ha"},{"_id":"69f5ad3a5a3c5e886c3d88be","option":"3000 ha"},{"_id":"69f5ad3a5a3c5e886c3d88bf","option":"1000 ha"}]},{"_id":"69f5b9c5c746de9fea91632d","type":"SINGLE_OPTION","category":"JOGI_ÉS_IGAZGATASI_KERDESEK","question":"Mely szervezet az elsőfokú vadászati hatóság hazánkban?","correct_option":"69f5ad3a5a3c5e886c3d88c0","options":[{"_id":"69f5ad3a5a3c5e886c3d88c0","option":"Vármegyei vadászati hatóság"},{"_id":"69f5ad3a5a3c5e886c3d88c1","option":"Vármegyei vadászkamara"},{"_id":"69f5ad3a5a3c5e886c3d88c2","option":"Vármegyei vadászszövetség"}]}],"timestamp":"2026-05-02T08:45:57.894Z"}

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