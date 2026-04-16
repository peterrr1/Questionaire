package com.example.questionaire.feature.quiz

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.questionaire.R
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.questionaire.components.common.CustomCircularProgressIndicator
import com.example.questionaire.components.common.EmptyScreen
import com.example.questionaire.components.common.ErrorScreen
import com.example.questionaire.components.common.LoadingState
import com.example.questionaire.model.Question
import com.example.questionaire.utils.ErrorMessage
import com.example.questionaire.utils.UIState
import com.example.questionaire.utils.hasError


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    params: QuizRouteParams,
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
    quizViewModel: QuizViewModel = hiltViewModel<QuizViewModel, QuizViewModel.Factory> { factory ->
        factory.create(params = params)
    }
) {

    val uiState: UIState<List<Question>> by quizViewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is UIState.NoData -> {
            when {
                state.isLoading -> LoadingState()
                state.hasError -> ErrorScreen(
                    onTryAgain = { quizViewModel.reloadData() },
                    errorMessages = state.errorMessages
                )
                else -> EmptyScreen(
                    onTryAgain = { quizViewModel.reloadData() },
                    message = "There is no information about the quiz yet."
                )
            }
        }

        is UIState.HasData -> {
            val pagerState = rememberPagerState(pageCount = { state.data.size })
            Box(
                modifier = modifier
                    .fillMaxSize()
            ) {
                Column {

                    val targetProgress = if (pagerState.pageCount == 0) 0f
                    else (pagerState.currentPage + 1f) / pagerState.pageCount

                    val animatedProgress by animateFloatAsState(
                        targetValue = targetProgress,
                        animationSpec = tween(
                            durationMillis = 300,
                            easing = FastOutSlowInEasing
                        ),
                        label = "PagerProgress"
                    )

                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        progress = { animatedProgress }
                    )

                    VerticalPager(
                        state = pagerState,
                        modifier = Modifier.weight(1f)
                    ) { page ->

                        QuestionElement(
                            question = state.data[page],
                            onSelectOption = { optionId ->
                                quizViewModel.onSelectOption(
                                    questionId = state.data[page].id,
                                    optionId = optionId
                                )
                            },
                            onCheckAnswer = { optionId ->
                                quizViewModel.onCheckAnswer(
                                    state.data[page].id,
                                    optionId = optionId
                                )
                            }
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .zIndex(1f)
                        .align(Alignment.BottomCenter)
                ) {
                    AnimatedVisibility(
                        visible = pagerState.currentPage == pagerState.pageCount - 1,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {

                        Button(
                            onClick = {
                                quizViewModel.createQuizResultSnapshot()
                                onFinished()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text("Finish quiz!")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuestionElement(
    onSelectOption: (String) -> Unit,
    onCheckAnswer: (String) -> Boolean,
    question: Question,
    modifier: Modifier = Modifier
) {

    var selectedOptionId by rememberSaveable { mutableStateOf<String?>(null) }
    var showAnswers by rememberSaveable { mutableStateOf(false) }


    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = question.text,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.padding(vertical = 20.dp))
        Column {
            question.options.forEach { option ->
                val isSelected: Boolean = selectedOptionId == option.id
                val isCorrect: Boolean = onCheckAnswer(option.id)

                val optionBackgroundColor by animateColorAsState(
                    targetValue =
                        if (isCorrect && showAnswers)
                            MaterialTheme.colorScheme.primaryContainer
                        else if (showAnswers && !isCorrect)
                            MaterialTheme.colorScheme.errorContainer
                        else
                            MaterialTheme.colorScheme.secondaryContainer,
                    label = "bgColor"
                )

                val borderWidth by animateDpAsState(
                    targetValue =
                        if (isSelected)
                            2.dp
                        else 0.dp,
                    label = "border"
                )

                val scale by animateFloatAsState(
                    targetValue = if (isSelected) 1.03f else 1f,
                    label = "scale"
                )

                Button(
                    onClick = {
                        selectedOptionId = option.id
                        onSelectOption(option.id)

                    },
                    border = if (borderWidth > 0.dp) {
                        BorderStroke(borderWidth, MaterialTheme.colorScheme.primary,)
                    }
                    else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(70.dp)
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                        },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = optionBackgroundColor
                    ),
                    contentPadding = PaddingValues(8.dp))
                {
                    Text(
                        text = option.text,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
        Spacer(modifier = Modifier.padding(vertical = 20.dp))

        AnimatedVisibility(
            visible = selectedOptionId != null,
            enter = fadeIn() + expandHorizontally(),
            exit = fadeOut() + shrinkHorizontally()
        ) {

            Button(
                onClick = {
                    showAnswers = !showAnswers
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                if (showAnswers)
                    Text("Reset")
                else
                    Text("Show answers")
            }
        }
    }
}



