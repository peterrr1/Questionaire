package com.example.questionaire

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.DefaultTab.AlbumsTab.value
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.questionaire.components.common.TopAppBar
import com.example.questionaire.feature.home.HomeScreen
import com.example.questionaire.feature.login.LoginScreen
import com.example.questionaire.feature.quiz.QuizRouteParams
import com.example.questionaire.feature.quiz.QuizScreen
import com.example.questionaire.feature.quizCategory.QuizInformationScreen
import com.example.questionaire.feature.quizSummary.QuizSummaryScreen
import com.example.questionaire.theme.HuntingQuizTheme
import com.example.questionaire.utils.managers.TokenManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.Serializable
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

interface TitledNavKey {
    val title: String
}

@Serializable
data object Home: NavKey, TitledNavKey {
    override val title = "Home"
}
@Serializable
data class QuizInformation(val type: String): NavKey, TitledNavKey {
    override val title = "Quiz Information"
    companion object {
    }
}
@Serializable
data class Quiz(val collectionId: String, val category: String): NavKey, TitledNavKey {
    override val title = category
}

@Serializable
data object Login: NavKey, TitledNavKey {
    override val title = "Login"
}
@Serializable
data object Summary: NavKey, TitledNavKey {
    override val title = "Summary"
}

sealed class AuthState {
    data object Loading : AuthState()
    data object Authenticated : AuthState()
    data object Unauthenticated : AuthState()
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val backStack = rememberNavBackStack(Login)
            val screensWithBottomBar = listOf(Home::class, QuizInformation::class, Summary::class)
            val screensWithTopBarBackButton =
                listOf(QuizInformation::class, Summary::class, Quiz::class)

            val showBottomBar =
                backStack.lastOrNull()?.let { it::class in screensWithBottomBar } ?: false
            val showTopBarBackButton =
                backStack.lastOrNull()?.let { it::class in screensWithTopBarBackButton } ?: false

            val screenTitle = (backStack.lastOrNull() as? TitledNavKey)?.title ?: "Screen Title"

            val authState by produceState<AuthState>(initialValue = AuthState.Loading) {
                tokenManager.getToken("ACCESS").collect { token ->
                    Log.d("AUTH", "Token emitted: $token") // ✅ is this firing after login?
                    value = if (token != null) AuthState.Authenticated else AuthState.Unauthenticated
                }
            }

            HuntingQuizTheme {
                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            NavigationBar {
                                NavigationBarItem(
                                    modifier = Modifier.align(Alignment.CenterVertically),
                                    selected = backStack.lastOrNull() is Home,
                                    onClick = { backStack.add(Home) },
                                    icon = {
                                        Icon(
                                            painter = painterResource(R.drawable.home_24px),
                                            contentDescription = null
                                        )
                                    },
                                    label = { Text("Home") }
                                )
                                NavigationBarItem(
                                    modifier = Modifier.align(Alignment.CenterVertically),
                                    selected = backStack.lastOrNull() is Summary,
                                    onClick = { backStack.add(Summary) },
                                    icon = {
                                        Icon(
                                            painter = painterResource(R.drawable.library_books_24dp_e3e3e3_fill0_wght400_grad0_opsz24),
                                            contentDescription = null
                                        )
                                    },
                                    label = { Text("Summary") }
                                )
                                NavigationBarItem(
                                    modifier = Modifier.align(Alignment.CenterVertically),
                                    selected = backStack.lastOrNull() is Login,
                                    onClick = { backStack.add(Login) },
                                    icon = {
                                        Icon(
                                            painter = painterResource(R.drawable.person_24dp_e3e3e3_fill0_wght400_grad0_opsz24),
                                            contentDescription = null
                                        )
                                    },
                                    label = { Text("Profile") }
                                )
                            }
                        }
                    },
                    topBar = {
                        TopAppBar(
                            title = screenTitle,
                            showBackButton = showTopBarBackButton,
                            onBack = {
                                if (showTopBarBackButton) {
                                    backStack.removeLastOrNull()
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    LaunchedEffect(authState) {
                        Log.d("AUTH", "AuthState changed: $authState") // ✅ is this reaching Authenticated?
                        when (authState) {
                            is AuthState.Loading -> Unit
                            is AuthState.Authenticated -> {
                                Log.d("AUTH", "Navigating to Home") // ✅ is navigation being triggered?
                                backStack.clear()
                                backStack.add(Home)
                            }
                            is AuthState.Unauthenticated -> {
                                backStack.clear()
                                backStack.add(Login)
                            }
                        }
                    }
                    NavDisplay(
                        modifier = Modifier.padding(innerPadding),
                        entryDecorators = listOf(
                            rememberSaveableStateHolderNavEntryDecorator(),
                            rememberViewModelStoreNavEntryDecorator()
                        ),
                        backStack = backStack,
                        onBack = { backStack.removeLastOrNull() },
                        entryProvider = entryProvider {
                            entry<Home> {
                                if (authState is AuthState.Loading) {
                                    // show splash or blank
                                    return@entry
                                }
                                HomeScreen(
                                    navigateToQuizType = { type ->
                                        backStack.add(QuizInformation(type))
                                    })
                            }
                            entry<Login> {
                                LoginScreen()
                            }
                            entry<QuizInformation> { key ->
                                QuizInformationScreen(
                                    type = key.type,
                                    onNavigateToQuiz = { type, category ->
                                        backStack.add(Quiz(type, category))
                                    },
                                )
                            }
                            entry<Quiz> { key ->
                                QuizScreen(
                                    params = QuizRouteParams(key.collectionId, key.category),
                                    onFinished = {
                                        backStack.removeLastOrNull()
                                        backStack.add(Summary)
                                    }
                                )
                            }
                            entry<Summary> {
                                QuizSummaryScreen()
                            }
                        }
                    )
                }
            }
        }
    }
}

