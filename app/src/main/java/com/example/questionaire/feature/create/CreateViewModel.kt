package com.example.questionaire.feature.create

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.questionaire.R
import com.example.questionaire.data.network.services.QuizApiService
import com.example.questionaire.data.repositories.NetworkRepository
import com.example.questionaire.model.Question
import com.example.questionaire.utils.DataStatus
import com.example.questionaire.utils.ViewModelState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CreateViewModel @Inject constructor(
    private val networkRepository: NetworkRepository
) : ViewModel() {
    private val _createViewModelState = MutableStateFlow(
        ViewModelState<List<Question>>(isLoading = true)
    )


    private val questions: List<Question>
        get() = when (val status = _createViewModelState.value.status) {
            is DataStatus.Empty -> emptyList()
            is DataStatus.Available -> status.data
        }


    val uiState = _createViewModelState
        .map(ViewModelState<List<Question>>::toUIState)
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            _createViewModelState.value.toUIState()
        )

    // Adds question to the viewModelState
    fun addQuestion(newQuestion: Question) {
        _createViewModelState.update { currentState ->
            val updatedList = when (val status = currentState.status) {
                is DataStatus.Empty -> listOf(newQuestion)
                is DataStatus.Available -> status.data + newQuestion
            }
            currentState.onSuccess(updatedList)
        }
    }

    // Uploads quiz info, questions, etc to the server
    fun createQuiz() {

    }

    override fun onCleared() {
        super.onCleared()
        // TODO: Create a draft
    }
}
