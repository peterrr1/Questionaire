package com.example.questionaire.utils

import android.content.Context
import android.util.Log
import com.example.questionaire.data.local.db.AppDatabase
import com.example.questionaire.utils.dtos.QuestionDTO
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseSeeder @Inject constructor(
    private val db: AppDatabase,
    @ApplicationContext private val app: Context,
    private val moshi: Moshi
) {
    suspend fun seedIfEmpty() {
        if (db.questionsDao().getCount() > 0) return

        val jsonString = app.assets
            .open("database/questions.json")
            .bufferedReader()
            .use { it.readText() }

        val type = Types.newParameterizedType(
            List::class.java,
            QuestionDTO::class.java
        )

        val adapter: JsonAdapter<List<QuestionDTO>> =
            moshi.adapter(type)

        val questions = adapter.fromJson(jsonString).orEmpty()
        val categories = questions.map { it.category }.distinct()

        Log.d("SEEDER", "Unique categories: $categories")

        val entities = questions.map { it.toEntity() }

        db.questionsDao().insertQuestions(entities)
        Log.d("SEEDER", "Number of entries: ${db.questionsDao().getCount()}")
    }
}