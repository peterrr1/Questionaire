package com.example.questionaire.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun LoadingState(modifier: Modifier = Modifier) {
    Box (
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .wrapContentSize(Alignment.Center),
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.inverseSurface
        )
    }
}