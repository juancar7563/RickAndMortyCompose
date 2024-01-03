package com.example.rickandmortymvvm.ui.home

import android.widget.Toast
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.gestures.stopScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rickandmortymvvm.R
import com.example.rickandmortymvvm.domain.model.Characters
import com.example.rickandmortymvvm.ui.Screen
import com.example.rickandmortymvvm.ui.home.components.CharacterItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    onItemClicked: (Int, String) -> Unit,
    onSearchClicked: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state = viewModel.state
    val eventFlow = viewModel.eventFlow
    val scaffoldState = rememberScaffoldState()

    LaunchedEffect(key1 = true) {
        eventFlow.collectLatest { event ->
            when (event) {
                is HomeViewModel.UIEvent.ShowSnackbar -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.message
                    )
                }
            }
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            HomeTopBar(
                onSearchPressed = { onSearchClicked() }
            )
        }/*,
        bottomBar = {
            HomeBottomBar(
                showPrevious = state.showPrevious,
                showNext = state.showNext,
                onPreviousPressed = {
                    viewModel.getCharacters(false)
                },
                onNextPressed = { viewModel.getCharacters(true)}
            )
        }*/
    ) { innerPadding ->
        HomeContent(
            modifier = Modifier.padding(innerPadding),
            onItemClicked = { id, name -> onItemClicked(id, name) },
            isLoading = state.isLoading,
            charaters = state.characters,
            viewModel = viewModel
        )
    }
}

@Composable
fun HomeTopBar(
    onSearchPressed: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appBarHorizontalPadding = 4.dp
    val titleIconModifier = Modifier
        .fillMaxHeight()
        .width(48.dp - appBarHorizontalPadding)


    TopAppBar(
        backgroundColor = colorResource(id = R.color.green_morty),
        elevation = 0.dp,
        modifier = Modifier.fillMaxWidth()
    ) {

        //TopAppBar Content
        Box(Modifier.height(32.dp)) {

            //Title
            Row(
                Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.home_title),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.h6,
                    color = Color.Black,
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center)
                )
            }

            //Navigation Icon
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.weight(1f))
                CompositionLocalProvider(
                    LocalContentAlpha provides ContentAlpha.high,
                ) {
                    IconButton(
                        onClick = onSearchPressed,
                        enabled = true,
                        modifier = titleIconModifier
                    ) {
                        Icon(
                            Icons.Filled.Search,
                            contentDescription = "Search",
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    onItemClicked: (Int, String) -> Unit,
    isLoading: Boolean = false,
    charaters: List<Characters> = emptyList(),
    viewModel: HomeViewModel
) {
    val lazyListState = rememberLazyListState()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colors.surface
    ) {
        LazyColumn(
            state = lazyListState,
            contentPadding = PaddingValues(vertical = 6.dp),
            content = {
                items(charaters.size) { index ->
                    CharacterItem(
                        modifier = Modifier.fillMaxWidth(),
                        item = charaters[index],
                        screen = Screen.Home.route,
                        onItemClicked = { id, name ->
                            if (!isLoading) {
                                onItemClicked(id, name)
                            }
                        }
                    )
                }
            },
            userScrollEnabled = !isLoading
        )
        if (isLoading) {
            FullScreenLoading()
        }
    }

    val isScrollAtEnd = lazyListState.isScrollInProgress &&
            lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == lazyListState.layoutInfo.totalItemsCount - 1

    LaunchedEffect(isScrollAtEnd) {
        if (isScrollAtEnd) {
            viewModel.getCharacters(true) // Llama a la función para cargar más elementos
        }
    }
}

@Composable
private fun HomeBottomBar(
    showPrevious: Boolean,
    showNext: Boolean,
    onPreviousPressed: () -> Unit,
    onNextPressed: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                enabled = showPrevious,
                onClick = onPreviousPressed
            ) {
                Text(text = stringResource(id = R.string.previous_button))
            }
            TextButton(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                enabled = showNext,
                onClick = onNextPressed
            ) {
                Text(text = stringResource(id = R.string.next_button))
            }
        }
    }
}

@Preview
@Composable
private fun FullScreenLoading() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(0.5f)
            .background(Color.Black)
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)

    )
    {
        CircularProgressIndicator()
    }
}