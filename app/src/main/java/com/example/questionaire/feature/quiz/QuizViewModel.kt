package com.example.questionaire.feature.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.questionaire.data.local.entities.OptionEntity
import com.example.questionaire.utils.Result
import com.example.questionaire.data.local.entities.QuizAttemptEntity
import com.example.questionaire.data.local.entities.QuizResultEntity
import com.example.questionaire.data.repositories.NetworkRepository
import com.example.questionaire.data.repositories.QuizAttemptRepository
import com.example.questionaire.model.Question
import com.example.questionaire.utils.DataStatus
import com.example.questionaire.utils.ErrorMessage
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
import java.util.Date


data class QuizRouteParams(
    val collectionId: String,
    val category: String
)


@HiltViewModel(assistedFactory = QuizViewModel.Factory::class)
class QuizViewModel @AssistedInject constructor(
    @Assisted private val params: QuizRouteParams,
    private val quizAttemptRepository: QuizAttemptRepository,
    private val networkRepository: NetworkRepository
) : ViewModel() {


    @AssistedFactory
    interface Factory {
        fun create(params: QuizRouteParams): QuizViewModel
    }

    private val _quizViewModelState = MutableStateFlow(
        ViewModelState<List<Question>>(
            isLoading = true
        )
    )

    val uiState = _quizViewModelState
        .map(ViewModelState<List<Question>>::toUIState)
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            _quizViewModelState.value.toUIState(),
        )


    init {
        if (params.category == "ALL") {
            loadQuestionCategoryFromNetworkAPI(params.collectionId)
        } else {
            loadQuestionCategoryFromNetworkAPI(params.collectionId, params.category)
        }

    }


    private val _selectedQuestions: HashMap<String, String> = HashMap()

    private val currentAttempt: QuizAttemptEntity =
        QuizAttemptEntity(
            quizCategory = params.category,
            startTime = Date(),
            endTime = null
        )

    private fun loadQuestionCategoryFromNetworkAPI(type: String, category: String? = null) {
        _quizViewModelState.update { it.loading() }

        viewModelScope.launch {
            val result = networkRepository.getQuestions(type, category)
            _quizViewModelState.update {
                when (result) {
                    is Result.Success -> it.onSuccess(result.data)
                    is Result.Error -> it.onError(ErrorMessage(messageId = "An error occurred."))
                }
            }
        }
    }

    fun reloadData() {
        loadQuestionCategoryFromNetworkAPI(params.collectionId, params.category)
    }

    fun onSelectOption(questionId: String, optionId: String) {
        _selectedQuestions[questionId] = optionId
        println(_selectedQuestions)
    }

    fun onCheckAnswer(questionId: String, optionId: String): Boolean {
        val data = (_quizViewModelState.value.status as? DataStatus.Available)?.data ?: return false
        return data.find { it.id == questionId }?.correctOptionId == optionId
    }


    fun createQuizResultSnapshot() {
        val data = (_quizViewModelState.value.status as? DataStatus.Available)?.data ?: return

        viewModelScope.launch {
            currentAttempt.endTime = Date()
            currentAttempt.result = data.mapIndexed { index, question ->
                if (question.correctOptionId == _selectedQuestions[question.id]) {
                    currentAttempt.score += 1
                }
                QuizResultEntity(
                    questionId = question.id,
                    questionText = question.text,
                    correctOptionId = question.correctOptionId,
                    chosenOptionId = _selectedQuestions[question.id],
                    optionSnapshots = question.options.map {
                        OptionEntity(it.id, it.text)
                    },
                    questionOrder = index
                )
            }
            quizAttemptRepository.insertAttempt(currentAttempt)
        }
    }
}