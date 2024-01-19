package com.example.rickandmortymvvm.ui.home


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rickandmortymvvm.R
import com.example.rickandmortymvvm.domain.model.Characters
import com.example.rickandmortymvvm.ui.Screen
import com.example.rickandmortymvvm.ui.home.components.CharacterItem
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
        }
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
    val coroutineScope = rememberCoroutineScope()

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

        AnimatedVisibility(visible = lazyListState.isScrollingUp(), enter = fadeIn(), exit = fadeOut()) {
            GoToTop {
                coroutineScope.launch {
                    lazyListState.scrollToItem(0)
                }
            }
        }

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

@Composable
fun GoToTop(goToTop: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        FloatingActionButton(
            modifier = Modifier
                .padding(16.dp)
                .size(40.dp)
                .align(Alignment.TopCenter),
            onClick = goToTop,
            backgroundColor = White, contentColor = Black
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_black_up_foreground),
                contentDescription = "go to top"
            )
        }
    }
}

//Sacar esta función a otra clase para tenerlo ordenado y que tenga mas sentido si se quiere usar en otros LazyListState
@Composable
fun LazyListState.isScrollingUp(): Boolean {
    var previousIndex by remember(this) { mutableStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableStateOf(firstVisibleItemScrollOffset) }
    return remember(this) {
        derivedStateOf {
            if (previousIndex != firstVisibleItemIndex) {
                previousIndex > firstVisibleItemIndex
            } else {
                previousScrollOffset >= firstVisibleItemScrollOffset
            }.also {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }.value
}