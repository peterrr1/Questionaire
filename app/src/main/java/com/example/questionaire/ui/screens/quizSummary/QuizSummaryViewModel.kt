package com.example.questionaire.ui.screens.quizSummary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.questionaire.data.local.db.entities.QuizResultSnapshot
import com.example.questionaire.di.repositories.QuizAttemptRepository
import com.example.questionaire.model.QuestionCategory
import com.example.questionaire.utils.ErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

sealed interface QuizResultsUIState {
    val isLoading: Boolean

    val errorMessages: List<ErrorMessage>

    data class NoResults(
        override val isLoading: Boolean,
        override val errorMessages: List<ErrorMessage>
    ) : QuizResultsUIState

    data class HasResults(
        val results: List<QuizAttemptSnapshot>,
        override val isLoading: Boolean,
        override val errorMessages: List<ErrorMessage>
    ): QuizResultsUIState
}


data class QuizAttemptSnapshot(
    val attemptId: Int,
    val quizType: QuestionCategory,
    val startTime: Date,
    var endTime: Date?,
    var result: List<QuizResultSnapshot>
)

@HiltViewModel
class QuizSummaryViewModel @Inject constructor(
    private val attemptRepository: QuizAttemptRepository,
): ViewModel() {

    val uiState: StateFlow<QuizResultsUIState> =
        attemptRepository
            .getAllAttempts()
            .map { attempts ->
                if (attempts.isEmpty()) {
                    QuizResultsUIState.NoResults(
                        isLoading = false,
                        errorMessages = emptyList()
                    )
                } else {
                    val results = attempts.map { attempt ->
                        QuizAttemptSnapshot(
                            attemptId = attempt.attemptId,
                            quizType = attempt.quizType,
                            startTime = attempt.startTime,
                            endTime = attempt.endTime,
                            result = attempt.result
                        )
                    }
                    QuizResultsUIState.HasResults(
                        results = results,
                        isLoading = false,
                        errorMessages = emptyList()
                    )
                }
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                QuizResultsUIState.NoResults(
                    isLoading = true,
                    errorMessages = emptyList()
                )
            )

    fun deleteAttempt(attemptId: Int) {
        viewModelScope.launch {
            attemptRepository.deleteAttempt(attemptId)
        }
    }
}