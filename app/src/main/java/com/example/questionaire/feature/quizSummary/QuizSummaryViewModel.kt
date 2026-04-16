package com.example.questionaire.feature.quizSummary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.questionaire.data.local.entities.QuizAttemptEntity
import com.example.questionaire.data.repositories.QuizAttemptRepository
import com.example.questionaire.utils.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizSummaryViewModel @Inject constructor(
    private val attemptRepository: QuizAttemptRepository,
): ViewModel() {

    val uiState: StateFlow<UIState<List<QuizAttemptEntity>>> =
        attemptRepository
            .getAllAttempts()
            .map { attempts ->
                when {
                    attempts.isEmpty() -> UIState.NoData(
                        isLoading = false,
                        errorMessages = emptyList())
                    else -> UIState.HasData(
                        data = attempts,
                        isLoading = false,
                        errorMessages = emptyList()
                    )
                }
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                UIState.NoData(
                    isLoading = true,
                    errorMessages = emptyList()
                )
            )

    fun deleteAttempt(attemptId: Int) {
        viewModelScope.launch {
            attemptRepository.deleteAttempt(attemptId)
        }
    }

    fun reloadData() {

    }
}