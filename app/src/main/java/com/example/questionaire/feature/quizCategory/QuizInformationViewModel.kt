package com.example.questionaire.feature.quizCategory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.questionaire.data.repositories.NetworkRepository
import com.example.questionaire.model.DetailedQuizInfo
import com.example.questionaire.utils.ErrorMessage
import com.example.questionaire.utils.Result
import com.example.questionaire.utils.ViewModelState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch



@HiltViewModel(assistedFactory = QuizInformationViewModel.Factory::class)
class QuizInformationViewModel @AssistedInject constructor(
    @Assisted private val quizId: String,
    private val networkRepository: NetworkRepository,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(type: String): QuizInformationViewModel
    }

    private val _quizInfoViewModelState = MutableStateFlow(
        ViewModelState<DetailedQuizInfo>(isLoading = true)
    )
    val quizInformation = _quizInfoViewModelState
        .map(ViewModelState<DetailedQuizInfo>::toUIState)
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            initialValue = _quizInfoViewModelState.value.toUIState()
        )

    init {
        loadData(quizId)
    }

    fun reloadData() {
        loadData(quizId)
    }

    fun deleteQuiz(quizId: String) {
        viewModelScope.launch {
            networkRepository.deleteQuiz(quizId)
        }
    }

    private fun loadData(quizId: String) {
        _quizInfoViewModelState.update { it.loading() }

        viewModelScope.launch {
            _quizInfoViewModelState.update {
                when (val result = networkRepository.getQuizInfo(quizId)) {
                    is Result.Success -> it.onSuccess(result.data)
                    is Result.Error -> it.onError(ErrorMessage(messageId = "An error occurred: QuizCategoryViewModel"))
                }
            }
        }
    }
}