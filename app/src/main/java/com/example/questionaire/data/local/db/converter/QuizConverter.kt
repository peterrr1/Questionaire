package com.example.questionaire.data.local.db.converter

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.example.questionaire.data.local.db.entities.OptionSnapshot
import com.example.questionaire.data.local.db.entities.QuizResultSnapshot
import com.example.questionaire.model.QuestionCategory
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.util.Date

@ProvidedTypeConverter
class QuizConverters (
    private val moshi: Moshi
) {

    @TypeConverter
    fun optionsSnapshotToJson(options: List<OptionSnapshot>): String {
        return moshi.adapter<List<OptionSnapshot>>(
            Types.newParameterizedType(List::class.java, OptionSnapshot::class.java)
        ).toJson(options)
    }

    @TypeConverter
    fun jsonToOptionsSnapshot(json: String): List<OptionSnapshot> {
        return moshi.adapter<List<OptionSnapshot>>(
            Types.newParameterizedType(List::class.java, OptionSnapshot::class.java)
        ).fromJson(json) ?: emptyList()
    }

    @TypeConverter
    fun quizResultSnapshotToJson(results: List<QuizResultSnapshot>): String {
        return moshi.adapter<List<QuizResultSnapshot>>(
            Types.newParameterizedType(List::class.java, QuizResultSnapshot::class.java)
        ).toJson(results)
    }

    @TypeConverter
    fun jsonToQuizResultSnapshot(json: String): List<QuizResultSnapshot> {
        return moshi.adapter<List<QuizResultSnapshot>>(
            Types.newParameterizedType(List::class.java, QuizResultSnapshot::class.java)
        ).fromJson(json) ?: emptyList()
    }

    @TypeConverter
    fun fromTimeStamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun stringToQuestionCategory(value: String?): QuestionCategory {
        return when (value) {
            "hunting_zoology" -> QuestionCategory.HUNTING_ZOOLOGY
            "law_and_administration" -> QuestionCategory.LAW_AND_ADMINISTRATION
            "hunting_practices" -> QuestionCategory.HUNTING_PRACTICES
            else -> QuestionCategory.HUNTING_PRACTICES
        } ?: QuestionCategory.HUNTING_PRACTICES
    }

    @TypeConverter
    fun questionCategoryToString(category: QuestionCategory?): String {
        return when (category) {
            QuestionCategory.HUNTING_ZOOLOGY -> "hunting_zoology"
            QuestionCategory.LAW_AND_ADMINISTRATION -> "law_and_administration"
            QuestionCategory.HUNTING_PRACTICES -> "hunting_practices"
            else -> "hunting_practices"
        } ?: "hunting_practices"
    }
}