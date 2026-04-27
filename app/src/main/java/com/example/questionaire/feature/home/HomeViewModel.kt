package com.example.questionaire.feature.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.questionaire.data.repositories.NetworkRepository
import com.example.questionaire.model.CompactQuizInfo
import com.example.questionaire.utils.ErrorMessage
import com.example.questionaire.utils.Result
import com.example.questionaire.utils.ViewModelState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val networkRepository: NetworkRepository,
): ViewModel() {

    private val _homeViewModelState = MutableStateFlow(
        ViewModelState<List<CompactQuizInfo>>(isLoading = true)
    )

    val uiState = _homeViewModelState
        .map(ViewModelState<List<CompactQuizInfo>>::toUIState)
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            _homeViewModelState.value.toUIState()
        )

    init {
        loadData()
    }

    private fun loadData() {
        _homeViewModelState.update { it.loading() }

        viewModelScope.launch {
            _homeViewModelState.update {
                when (val result = networkRepository.getQuizTypes()) {
                    is Result.Success -> it.onSuccess(result.data)
                    is Result.Error -> it.onError(ErrorMessage(messageId = "Can't load the quizzes"))
                }
            }

        }
    }

    fun reloadData() {
        loadData()
    }

}