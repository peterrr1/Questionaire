package com.example.questionaire.di.modules

import com.example.questionaire.network.QuizApiService
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
    private val baseUrl =
        "http://10.0.2.2:8000/"

    @Provides
    @Singleton
    fun provideApiService(
        moshi: Moshi
    ): QuizApiService {

        val retrofit = Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl(baseUrl)
            .build()

        return retrofit.create(QuizApiService::class.java)
    }
}