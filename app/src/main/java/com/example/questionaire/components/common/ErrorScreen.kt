package com.example.questionaire.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.questionaire.utils.ErrorMessage

@Composable
fun ErrorScreen(
    onTryAgain: () -> Unit,
    errorMessages: List<ErrorMessage>,
    modifier: Modifier = Modifier
) {
    Box(modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .wrapContentSize(Alignment.Center)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            errorMessages.forEach {
                Text(
                    text = it.messageId,
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                )
            }
            Spacer(modifier = Modifier.padding(16.dp))
            Button(
                onClick = onTryAgain
            ) {
                Text(
                    text = "Try again",
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}