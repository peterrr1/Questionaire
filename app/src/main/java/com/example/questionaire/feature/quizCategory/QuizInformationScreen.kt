package com.example.questionaire.feature.quizCategory

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.questionaire.R
import com.example.questionaire.components.common.CustomPullToRefreshBox
import com.example.questionaire.components.common.EmptyScreen
import com.example.questionaire.components.common.ErrorScreen
import com.example.questionaire.components.common.LoadingState
import com.example.questionaire.model.DetailedQuizInfo
import com.example.questionaire.model.PublicUserInfo
import com.example.questionaire.theme.HuntingQuizTheme
import com.example.questionaire.utils.UIState
import com.example.questionaire.utils.hasError
import com.example.questionaire.utils.isRefreshing
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.time.Instant

@Composable
fun QuizInformationScreen(
    type: String,
    onNavigateToQuiz: (String, String) -> Unit,
    onDeleteRedirect: () -> Unit,
    quizCategoryViewModel: QuizInformationViewModel = hiltViewModel<QuizInformationViewModel, QuizInformationViewModel.Factory> { factory ->
        factory.create(type = type)
    }
) {
    val uiState: UIState<DetailedQuizInfo> by quizCategoryViewModel.quizInformation.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is UIState.NoData -> when {
            state.isLoading -> LoadingState()
            state.hasError -> ErrorScreen(
                onTryAgain = { quizCategoryViewModel.reloadData() },
                errorMessage = state.errorMessages.last()
            )
            else -> EmptyScreen(
                onTryAgain = { quizCategoryViewModel.reloadData() },
                message = "There is no information about this quiz yet."
            )
        }

        is UIState.HasData -> {
            CustomPullToRefreshBox(
                isRefreshing = state.isRefreshing,
                onRefresh = { quizCategoryViewModel.reloadData() }
            ) {
                QuizInformationContent(
                    quizInformation = state.data,
                    onNavigateToQuiz = onNavigateToQuiz,
                    onDelete = {
                        quizCategoryViewModel.deleteQuiz(state.data.id)
                        onDeleteRedirect()
                    }
                )
            }
        }
    }
}

// ── Main content ──────────────────────────────────────────────────────────────

@Composable
private fun QuizInformationContent(
    quizInformation: DetailedQuizInfo,
    onNavigateToQuiz: (String, String) -> Unit,
    onDelete: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var sheetCategory by remember { mutableStateOf<Pair<String, String>?>(null) }

    val allCategories = listOf("ALL") + quizInformation.questionCategories
    val zippedCategories = allCategories.zip(listOf("All") + quizInformation.categoriesDisplayName)

    val visibleCategories = if (selectedCategory == null || selectedCategory == "ALL") {
        zippedCategories
    } else {
        zippedCategories.filter { it.first == selectedCategory }
    }

    Box(modifier = modifier.fillMaxSize()) {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // ── Hero header ───────────────────────────────────────────────
            item {
                QuizHeroHeader(
                    onDelete = onDelete,
                    quizInformation = quizInformation
                )
            }

            // ── Category filter chips ─────────────────────────────────────
            item {
                CategoryFilterRow(
                    categories = zippedCategories,
                    selected = selectedCategory ?: "ALL",
                    onSelect = { selectedCategory = it }
                )
            }

            item { Spacer(Modifier.height(8.dp)) }

            // ── Category cards ────────────────────────────────────────────
            items(visibleCategories) { (categoryName, displayName) ->
                QuizCategoryCard(
                    drawable = R.drawable.hunting_practices,
                    text = displayName,
                    onClick = { sheetCategory = Pair(categoryName, displayName) }
                )
            }

            item { Spacer(Modifier.height(120.dp)) }
        }

        // ── Scrim ─────────────────────────────────────────────────────────
        AnimatedVisibility(
            visible = sheetCategory != null,
            enter = fadeIn(tween(200)),
            exit = fadeOut(tween(200))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.4f))
                    .clickable { sheetCategory != null }
            )
        }

        // ── Floating bottom sheet ─────────────────────────────────────────
        AnimatedVisibility(
            visible = sheetCategory != null,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(340, easing = FastOutSlowInEasing)
            ) + fadeIn(tween(200)),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(260, easing = FastOutSlowInEasing)
            ) + fadeOut(tween(150))
        ) {
            sheetCategory?.let { category ->
                CategoryDetailSheet(
                    categoryDisplayName = category.second,
                    onPlay = { onNavigateToQuiz(quizInformation.collectionId, category.first) },
                    onDismiss = { sheetCategory = null }
                )
            }
        }
    }
}

// ── Hero header ───────────────────────────────────────────────────────────────

