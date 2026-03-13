package com.example.questionaire.ui.screens.quiz

import androidx.annotation.StringRes
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.questionaire.R
import com.example.questionaire.data.local.db.entities.Option
import com.example.questionaire.utils.Result
import com.example.questionaire.data.local.db.entities.QuizAttempt
import com.example.questionaire.data.local.db.entities.QuizResultSnapshot
import com.example.questionaire.data.local.questions.QuestionsRepository
import com.example.questionaire.di.repositories.QuizAttemptRepository
import com.example.questionaire.model.Question
import com.example.questionaire.model.QuestionCategory
import com.example.questionaire.ui.screens.home.HomeViewModel
import com.example.questionaire.ui.screens.quizSummary.QuestionResultCard
import com.example.questionaire.utils.ErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID
import javax.inject.Inject


sealed interface QuizUIState {
    val isLoading: Boolean
    val errorMessages: List<ErrorMessage>

    data class NoQuestions(
        override val isLoading: Boolean,
        override val errorMessages: List<ErrorMessage>
    ) : QuizUIState

    data class HasQuestions(
        val questions: List<Question>,
        override val isLoading: Boolean,
        override val errorMessages: List<ErrorMessage>
    ) : QuizUIState
}



private data class QuizViewModelState(
    val questions: List<Question>? = null,
    val isLoading: Boolean = false,
    val errorMessages: List<ErrorMessage> = emptyList()
) {

    fun toUIState(): QuizUIState = if (questions == null) {
        QuizUIState.NoQuestions(
            isLoading = isLoading,
            errorMessages = errorMessages
        )
    } else {
        QuizUIState.HasQuestions(
            questions = questions,
            isLoading = isLoading,
            errorMessages = errorMessages
        )
    }
}


@HiltViewModel
class QuizViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val questionsRepository: QuestionsRepository,
    private val quizAttemptRepository: QuizAttemptRepository,

) : ViewModel() {

    private val _quizType: String = savedStateHandle["quizType"]
        ?: error("quizType missing from SavedStateHandle")

    private val _viewModelState = MutableStateFlow(
        QuizViewModelState(
            isLoading = true
        )
    )

    val uiState = _viewModelState
        .map(QuizViewModelState::toUIState)
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            _viewModelState.value.toUIState(),
        )


    val quizTypeDisplayName: QuestionCategory = QuestionCategory.fromRouteParam(_quizType)


    init {
        loadQuestionCategory(QuestionCategory.fromRouteParam(_quizType))
    }


    private val _selectedQuestions: HashMap<String, String> = HashMap()

    private val currentAttempt: QuizAttempt =
        QuizAttempt(
            quizType = QuestionCategory.fromRouteParam(_quizType),
            startTime = Date(),
            endTime = null,
            score = 0
        )

    fun loadQuestionCategory(category: QuestionCategory) {
        _viewModelState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            println(category)
            val result = questionsRepository.getQuestionCategory(category)
//            val result = if (category == QuestionCategory.ALL_CATEGORIES) {
//                questionsRepository.getAllQuestions()
//            } else {
//                questionsRepository.getQuestionCategory(category)
//            }
            _viewModelState.update {
                when (result) {
                    is Result.Success -> it.copy(
                        questions = result.data,
                        isLoading = false
                    )

                    is Result.Error -> {
                        val errorMessages = it.errorMessages + ErrorMessage(
                            id = UUID.randomUUID().mostSignificantBits,
                            messageId = R.string.can_t_load_the_questions
                        )
                        it.copy(errorMessages = errorMessages, isLoading = false)
                    }
                }
            }
        }
    }

    fun onCheckAnswer(questionId: String, optionId: String): Boolean {
        return _viewModelState.value.questions?.find { it.id == questionId }?.correctOptionId == optionId
    }

    fun onSelectOption(questionId: String, optionId: String) {
        _selectedQuestions[questionId] = optionId
        println(_selectedQuestions)
    }


    fun createQuizResultSnapshot() {

        viewModelScope.launch {
            currentAttempt.endTime = Date()

            currentAttempt.result = _viewModelState.value.questions!!.mapIndexed { index, question ->
                if (question.correctOptionId == _selectedQuestions[question.id]) {
                    currentAttempt.score += 1
                }
                QuizResultSnapshot(
                    questionId = question.id,
                    questionText = question.text,
                    correctOptionId = question.correctOptionId,
                    chosenOptionId = _selectedQuestions[question.id],
                    optionSnapshots = question.options.map {
                        Option(it.id, it.text)
                    },
                    questionOrder = index
                )
            }
            quizAttemptRepository.insertAttempt(currentAttempt)
        }
    }
}