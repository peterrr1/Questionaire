package com.example.questionaire.feature.create
import android.util.Log
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.questionaire.data.repositories.NetworkRepository
import com.example.questionaire.model.Option
import com.example.questionaire.model.OptionDraft
import com.example.questionaire.model.QuestionDraft
import com.example.questionaire.model.QuizDraft
import com.example.questionaire.model.QuizInformationDraft
import com.example.questionaire.utils.DataStatus
import com.example.questionaire.utils.ErrorMessage
import com.example.questionaire.utils.Result
import com.example.questionaire.utils.ViewModelState
import dagger.Component
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ValidationError {
    data object EmptyQuizName: ValidationError()
    data object NoQuestions: ValidationError()
    data object EmptyOptionText: ValidationError()
    data object EmptyQuestionText: ValidationError()
    data class NoCorrectOptionSelected(val index: Int): ValidationError()
}

sealed interface SubmitStatus {
    data object Idle: SubmitStatus
    data object Loading: SubmitStatus
    data object Success: SubmitStatus
    data class Error(val message: String): SubmitStatus
}


@HiltViewModel(assistedFactory = CreateViewModel.Factory::class)
class CreateViewModel @AssistedInject constructor(
    @Assisted val quizId: String?,
    private val networkRepository: NetworkRepository
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(quizId: String?): CreateViewModel
    }

    private val _createViewModelState = MutableStateFlow(
        ViewModelState<QuizDraft>(isLoading = true)
    )

    private val _validationErrors = MutableStateFlow<List<ValidationError>>(emptyList())


    val uiState = _createViewModelState
        .map(ViewModelState<QuizDraft>::toUIState)
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            _createViewModelState.value.toUIState()
        )

    val validationErrors = _validationErrors.asStateFlow()

    var submitStatus by mutableStateOf<SubmitStatus>(SubmitStatus.Idle)
        private set

    // TextField states
    // QuizName field
    val quizNameState = TextFieldState()

    // QuestionText field
    val questionTextState = TextFieldState()

    val optionTextState = TextFieldState()

    // Question category selector
    private val _currentQuestionDraft = MutableStateFlow(
        QuestionDraft()
    )


    val currentQuestionDraft = _currentQuestionDraft.asStateFlow()

    // This is used for displaying/not displaying existing texts
    private val _selectedQuestion = MutableStateFlow<QuestionDraft?>(null)
    val selectedQuestion = _selectedQuestion.asStateFlow()



    init {
        if (quizId == null) {
            _createViewModelState.update {
                it.onSuccess(QuizDraft())
            }
        } else {
            loadQuizData(quizId)
        }
    }

    private fun loadQuizData(quizId: String) {
        _createViewModelState.update { it.loading() }

        viewModelScope.launch {
            _createViewModelState.update {
                when (val quizInformationResult = networkRepository.getQuizInfo(quizId)) {
                    is Result.Success -> {
                        when (val questionsResult = networkRepository.getQuestions(quizInformationResult.data.collectionId, null)) {
                            is Result.Success -> {
                                quizNameState.edit {
                                    replace(0, length, quizInformationResult.data.name)
                                }
                                it.onSuccess(
                                    data = QuizDraft(
                                        quizInformation = QuizInformationDraft(
                                            name = quizInformationResult.data.name,
                                            visibility = quizInformationResult.data.visibility,
                                            questionCategories = quizInformationResult.data.questionCategories
                                        ),
                                        questions = questionsResult.data.map { question ->
                                            QuestionDraft(
                                                type = question.type,
                                                category = question.category,
                                                text = question.text,
                                                options = question.options.map { option -> OptionDraft(option.text) },
                                                correctOptionId = question.options.indexOfFirst { option -> option.id == question.correctOptionId } + 1
                                            )
                                        }
                                    )
                                )
                            }
                            is Result.Error -> it.onError(ErrorMessage(messageId = "An error occurred: QuizCategoryViewModel"))
                        }
                    }
                    is Result.Error -> it.onError(ErrorMessage(messageId = "An error occurred: QuizCategoryViewModel"))
                }
            }
        }
    }
    fun editOption(optionIdx: Int) {
        _currentQuestionDraft.update {
            val options = _currentQuestionDraft.value.options.toMutableList().also { options ->
                options[optionIdx] = options[optionIdx].copy(text = optionTextState.text.toString())
            }
            it.copy(options = options)
        }
    }

    fun addOption() {
        // If the text field loses focus, it adds the current text to the draft
        // then adds the draft to the state
        _currentQuestionDraft.update {
            it.copy(options = it.options + OptionDraft())
        }
    }

    fun setCorrectOption(optionId: Int, isDraft: Boolean = true, questionIndex: Int = 0) {
        if (isDraft) {
            _currentQuestionDraft.update {
                it.copy(correctOptionId = optionId)
            }
        } else {
            _createViewModelState.update {
                val data = (it.status as? DataStatus.Available)?.data ?: return@update it
                val updatedQuestions = data.questions.toMutableList().also { questions ->
                    questions[questionIndex] = questions[questionIndex].copy(correctOptionId = optionId)
                }
                it.onSuccess(
                    data.copy(questions = updatedQuestions)
                )
            }
        }
    }

    fun selectQuestionCategory(category: String) {
        _currentQuestionDraft.update { it.copy(category = category) }
    }


    fun selectVisibility(visibility: String) {
        _createViewModelState.update { currentState ->
            when (val status = currentState.status) {
                is DataStatus.Empty -> currentState // unreachable, init always set Available
                is DataStatus.Available -> currentState.onSuccess(
                    status.data.copy(
                        quizInformation = status.data.quizInformation.copy(
                            visibility = visibility
                        )
                    )
                )
            }
        }
    }


    fun addQuestionCategory(category: String) {
        _createViewModelState.update { currentState ->
            when (val status = currentState.status) {
                is DataStatus.Empty -> currentState.onSuccess(
                    QuizDraft(quizInformation = QuizInformationDraft(questionCategories = listOf(category))
                ))
                is DataStatus.Available -> {
                    val updatedQuizInfo = status.data.quizInformation.copy(
                        questionCategories = status.data.quizInformation.questionCategories + category // ← new list
                    )
                    currentState.onSuccess(status.data.copy(quizInformation = updatedQuizInfo))
                }
            }
        }
    }


    fun deleteQuestionCategory(category: String) {
        _createViewModelState.update { currentState ->
            when (val status = currentState.status) {
                is DataStatus.Empty -> currentState
                is DataStatus.Available -> {
                    val updatedQuizInfo = status.data.quizInformation.copy(
                        questionCategories = status.data.quizInformation.questionCategories - category
                    )
                    currentState.onSuccess(status.data.copy(quizInformation = updatedQuizInfo))
                }
            }
        }
    }

    // Adds question to the viewModelState
    fun saveQuestion(questionIndex: Int = 0) {
        // Add
        val draft = _currentQuestionDraft.value.copy(
                text = questionTextState.text.toString())

        _createViewModelState.update { currentState ->
            val data = (currentState.status as? DataStatus.Available)?.data ?: return@update currentState
            val updatedQuestions = if (_selectedQuestion.value == null) {
                data.questions + draft
            } else {
                data.questions.toMutableList().also { questions ->
                    questions[questionIndex] = questions[questionIndex].copy(
                        text = draft.text,
                        category = draft.category,
                        correctOptionId = draft.correctOptionId,
                        options = draft.options
                    )
                }
            }
            currentState.onSuccess(
                data.copy(questions = updatedQuestions)
            )
        }
        _currentQuestionDraft.value = QuestionDraft()
        questionTextState.clearText()
    }

    fun openExistingQuestion(questionDraft: QuestionDraft) {
        _selectedQuestion.value = questionDraft
        _currentQuestionDraft.value = questionDraft
        questionTextState.edit {
            replace(0, length, questionDraft.text)
        }
    }

    fun openNewQuestion() {
        if (_selectedQuestion.value != null) {
            _currentQuestionDraft.value = QuestionDraft()
            questionTextState.edit {
                replace(0, length, "")
            }
        }
        _selectedQuestion.value = null
    }

    fun clearQuestionDraft() {
        _selectedQuestion.value = null
        questionTextState.edit {
            replace(0, length, "")
        }
    }


    // Uploads quiz info, questions, etc to the server
    fun createQuiz() {
        // Log data
        if (!validateInput()) { return } //  validateInput returns the error messages

        val data = (_createViewModelState.value.status as? DataStatus.Available)?.data!!

        Log.d("CREATE", "Quiz name: ${quizNameState.text}")
        Log.d("CREATE", "Visibility: ${data.quizInformation.visibility}")
        Log.d("CREATE", "Question categories: ${data.quizInformation.questionCategories}")
        data.questions.forEach { question ->
            Log.d("CREATE", "Question name: ${question.text}")
            Log.d("CREATE", "Question category: ${question.category}")
            Log.d("CREATE", "Question type: ${question.type}")
            Log.d("CREATE", "Question correct option: ${question.correctOptionId}")

            question.options.forEach { option ->
                Log.d("CREATE", "Option name: ${option.text}")
            }

        }

        // Save
        viewModelScope.launch {
            submitStatus = SubmitStatus.Loading
            val result = networkRepository.createQuiz(data)
            // Check whether it was successful or not
            submitStatus = when (result) {
                // If it was successful return to Home and trigger a reload
                is Result.Success -> {
                    SubmitStatus.Success
                }
                // if it wasn't display a network error message
                is Result.Error -> SubmitStatus.Error(result.exception.message ?: "Some error occurred during quiz creation.")
            }



        }

        clearQuestionDraft()
    }

    private fun validateInput(): Boolean {
        val errors = mutableListOf<ValidationError>()

        when (val state = _createViewModelState.value.status) {
            is DataStatus.Empty -> {
                errors.add(ValidationError.EmptyQuizName)
            }
            is DataStatus.Available -> {
                val data = state.data

                if (quizNameState.text.isEmpty()) {
                    errors.add(ValidationError.EmptyQuizName)
                }
                if (data.questions.isEmpty()) {
                    errors.add(ValidationError.NoQuestions)
                }

                data.questions.forEachIndexed { index, draft ->
                    if (draft.text.isEmpty()) errors.add(ValidationError.EmptyQuestionText)
                    if (draft.correctOptionId == -1) errors.add(ValidationError.NoCorrectOptionSelected(index))
                    draft.options.forEach { option ->
                        if (option.text.isEmpty()) errors.add(ValidationError.EmptyOptionText)
                    }
                }

                val updatedQuestions = data.questions.map {
                    if (it.category.isEmpty()) {
                        it.copy(type = "SINGLE_OPTION", category = "None")
                    } else {
                        it.copy(type = "SINGLE_OPTION")
                    }
                }

                val updatedCategories = if (data.quizInformation.questionCategories.isEmpty()) {
                    listOf("None")
                } else {
                    data.questions.distinctBy { it.category }.map { q -> q.category }
                }

                _createViewModelState.update { currentState ->
                    currentState.onSuccess(
                        data.copy(
                            quizInformation = data.quizInformation.copy(
                                name = quizNameState.text.toString(),
                                questionCategories = updatedCategories
                            ),
                            questions = updatedQuestions
                        )
                    )
                }
            }
        }
        _validationErrors.value = errors
        return errors.isEmpty()
    }

    // Option1: If the user navigates elsewhere, create a memo
    // Option2: Only show a verification message that the form data will be lost
    override fun onCleared() {
        super.onCleared()
        // TODO: Create a draft
    }
}
