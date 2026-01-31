package com.example.questionaire.ui.screens.home


import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    enum class QuizType(
        val displayName: String,
        val routeParam: String
    ) {
        HUNTING_ZOOLOGY("Hunting zoology", "hunting_zoology"),
        LAW_AND_ADMINISTRATION("Law and administration", "law_and_administration"),
        HUNTING_PRACTICES("Hunting practices", "hunting_practices"),
    }

    val quizTypes: List<QuizType> = QuizType.entries.toList()

    // Track selected quiz type
    private val _selectedQuizType = MutableStateFlow<QuizType?>(null)

    fun onSelectedQuizType(quizType: QuizType) {
        _selectedQuizType.value = quizType
    }
}