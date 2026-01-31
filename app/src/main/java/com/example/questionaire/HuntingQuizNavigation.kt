package com.example.questionaire

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

object HuntingQuizDestinations {
    const val HOME_ROUTE = "home"
    const val QUIZ_ROUTE = "quiz/{quizType}"
    const val SUMMARY_ROUTE = "summary"

    fun createQuizRoute(quizType: String) = "quiz/$quizType"
}


class HuntingQuizNavigationActions(navController: NavHostController) {

    val navigateToHome: () -> Unit = {
        navController.navigate(HuntingQuizDestinations.HOME_ROUTE) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = false
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    val navigateToQuiz: (String) -> Unit = { quizType ->
        navController.navigate(HuntingQuizDestinations.createQuizRoute(quizType)) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    val navigateToSummary: () -> Unit = {
        navController.navigate(HuntingQuizDestinations.SUMMARY_ROUTE) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = false
            }
            launchSingleTop = true
            restoreState = true
        }
    }
}