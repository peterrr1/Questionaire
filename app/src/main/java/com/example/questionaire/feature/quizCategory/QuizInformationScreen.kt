package com.example.questionaire.feature.quizCategory

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.questionaire.R
import com.example.questionaire.components.common.CustomCircularProgressIndicator
import com.example.questionaire.components.common.CustomPullToRefreshBox
import com.example.questionaire.components.common.CustomPullToRefreshIndicator
import com.example.questionaire.components.common.EmptyScreen
import com.example.questionaire.components.common.ErrorScreen
import com.example.questionaire.components.common.LoadingState
import com.example.questionaire.model.DetailedQuizInfo
import com.example.questionaire.model.PublicUserInfo
import com.example.questionaire.utils.UIState
import com.example.questionaire.utils.hasError
import com.example.questionaire.utils.isRefreshing

@Composable
fun QuizInformationScreen(
    type: String,
    onNavigateToQuiz: (String, String) -> Unit,
    quizCategoryViewModel: QuizInformationViewModel = hiltViewModel<QuizInformationViewModel, QuizInformationViewModel.Factory> { factory ->
        factory.create(type = type)
    }
) {
    val uiState: UIState<DetailedQuizInfo> by quizCategoryViewModel.quizInformation.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is UIState.NoData -> {
            when {
                state.isLoading -> LoadingState()
                state.hasError -> ErrorScreen(
                    onTryAgain = { quizCategoryViewModel.reloadData() },
                    errorMessages = state.errorMessages
                )
                else -> EmptyScreen(
                    onTryAgain = { quizCategoryViewModel.reloadData() },
                    message = "There is no information about the quiz yet."
                )
            }
        }
        is UIState.HasData -> {
            CustomPullToRefreshBox(
                isRefreshing = state.isRefreshing,
                onRefresh = { quizCategoryViewModel.reloadData() }
            ) {
                QuizInformationScreen(
                    state.data,
                    onNavigateToQuiz
                )
            }
        }
    }
}

@Composable
fun QuizInformationScreen(
    quizInformation: DetailedQuizInfo,
    onNavigateToQuiz: (String, String) -> Unit,
    modifier: Modifier = Modifier,

) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Information about the quiz

        Text(text = quizInformation.name, modifier = Modifier.padding(16.dp))
        Text(text = quizInformation.visibility, modifier = Modifier.padding(16.dp))
        Text(text = "Author: ${quizInformation.author.username}", modifier = Modifier.padding(16.dp))

        Spacer(modifier = Modifier.padding(16.dp))

        Text(text = "Categories", modifier = Modifier.padding(horizontal = 16.dp))

        Spacer(modifier = Modifier.padding(16.dp))
        // Question categories
        quizInformation.questionCategories.forEach { quizCategory ->
            QuizCategoryCard(
                drawable = R.drawable.hunting_practices,
                text = quizCategory,
                navigate = {
                    onNavigateToQuiz(quizInformation.collectionId, quizCategory)
                }
            )
        }
    }
}


@Composable
fun QuizCategoryCard(
    @DrawableRes drawable: Int,
    text: String,
    navigate: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.secondaryContainer,
        modifier = modifier
            .padding(10.dp)
            .clickable(onClick = navigate)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Image(
                painter = painterResource(drawable),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(90.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .weight(1f)
            )
            IconButton(
                onClick = {  },
                modifier = Modifier.size(56.dp) // optional, keeps material touch target
            ) {
                Icon(
                    painter = painterResource(R.drawable.more_vert_24px),
                    contentDescription = "Download",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    title: String,
    navigate: () -> Unit
) {
    CenterAlignedTopAppBar(
        navigationIcon = {
            IconButton(onClick = navigate) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_back),
                    contentDescription = null,
                )
            }
        },
        title = {
            Text(text = title)
        }
    )
}




