package com.example.questionaire.ui.screens.quizSummary

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.BottomAppBar
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.questionaire.R
import com.example.questionaire.components.common.LoadingState
import com.example.questionaire.data.local.db.entities.QuizResultSnapshot
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.example.questionaire.model.QuestionCategory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizSummaryScreen(
    onNavigateBack: () -> Unit,
    onNavigateHome: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: QuizSummaryViewModel = hiltViewModel()
) {

    val uiState: QuizResultsUIState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Quiz Results"
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = null
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.background(MaterialTheme.colorScheme.background),
                actions = {
                    IconButton(onClick = onNavigateHome) {
                        Icon(
                            painter = painterResource(R.drawable.home_24px),
                            contentDescription = null
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->

        when (val state = uiState) {
            is QuizResultsUIState.HasResults -> {
                val results = state.results.reversed()
                val pagerState = rememberPagerState(pageCount = { results.size })

                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
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

                    Text(
                        text = "Attempt ${pagerState.currentPage + 1} of ${pagerState.pageCount}",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(16.dp)
                    )

                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        QuizAttemptPage(
                            attemptSnapshot = results[page],
                            onDelete = { id -> viewModel.deleteAttempt(id) }
                        )
                    }
                }
            }
            is QuizResultsUIState.NoResults -> {
                if (state.isLoading) {
                    LoadingState(modifier = Modifier.padding(innerPadding))
                } else {
                    // TODO(): Show error message
                }
            }
        }
    }
}

@Composable
fun QuizAttemptPage(
    attemptSnapshot: QuizAttemptSnapshot,
    modifier: Modifier = Modifier,
    onDelete: (Int) -> Unit
) {

    LazyColumn(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            AttemptHeader(attemptSnapshot, onDelete = onDelete)
        }

        items(attemptSnapshot.result) {
            QuestionResultCard(it)
        }
    }
}

@Composable
fun AttemptHeader(
    attempt: QuizAttemptSnapshot,
    modifier: Modifier = Modifier,
    onDelete: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.secondaryContainer,
                RoundedCornerShape(8.dp)
            )
            .combinedClickable(
                onClick = {},
                onLongClick = { expanded = true })
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = attempt.quizType.displayName,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = "Started: ${attempt.startTime.formatToDisplay()}",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "Finished: ${attempt.endTime?.formatToDisplay() ?: "—"}",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "Score: ${attempt.score} / ${attempt.result.size}",
            style = MaterialTheme.typography.bodyMedium
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Delete") },
                onClick = {
                    onDelete(attempt.attemptId)
                    expanded = false
                }
            )
        }
    }
}

@Composable
fun QuestionResultCard(
    snapshot: QuizResultSnapshot,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(6.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = snapshot.questionText,
            style = MaterialTheme.typography.bodyLarge
        )

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            snapshot.optionSnapshots.forEach { option ->
                val isChosen = option.id == snapshot.chosenOptionId
                val isCorrect = option.id == snapshot.correctOptionId

                val background = when {
                    isChosen && isCorrect -> Color(0xFFB9FBC0)
                    isChosen && !isCorrect -> Color(0xFFFFB3B3)
                    isCorrect -> Color(0xFFD0F0C0)
                    else -> MaterialTheme.colorScheme.surface
                }

                Text(
                    text = option.text,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(background, RoundedCornerShape(4.dp))
                        .padding(8.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

private fun Date.formatToDisplay(): String {
    val formatter = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
    return formatter.format(this)
}
/*
private fun categoryToDisplayName(quizType: QuestionCategory): String {
    return when (quizType) {
        QuestionCategory.HUNTING_ZOOLOGY -> "Hunting zoology"
        QuestionCategory.LAW_AND_ADMINISTRATION -> "Law and administration"
        QuestionCategory.HUNTING_PRACTICES -> "Hunting practices"
        QuestionCategory.ALL_CATEGORIES -> "All categories"
    }
}
 */


