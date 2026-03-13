package com.example.questionaire.data.local.db.converter

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.example.questionaire.data.local.db.entities.Option
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
    fun optionToJson(options: List<Option>): String {
        return moshi.adapter<List<Option>>(
            Types.newParameterizedType(List::class.java, Option::class.java)
        ).toJson(options)
    }

    @TypeConverter
    fun jsonToOption(json: String): List<Option> {
        return moshi.adapter<List<Option>>(
            Types.newParameterizedType(List::class.java, Option::class.java)
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
    fun stringToQuestionCategory(value: String): QuestionCategory {
        return QuestionCategory.fromDbValue(value)
    }

    @TypeConverter
    fun questionCategoryToString(category: QuestionCategory): String {
        return category.routeParam
    }
}