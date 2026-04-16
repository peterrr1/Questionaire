package com.example.questionaire.feature.quizSummary

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.questionaire.components.common.CustomCircularProgressIndicator
import com.example.questionaire.components.common.EmptyScreen
import com.example.questionaire.components.common.LoadingState
import com.example.questionaire.data.local.entities.QuizAttemptEntity
import com.example.questionaire.data.local.entities.QuizResultEntity
import com.example.questionaire.utils.UIState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizSummaryScreen(
    modifier: Modifier = Modifier,
    viewModel: QuizSummaryViewModel = hiltViewModel()
) {
    val uiState: UIState<List<QuizAttemptEntity>> by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is UIState.NoData -> {
            when {
                state.isLoading -> LoadingState()
                else -> EmptyScreen(
                    onTryAgain = {},
                    message = "There aren't any quiz summaries yet."
                )
            }
        }

        is UIState.HasData -> {
            val results = state.data.reversed()
            val pagerState = rememberPagerState(pageCount = { results.size })

            Column(
                modifier = modifier.fillMaxSize()
            ) {
                // ── Pager header bar ─────────────────────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Attempt",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = "${pagerState.currentPage + 1} / ${pagerState.pageCount}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // ── Segmented pager indicator ─────────────────────────────
                SegmentedPagerIndicator(
                    pageCount = pagerState.pageCount,
                    currentPage = pagerState.currentPage,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // ── Pager ─────────────────────────────────────────────────
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    QuizAttemptPage(
                        attempt = results[page],
                        onDelete = { id -> viewModel.deleteAttempt(id) }
                    )
                }
            }
        }
    }
}

// ── Segmented indicator ───────────────────────────────────────────────────────

@Composable
private fun SegmentedPagerIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(pageCount) { index ->
            val isActive = index == currentPage
            val animatedWeight by animateFloatAsState(
                targetValue = if (isActive) 2.5f else 1f,
                animationSpec = tween(300, easing = FastOutSlowInEasing),
                label = "segment_weight"
            )
            Box(
                modifier = Modifier
                    .weight(animatedWeight)
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        if (isActive) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
            )
        }
    }
}

// ── Attempt page ──────────────────────────────────────────────────────────────

@Composable
private fun QuizAttemptPage(
    attempt: QuizAttemptEntity,
    modifier: Modifier = Modifier,
    onDelete: (Int) -> Unit
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item { Spacer(Modifier.height(4.dp)) }

        item {
            AttemptHeader(attempt = attempt, onDelete = onDelete)
        }

        item { Spacer(Modifier.height(4.dp)) }

        items(attempt.result) { snapshot ->
            QuestionResultCard(snapshot)
        }

        item { Spacer(Modifier.height(20.dp)) }
    }
}

// ── Attempt header card ───────────────────────────────────────────────────────

@Composable
private fun AttemptHeader(
    attempt: QuizAttemptEntity,
    modifier: Modifier = Modifier,
    onDelete: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val scoreRatio = if (attempt.result.isEmpty()) 0f
    else attempt.score.toFloat() / attempt.result.size.toFloat()

    val animatedScore by animateFloatAsState(
        targetValue = scoreRatio,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "score_arc"
    )

    val arcColor = when {
        scoreRatio >= 0.8f -> MaterialTheme.colorScheme.primary
        scoreRatio >= 0.5f -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.error
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .combinedClickable(
                onClick = {},
                onLongClick = { expanded = true }
            )
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: meta info
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = attempt.quizCategory.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    letterSpacing = 1.4.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(2.dp))
                MetaRow(label = "Started", value = attempt.startTime.formatToDisplay())
                MetaRow(label = "Finished", value = attempt.endTime?.formatToDisplay() ?: "—")
                MetaRow(label = "Questions", value = "${attempt.result.size}")
            }

            Spacer(modifier = Modifier.width(20.dp))

            // Right: score arc
            Box(contentAlignment = Alignment.Center) {
                CustomCircularProgressIndicator(
                    progress = animatedScore,
                    size = 80.dp,
                    strokeWidth = 5.dp,
                    arcColor = arcColor,
                    trackColor = MaterialTheme.colorScheme.surface
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${attempt.score}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = arcColor
                    )
                    Text(
                        text = "/ ${attempt.result.size}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        "Delete attempt",
                        color = MaterialTheme.colorScheme.error
                    )
                },
                onClick = {
                    onDelete(attempt.attemptId)
                    expanded = false
                }
            )
        }
    }
}

@Composable
private fun MetaRow(label: String, value: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(60.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

// ── Question result card ──────────────────────────────────────────────────────

@Composable
private fun QuestionResultCard(
    snapshot: QuizResultEntity,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = snapshot.questionText,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            snapshot.optionSnapshots.forEach { option ->
                val isChosen = option.id == snapshot.chosenOptionId
                val isCorrect = option.id == snapshot.correctOptionId

                val accentColor = when {
                    isChosen && isCorrect -> MaterialTheme.colorScheme.primary
                    isChosen && !isCorrect -> MaterialTheme.colorScheme.error
                    isCorrect -> MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    else -> Color.Transparent
                }

                val textColor = when {
                    isChosen -> MaterialTheme.colorScheme.onSurface
                    isCorrect -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }

                val rowBackground = when {
                    isChosen && isCorrect -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                    isChosen && !isCorrect -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)
                    isCorrect -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                    else -> Color.Transparent
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(rowBackground),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left accent border
                    Box(
                        modifier = Modifier
                            .width(3.dp)
                            .height(36.dp)
                            .background(
                                accentColor,
                                RoundedCornerShape(topStart = 6.dp, bottomStart = 6.dp)
                            )
                    )
                    Text(
                        text = option.text,
                        modifier = Modifier
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = if (isChosen || isCorrect) FontWeight.Medium else FontWeight.Normal,
                        color = textColor
                    )
                }
            }
        }
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────

private fun Date.formatToDisplay(): String =
    SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(this)