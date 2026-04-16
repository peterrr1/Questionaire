package com.example.questionaire.feature.home

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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.example.questionaire.R
import com.example.questionaire.components.common.CustomCircularProgressIndicator
import com.example.questionaire.components.common.CustomPullToRefreshBox
import com.example.questionaire.components.common.ErrorScreen
import com.example.questionaire.components.common.LoadingState
import com.example.questionaire.model.CompactQuizInfo
import com.example.questionaire.utils.UIState
import com.example.questionaire.utils.hasError
import com.example.questionaire.utils.isRefreshing


/*

Home Screen should list the available quizzes
The user can search for a quiz
There should be recommendations
Also the user can sort by quiz categories

*/

@Composable
fun HomeScreen(
    navigateToQuizType: (String) -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel()
) {

    val uiState: UIState<List<CompactQuizInfo>> by homeViewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is UIState.NoData -> when {
            state.isLoading -> LoadingState()
            state.hasError -> ErrorScreen(
                onTryAgain = { homeViewModel.reloadData() },
                errorMessages = state.errorMessages
            )
        }
        is UIState.HasData -> {
            CustomPullToRefreshBox(
                isRefreshing = state.isLoading,
                onRefresh = { homeViewModel.reloadData() }
            ) {
                HomeScreen(
                    quizTypes = state.data,
                    navigateToQuizType = navigateToQuizType
                )
            }
        }
    }
}


@Composable
fun HomeScreen(
    quizTypes: List<CompactQuizInfo>,
    navigateToQuizType: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
    ) {
        quizTypes.forEach { quizType ->
            QuizTypeCard(
                url = quizType.displayImageUrl,
                placeholderDrawable = R.drawable.hunting_practices,
                text = quizType.name,
                navigate = {
                    navigateToQuizType(quizType.id)
                }
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun QuizTypeCard(
    url: String,
    text: String,
    navigate: () -> Unit,
    modifier: Modifier = Modifier,
    @DrawableRes placeholderDrawable: Int,
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.secondaryContainer,
        modifier = modifier
            .padding(10.dp)
            .clickable(onClick = navigate)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            GlideImage(
                model = url,
                contentScale = ContentScale.Crop,
                loading = placeholder(resourceId = placeholderDrawable),
                contentDescription = null,
                modifier = Modifier
                    .size(92.dp)
                    .clip(MaterialTheme.shapes.small)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
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
    title: String
) {
    CenterAlignedTopAppBar(
        title = {
            Text(text = title)
        }
    )
}