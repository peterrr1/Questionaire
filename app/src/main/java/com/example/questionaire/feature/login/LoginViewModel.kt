package com.example.questionaire.feature.login

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.questionaire.data.repositories.AuthRepository
import com.example.questionaire.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LoginState {
    data object Idle: LoginState()
    data object Loading: LoginState()
    data object Success: LoginState()
    data class Error(val error: String): LoginState()
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {

    val emailState = TextFieldState()
    val passwordState = TextFieldState()

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState = _loginState.asStateFlow()

    fun login() {
        _loginState.value = LoginState.Loading
        viewModelScope.launch {
            val response = authRepository.login(emailState.text.toString(), passwordState.text.toString())
            when (response) {
                is Result.Success -> _loginState.value = LoginState.Success
                is Result.Error -> _loginState.value = LoginState.Error(
                    response.exception.message.toString()
                )
            }
        }
    }
}