package com.example.questionaire.ui.screens.home

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.questionaire.R
import com.example.questionaire.ui.theme.HuntingQuizTheme

@Composable
fun HomeScreen(
    navigateToQuiz: (String) -> Unit,
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = hiltViewModel()
) {

    Scaffold(
        topBar = { HomeTopBar() },
        bottomBar = {},
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            homeViewModel.quizTypes.forEach { quizType ->
                HomeCard(
                    drawable = R.drawable.hunting_practices,
                    text = quizType.displayName,
                    navigate = {
                        homeViewModel.onSelectedQuizType(quizType)
                        navigateToQuiz(quizType.routeParam)
                    }
                )
            }
        }
    }
}

@Composable
fun HomeCard(
    @DrawableRes drawable: Int,
    text: String,
    navigate: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = modifier
            .padding(10.dp)
            .clickable(onClick = navigate)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Image(
                painter = painterResource(drawable),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(90.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .weight(1f)
            )
            IconButton(
                onClick = {  },
                modifier = Modifier.size(56.dp) // optional, keeps material touch target
            ) {
                Icon(
                    painter = painterResource(R.drawable.more_vert_24px),
                    contentDescription = "Download",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    modifier: Modifier = Modifier
) {
    Column {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text ="Hunting quiz",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        )
        HorizontalDivider(modifier = Modifier.fillMaxWidth())
    }
}



@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun HomeCardPreview() {
    HuntingQuizTheme {
        HomeCard(
            drawable = R.drawable.hunting_practices,
            text = "Hinting practices",
            navigate = {}
        )
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun HomeTopBarPreview() {
    HuntingQuizTheme {
        HomeTopBar()
    }
}


