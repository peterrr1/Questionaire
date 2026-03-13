package com.example.questionaire.ui.screens.home


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.questionaire.di.repositories.QuizQuestionRepository
import com.example.questionaire.model.QuestionCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val quizQuestionRepository: QuizQuestionRepository
) : ViewModel() {

    private val _quizTypes = MutableStateFlow<List<QuestionCategory>>(emptyList())
    val quizTypes: StateFlow<List<QuestionCategory>> = _quizTypes.asStateFlow()

    init {
        viewModelScope.launch {
            _quizTypes.value = quizQuestionRepository.getCategories()  // runs on Dispatchers.Main, suspend handles threading ✅
        }
    }


    // Track selected quiz type
    private val _selectedQuizType = MutableStateFlow<QuestionCategory?>(null)

    fun onSelectedQuizType(quizType: QuestionCategory) {
        _selectedQuizType.value = quizType
    }
}