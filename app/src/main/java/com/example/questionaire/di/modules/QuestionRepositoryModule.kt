package com.example.questionaire.di.modules

import com.example.questionaire.data.local.questions.QuestionsRepository
import com.example.questionaire.data.local.questions.impl.HuntingQuestionsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class QuestionRepositoryModule {

    @Provides
    @Singleton
    fun providesRepository(): QuestionsRepository {
        return HuntingQuestionsRepository()
    }
}