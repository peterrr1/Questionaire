package com.example.questionaire

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.questionaire.ui.screens.home.HomeScreen
import com.example.questionaire.ui.screens.quiz.QuizScreen
import com.example.questionaire.ui.screens.quizSummary.QuizSummaryScreen
import com.example.questionaire.ui.theme.HuntingQuizTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {

            val navController = rememberNavController()
            val navigationActions = remember(navController) {
                HuntingQuizNavigationActions(navController)
            }


            HuntingQuizTheme {
                NavHost(
                    navController = navController,
                    startDestination = HuntingQuizDestinations.HOME_ROUTE,
                ) {
                    composable(route = HuntingQuizDestinations.HOME_ROUTE) {
                        HomeScreen(
                            navigateToQuiz = navigationActions.navigateToQuiz
                        )
                    }
                    composable(
                        route = HuntingQuizDestinations.QUIZ_ROUTE,
                        arguments = listOf(
                            navArgument("quizType") { type = NavType.StringType }
                        )
                    ) {
                        QuizScreen(
                            onNavigateBack = navigationActions.navigateToHome,
                            onFinished = navigationActions.navigateToSummary
                        )
                    }
                    composable(route = HuntingQuizDestinations.SUMMARY_ROUTE) {
                        QuizSummaryScreen(
                            onNavigateBack = navigationActions.navigateToHome,
                            onNavigateHome = navigationActions.navigateToHome
                        )
                    }
                }
            }
        }
    }
}
