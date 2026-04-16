package com.example.questionaire.feature.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel = hiltViewModel()
) {

    val uiState by loginViewModel.loginState.collectAsStateWithLifecycle()

    LoginContent(
        loginViewModel = loginViewModel
    )
}

@Composable
fun LoginContent(
    loginViewModel: LoginViewModel,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
    ) {
        OutlinedTextField(
            state = loginViewModel.emailState,
            enabled = true,
            label = { Text("Email") }
        )
        Spacer(modifier = Modifier.padding(16.dp))
        OutlinedTextField(
            state = loginViewModel.passwordState,
            enabled = true,
            label = { Text("Password") }
        )
        Spacer(modifier = Modifier.padding(16.dp))
        Button(
            onClick = { loginViewModel.login() },
        ) {
            Text("Login")
        }
    }

}