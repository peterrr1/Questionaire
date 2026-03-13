package com.example.questionaire.di.modules

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.questionaire.data.local.db.AppDatabase
import com.example.questionaire.data.local.db.converter.QuizConverters
import com.example.questionaire.data.local.db.dao.QuizAttemptDao
import com.example.questionaire.data.local.db.dao.QuizQuestionDao
import com.example.questionaire.data.local.db.entities.Option
import com.example.questionaire.data.local.db.entities.Question
import com.example.questionaire.model.QuestionCategory
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.logging.Logger
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class AppDatabaseModule {

    @Provides
    @Singleton
    fun provideYourDatabase(
        @ApplicationContext app: Context,
        moshi: Moshi
    ): AppDatabase {

        //app.deleteDatabase("dev_db")


        return Room.databaseBuilder(
            app,
            AppDatabase::class.java,
            "dev_db"
        )
            .addTypeConverter(QuizConverters(moshi))
            .fallbackToDestructiveMigration(true).build()
    }

    @Provides
    @Singleton
    fun provideQuestionsDao(database: AppDatabase): QuizQuestionDao {
        return database.questionsDao()
    }

    @Provides
    @Singleton
    fun provideQuizAttemptDao(database: AppDatabase): QuizAttemptDao {
        return database.quizAttemptDao()
    }


}