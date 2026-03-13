package com.example.questionaire.model

import androidx.annotation.StringRes
import androidx.compose.ui.res.stringResource
import com.example.questionaire.R
import okhttp3.Route


data class QuestionCategory(
    val displayName: String,
    val routeParam: String
) {
    val name: String get() = routeParam

    companion object {
        private val accentMap = mapOf(
            'á' to 'a', 'é' to 'e', 'í' to 'i', 'ó' to 'o', 'ö' to 'o',
            'ő' to 'o', 'ú' to 'u', 'ü' to 'u', 'ű' to 'u',
            'Á' to 'A', 'É' to 'E', 'Í' to 'I', 'Ó' to 'O', 'Ö' to 'O',
            'Ő' to 'O', 'Ú' to 'U', 'Ü' to 'U', 'Ű' to 'U'
        )
        fun fromDbValue(dbValue: String): QuestionCategory {

            val routeParam = dbValue.map { accentMap[it] ?: it }.joinToString("")

            val displayName = dbValue
                .replace("_", " ")
                .lowercase()
                .replaceFirstChar { it.uppercase() }

            return QuestionCategory(
                displayName = displayName,
                routeParam = routeParam
            )
        }
        fun fromRouteParam(routeParam: String): QuestionCategory {
            // routeParam is already normalized (no accents, has underscores)
            // so we just reconstruct the display name from it
            val displayName = routeParam
                .replace('_', ' ')
                .lowercase()
                .replaceFirstChar { it.uppercase() }

            return QuestionCategory(
                displayName = displayName,
                routeParam = routeParam
            )
        }

    }
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




