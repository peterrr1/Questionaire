package com.example.questionaire

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.questionaire.components.common.TopAppBar
import com.example.questionaire.feature.create.CreateScreen
import com.example.questionaire.feature.home.HomeScreen
import com.example.questionaire.feature.home.HomeViewModel
import com.example.questionaire.feature.login.LoginScreen
import com.example.questionaire.feature.quiz.QuizRouteParams
import com.example.questionaire.feature.quiz.QuizScreen
import com.example.questionaire.feature.quizCategory.QuizInformationScreen
import com.example.questionaire.feature.quizSummary.QuizSummaryScreen
import com.example.questionaire.model.DetailedQuizInfo
import com.example.questionaire.theme.HuntingQuizTheme
import com.example.questionaire.utils.managers.TokenManager
import com.example.questionaire.utils.managers.TokenType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.BUFFERED
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.serialization.Serializable
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

interface TitledNavKey {
    val title: String
}

@Serializable
sealed class ConditionalNavKey(val requiresLogin: Boolean = false) : NavKey

@Serializable
data object Home: NavKey, TitledNavKey {
    override val title = "Home"
}

@Serializable
data class QuizInformation(val type: String): NavKey, TitledNavKey {
    override val title = "Quiz Information"
    companion object
}

@Serializable
data class Quiz(val collectionId: String, val categoryDisplayName: String): NavKey, TitledNavKey {
    override val title = categoryDisplayName
}

@Serializable
data object Login: NavKey, TitledNavKey {
    override val title = "Login"
}
@Serializable
data object Summary: NavKey, TitledNavKey {
    override val title = "Summary"
}

@Serializable
data object Search: NavKey, TitledNavKey {
    override val title = "Search"
}

@Serializable
data class Create(val quizId: String?): NavKey, TitledNavKey {
    override val title = "Create"
}


sealed class AuthState {
    data object Loading : AuthState()
    data object Authenticated : AuthState()
    data object Unauthenticated : AuthState()
}

@Composable
inline fun <reified T> ResultEffect(
    resultEventBus: ResultEventBus = LocalResultEventBus.current,
    resultKey: String = T::class.toString(),
    crossinline onResult: suspend (T) -> Unit
) {
    LaunchedEffect(resultKey, resultEventBus.channelMap[resultKey]) {
        resultEventBus.getResultFlow<T>(resultKey)?.collect { result ->
            onResult.invoke(result as T)
        }
    }
}

object LocalResultEventBus {
    private val LocalResultEventBus: ProvidableCompositionLocal<ResultEventBus?> =
        compositionLocalOf { null }
    
    val current: ResultEventBus
        @Composable
        get() = LocalResultEventBus.current ?: error("No ResultEventBus has been provided")


    infix fun provides(
        bus: ResultEventBus
    ): ProvidedValue<ResultEventBus?> {
        return LocalResultEventBus.provides(bus)
    }
}

class ResultEventBus {

    val channelMap = mutableStateMapOf<String, Channel<Any?>>()

    inline fun <reified T> getResultFlow(resultKey: String = T::class.toString()) =
        channelMap[resultKey]?.receiveAsFlow()

    inline fun <reified T> sendResult(resultKey: String = T::class.toString(), result: T) {
        if (!channelMap.contains(resultKey)) {
            channelMap[resultKey] = Channel(capacity = BUFFERED, onBufferOverflow = BufferOverflow.SUSPEND)
        }
        channelMap[resultKey]?.trySend(result)
    }

    inline fun <reified T> removeResult(resultKey: String = T::class.toString()) {
        channelMap.remove(resultKey)
    }

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
                listOf(QuizInformation::class, Summary::class, Quiz::class, Create::class)

            val showBottomBar =
                backStack.lastOrNull()?.let { it::class in screensWithBottomBar } ?: false
            val showTopBarBackButton =
                backStack.lastOrNull()?.let { it::class in screensWithTopBarBackButton } ?: false

            val screenTitle = (backStack.lastOrNull() as? TitledNavKey)?.title ?: "Screen Title"

            val authState by produceState<AuthState>(initialValue = AuthState.Loading) {
                tokenManager.getToken(TokenType.ACCESS_TOKEN).collect { token ->
                    value = if (token != null) AuthState.Authenticated else AuthState.Unauthenticated
                }
            }

            val resultBus = remember { ResultEventBus() }

            HuntingQuizTheme {
                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            BottomNavigationBar(backStack)
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
                        Log.d("AUTH", "AuthState changed: $authState")
                        when (authState) {
                            is AuthState.Loading -> Unit
                            is AuthState.Authenticated -> {
                                Log.d("AUTH", "Navigating to Home")
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
                                val triggerReload = MutableStateFlow(false)
                                ResultEffect<Boolean>(resultBus) { isSuccess ->
                                    Log.d("NAV_DISPLAY", "$isSuccess")
                                    triggerReload.value = true
                                }

                                HomeScreen(
                                    navigateToQuizType = { type ->
                                        backStack.add(QuizInformation(type))
                                    },
                                    triggerReload = triggerReload
                                )
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
                                    onDeleteRedirect = {
                                        resultBus.sendResult(result = true)
                                        backStack.clear()
                                        backStack.add(Home)
                                    },
                                    onEditRedirect = { quizId ->
                                        backStack.add(Create(quizId))
                                    }
                                )
                            }
                            entry<Quiz> { key ->
                                QuizScreen(
                                    params = QuizRouteParams(key.collectionId, key.categoryDisplayName),
                                    onFinished = {
                                        backStack.removeLastOrNull()
                                        backStack.add(Summary)
                                    }
                                )
                            }
                            entry<Summary> {
                                QuizSummaryScreen()
                            }
                            entry<Create> { key ->
                                CreateScreen(
                                    quizId = key.quizId,
                                    onSubmit = { isSuccess ->
                                        resultBus.sendResult(result = isSuccess)
                                        backStack.removeLastOrNull()
                                    }
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    backStack: NavBackStack<NavKey>
) {
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
            selected = backStack.lastOrNull() is Search,
            onClick = { backStack.add(Home) },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.ic_search),
                    contentDescription = null
                )
            },
            label = { Text("Search") }
        )
        NavigationBarItem(
            modifier = Modifier.align(Alignment.CenterVertically),
            selected = backStack.lastOrNull() is Create,
            onClick = { backStack.add(Create(null)) },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.ic_plus),
                    contentDescription = null
                )
            },
            label = { Text("Create") }
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

