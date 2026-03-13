package com.example.questionaire

import android.app.Application
import com.example.questionaire.utils.DatabaseSeeder
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


@HiltAndroidApp
class HuntingQuizApp : Application() {

    @Inject lateinit var seeder: DatabaseSeeder

    override fun onCreate() {
        super.onCreate()

        runBlocking {
            seeder.seedIfEmpty()
        }
    }
}

