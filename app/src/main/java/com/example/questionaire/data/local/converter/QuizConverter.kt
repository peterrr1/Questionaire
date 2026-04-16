package com.example.questionaire.data.local.converter

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.example.questionaire.data.local.entities.OptionEntity
import com.example.questionaire.data.local.entities.QuizResultEntity
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.util.Date

@ProvidedTypeConverter
class QuizConverters (
    private val moshi: Moshi
) {

    @TypeConverter
    fun optionsSnapshotToJson(options: List<OptionEntity>): String {
        return moshi.adapter<List<OptionEntity>>(
            Types.newParameterizedType(List::class.java, OptionEntity::class.java)
        ).toJson(options)
    }

    @TypeConverter
    fun jsonToOptionsSnapshot(json: String): List<OptionEntity> {
        return moshi.adapter<List<OptionEntity>>(
            Types.newParameterizedType(List::class.java, OptionEntity::class.java)
        ).fromJson(json) ?: emptyList()
    }

    @TypeConverter
    fun quizResultSnapshotToJson(results: List<QuizResultEntity>): String {
        return moshi.adapter<List<QuizResultEntity>>(
            Types.newParameterizedType(List::class.java, QuizResultEntity::class.java)
        ).toJson(results)
    }

    @TypeConverter
    fun jsonToQuizResultSnapshot(json: String): List<QuizResultEntity> {
        return moshi.adapter<List<QuizResultEntity>>(
            Types.newParameterizedType(List::class.java, QuizResultEntity::class.java)
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
}