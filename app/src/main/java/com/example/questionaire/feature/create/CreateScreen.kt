package com.example.questionaire.feature.create

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.room.util.TableInfo
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.example.questionaire.R
import com.example.questionaire.model.Option
import com.example.questionaire.model.Question
import com.example.questionaire.utils.UIState
import java.util.UUID

@Composable
fun CreateScreen(
    createViewModel: CreateViewModel = hiltViewModel()
) {
    val uiState: UIState<List<Question>> by createViewModel.uiState.collectAsStateWithLifecycle()

//    val questions: List<Question> = when (val state = uiState) {
//        is UIState.NoData -> emptyList()
//        is UIState.HasData -> state.data
//    }

    val questions: List<Question> = listOf(
        Question(
            id = UUID.randomUUID().toString(),
            type = "SINGLE_OPTION",
            category = "JOGI_ÉS_IGAZGATÁSI_KÉRDÉSEK",
            text = "Kié hazánkban a vad tulajdonjoga?",
            options = listOf(
                Option(
                    id = "1",
                    text = "Vadászatra jogosult"
                ),
                Option(
                    id = "2",
                    text = "Földtulajdonhoz kötött"
                ),
                Option(
                    id = "3",
                    text = "Állam"
                )
            ),
            correctOptionId = "3"
        )
    )


    var showQuestionDetail by remember { mutableStateOf(false) }
    var selectedQuestion by remember { mutableStateOf<Question?>(null) }

    val visibilityTypes = listOf("Public", "Private")
    var selected by remember { mutableStateOf<String?>(null) }

    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(showQuestionDetail) {
                detectTapGestures {
                    if (!showQuestionDetail) {
                        focusManager.clearFocus()
                    }
                }
            }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(R.drawable.placeholder_long),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10)),
                        contentScale = ContentScale.FillWidth
                    )


                    // Edit hint overlay
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                                shape = CircleShape
                            )
                            .padding(6.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.more_vert_24px),
                            contentDescription = "Change image",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Quiz name
            item {
                Text(
                    text = "Quiz name",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            item {
                Spacer(modifier = Modifier.height(10.dp))
            }

            item {
                OutlinedTextField(
                    state = rememberTextFieldState(),
                    label = { Text("Quiz name") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Visibility toggle button
            item {
                Text(
                    text = "Visibility",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            item {
                Spacer(modifier = Modifier.height(10.dp))
            }

            item {
                FlowRow {
                    visibilityTypes.forEach { visibility ->
                        FilterChip(
                            selected = selected == visibility ,
                            onClick = {
                                selected = if (selected == visibility) null else visibility
                            },
                            label = { Text(visibility) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Text(
                    text = "Questions",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            item {
                Spacer(modifier = Modifier.height(10.dp))
            }


            // QUESTION LIST
            // Only shows the question text, if clicked, a sheet is pops up from the bottom
            // and the options can be modified
            if (questions.isEmpty()) {
                item {
                    Text(
                        text = "There aren't any questions created yet.",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            } else {

                itemsIndexed(questions) { index, item ->
                    CustomCard(
                        CardType.QUESTION,
                        text = item.text,
                        borderColor = MaterialTheme.colorScheme.onBackground,
                        onClick = { showQuestionDetail = true; selectedQuestion = questions[index] }
                    )
                }

            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }


            item {
                Button(
                    onClick = { showQuestionDetail = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Add question")
                }
            }
        }

        if (showQuestionDetail) {
            QuestionSheet(
                onDismiss = { showQuestionDetail = false; selectedQuestion = null },
                question = selectedQuestion
            )
        }
    }
}


@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionSheet(
    question: Question?,
    onDismiss: () -> Unit
) {

    val scrollState = rememberScrollState()

    var selectedCategory by remember { mutableStateOf<String?>(question?.category) }
    val categories = listOf("+", "Vadászati állatan", "JOGI_ÉS_IGAZGATÁSI_KÉRDÉSEK", "Vadászkutyák", "Vadászati kultúra", "Vadfajok és vadászatuk")


    ModalBottomSheet(
        containerColor = MaterialTheme.colorScheme.background,
        onDismissRequest = onDismiss,
    ) {



        val focusManager = LocalFocusManager.current
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = LocalConfiguration.current.screenHeightDp.dp * 0.9f)
                .pointerInput(Unit) {
                    detectTapGestures {
                        focusManager.clearFocus()
                    }
                }
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            Text(
                text = "Create Question",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                state = rememberTextFieldState(initialText = question?.text ?: ""),
                label = { Text("Question") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Categories",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(10.dp))

            CategoryFilterRow(
                categories,
                selectedCategory ?: "",
                onSelect = { selectedCategory = it}
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Options",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (question == null) {
                Text(
                    text = "No options have been added yet.",
                    style = MaterialTheme.typography.titleLarge
                )
            } else {
                Column {
                    question.options.forEachIndexed { index, option ->
                        val correctOptionBorderColor = if (question.correctOptionId == (index + 1).toString()) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.onBackground
                        }

                        CustomCard(
                            cardType = CardType.OPTION,
                            text = option.text,
                            borderColor = correctOptionBorderColor
                        )
                    }
                }
            }


            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Add option")
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {  },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Save")
            }

        }
    }
}


enum class CardType {
    QUESTION, OPTION
}

@Composable
fun CustomCard(
    cardType: CardType,
    text: String,
    borderColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = { }
) {
    var editText by remember { mutableStateOf(false) }

    Card(
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        modifier = modifier
            .padding(10.dp)
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick, // If its acts as a question card
                onLongClick = {
                    if (cardType == CardType.OPTION) {
                        editText = true
                    }
                }// If its acts as an option card
            )
            .border(
                width = 1.dp,
                color = borderColor,
                shape = MaterialTheme.shapes.medium
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            if (editText) {
                val focusRequester = remember { FocusRequester() }
                var hasFocused by remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }

                OutlinedTextField(
                    state = rememberTextFieldState(),
                    textStyle = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .weight(1f)
                        .focusRequester(focusRequester)
                        .onFocusChanged { focusState ->
                            if (focusState.isFocused) {
                                hasFocused = true
                            } else if (hasFocused) {
                                editText = false
                            }
                        },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        errorContainerColor = Color.Transparent
                    )
                )
            } else {
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .weight(1f)
                )
            }
        }
    }
}

@Composable
private fun CategoryFilterRow(
    categories: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    var displayPopUp by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categories.forEach { category ->
            val isSelected = category == selected
            val animatedAlpha by animateFloatAsState(
                targetValue = if (isSelected) 1f else 0f,
                animationSpec = tween(200),
                label = "chip_alpha_$category"
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                    .clickable {
                        if (category == "+") {
                            displayPopUp = true
                        } else {
                            onSelect(category)
                        }
                    }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = category,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    if (displayPopUp) {
        CategoryAdderDialog(
            onDismissRequest = { displayPopUp = false },
            onConfirmation = { displayPopUp = false } // + save category
        )
    }
}

@Composable
fun CategoryAdderDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
) {
    Dialog(
        onDismissRequest = { onDismissRequest() },
    ) {
        // Draw a rectangle shape with rounded corners inside the dialog
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Add category",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    state = rememberTextFieldState(),
                    label = { Text("New category") }
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    TextButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Dismiss")
                    }
                    TextButton(
                        onClick = { onConfirmation() },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}







@Preview
@Composable
fun OptionCardPreview() {
    MaterialTheme {
        Column {
            CustomCard(
                cardType = CardType.OPTION,
                borderColor = MaterialTheme.colorScheme.primaryContainer,
                text = "Short")
            CustomCard(
                cardType = CardType.OPTION,
                borderColor = MaterialTheme.colorScheme.onBackground,
                text = "This is a medium length option text")
            CustomCard(
                cardType = CardType.OPTION,
                borderColor = MaterialTheme.colorScheme.primaryContainer,
                text = "This is a very long option text that should demonstrate how the layout behaves when the content exceeds two lines and needs to be truncated"
            )
        }
    }
}


@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, heightDp = 2000)
@Composable
fun QuestionSheetPreview() {
    MaterialTheme {
        QuestionSheet(
            onDismiss = {},
            question = null
        )
    }
}

@Preview
@Composable
fun QuestionCardPreview() {
    MaterialTheme {
        CustomCard(
            cardType = CardType.QUESTION,
            borderColor = MaterialTheme.colorScheme.onBackground,
            text = "This is a question card."
        )
    }
}