@Composable
private fun QuizHeroHeader(
    quizInformation: DetailedQuizInfo,
    onDelete: () -> Unit = {},
    onEdit: () -> Unit = {},
) {
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.inverseOnSurface)
                .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Visibility badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = quizInformation.visibility.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        letterSpacing = 1.2.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Quiz name
                Text(
                    text = quizInformation.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 32.sp
                )

                // Author row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = quizInformation.author.username.take(1).uppercase(),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = quizInformation.author.username,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                    val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

                    val date = inputFormat.parse(quizInformation.author.createdAt)
                    val formatted = outputFormat.format(date!!)

                    StatChip(
                        label = "Created at:",
                        value = formatted
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(end = 20.dp, top = 8.dp),
                    color = MaterialTheme.colorScheme.secondary,
                )
                if (quizInformation.editable) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(end = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.background,
                                contentColor = MaterialTheme.colorScheme.onBackground
                            ),
                            border = BorderStroke(
                                width = 2.dp, MaterialTheme.colorScheme.tertiary
                            ),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 0.dp,
                                pressedElevation = 0.dp
                            ),
                            onClick = onEdit,
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                            modifier = Modifier
                                .weight(1f)
                                .defaultMinSize(minWidth = 1.dp, minHeight = 1.dp)
                        ) {
                            Text(
                                style = MaterialTheme.typography.labelSmall,
                                text = "Edit"
                            )
                        }
                        Button(
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.background,
                                contentColor = MaterialTheme.colorScheme.onBackground
                            ),
                            border = BorderStroke(
                                width = 2.dp, MaterialTheme.colorScheme.error
                            ),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 0.dp,
                                pressedElevation = 0.dp
                            ),
                            onClick = onDelete,
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                            modifier = Modifier
                                .weight(1f)
                                .defaultMinSize(minWidth = 1.dp, minHeight = 1.dp)
                        ) {
                            Text(
                                style = MaterialTheme.typography.labelSmall,
                                text = "Delete"
                            )
                        }
                    }
                }
            }
            VerticalDivider(color = MaterialTheme.colorScheme.secondary)
            Image(
                painter = painterResource(R.drawable.placeholder),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(start = 20.dp)
                    .clip(RoundedCornerShape(20.dp))
            )
        }
    }
}

@Composable
private fun StatChip(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 0.8.sp
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

// ── Category filter row ───────────────────────────────────────────────────────

@Composable
private fun CategoryFilterRow(
    categories: List<Pair<String, String>>,
    selected: String,
    onSelect: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categories.forEach { (categoryName, displayName) ->
            val isSelected = categoryName == selected
            val animatedAlpha by animateFloatAsState(
                targetValue = if (isSelected) 1f else 0f,
                animationSpec = tween(200),
                label = "chip_alpha_$categoryName"
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                    .clickable { onSelect(categoryName) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = displayName,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ── Category card ─────────────────────────────────────────────────────────────

@Composable
private fun QuizCategoryCard(
    @DrawableRes drawable: Int,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 5.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Thumbnail
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
        ) {
            Image(
                painter = painterResource(drawable),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // Gradient overlay for readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                MaterialTheme.colorScheme.surface.copy(alpha = 0f),
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.1f)
                            )
                        )
                    )
            )
        }

        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 14.dp)
        )

        Icon(
            painter = painterResource(R.drawable.more_vert_24px),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .padding(end = 14.dp)
                .size(20.dp)
        )
    }
}

// ── Category detail sheet ─────────────────────────────────────────────────────

@Composable
private fun CategoryDetailSheet(
    categoryDisplayName: String,
    onPlay: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(MaterialTheme.colorScheme.surface)
            .navigationBarsPadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Drag handle
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
                .align(Alignment.CenterHorizontally)
        )

        // Category badge
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Text(
                text = "CATEGORY",
                style = MaterialTheme.typography.labelSmall,
                letterSpacing = 1.2.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Category name
        Text(
            text = categoryDisplayName,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        // Divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )

        // Play button
        Button(
            onClick = onPlay,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_play),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Start Quiz",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun QuizHeroHeaderPreview() {
    val quizInformationDummy = DetailedQuizInfo(
        id = "77477ebc-e1f7-45c3-8e5c-16582bf51ab3",
        name = "Hunting",
        collectionId = "quiz_94c1b44a-bb30-42e2-a28b-661e4d72ffda",
        visibility = "Public",
        displayImageUrl = "",
        author = PublicUserInfo(
            username = "peterrr",
            createdAt = "2026-05-02T17:36:57.472Z"
        ),
        questionCategories = listOf(
            "JOGI_ES_IGAZGATASI_KERDESEK",
            "VADASZATI_ALLATTAN"
        ),
        categoriesDisplayName = listOf("Jogi és igazgatási kérdések",
            "Vadászati állattan")
    )
    HuntingQuizTheme {
        QuizHeroHeader(quizInformationDummy)
    }
}