package com.example.questionaire.di

import android.content.Context
import androidx.room.Room
import com.example.questionaire.data.local.AppDatabase
import com.example.questionaire.data.local.converter.QuizConverters
import com.example.questionaire.data.local.dao.QuizAttemptDao
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
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
    fun provideQuizAttemptDao(database: AppDatabase): QuizAttemptDao {
        return database.quizAttemptDao()
    }
}