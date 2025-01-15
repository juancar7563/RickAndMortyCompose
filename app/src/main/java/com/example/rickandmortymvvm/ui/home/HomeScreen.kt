package com.example.rickandmortymvvm.ui.home


import CenteredAppBar
import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.rickandmortymvvm.R
import com.example.rickandmortymvvm.domain.model.Characters
import com.example.rickandmortymvvm.ui.Screen
import com.example.rickandmortymvvm.ui.home.components.CharacterItem
import com.example.rickandmortymvvm.util.commoncomponents.FloatingButton
import com.example.rickandmortymvvm.util.commoncomponents.FullScreenLoading
import com.example.rickandmortymvvm.util.commoncomponents.isScrollingDown
import com.example.rickandmortymvvm.util.commoncomponents.isScrollingUp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    navController: NavHostController,
    onItemClicked: (Int, String) -> Unit,
    onSearchClicked: () -> Unit,
    onLogoutPressed: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val systemUiController = rememberSystemUiController()
    val colorNotificationBar = colorResource(R.color.soft_blue)
    val state = viewModel.state
    val context = LocalContext.current
    val eventFlow = viewModel.eventFlow
    val scaffoldState = rememberScaffoldState()

    SideEffect {
        systemUiController.setStatusBarColor(
            color = colorNotificationBar,
            darkIcons = true
        )
    }

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

    BackHandler {
        (context as? Activity)?.finishAndRemoveTask()
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            CenteredAppBar(
                titleImageRes = R.drawable.rickandmortytitle,
                onSearchPressed = { onSearchClicked() },
                onLogoutPressed = {
                    viewModel.signout()
                    navController.popBackStack()
                    onLogoutPressed()
                }
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
fun HomeContent(
    modifier: Modifier = Modifier,
    onItemClicked: (Int, String) -> Unit,
    isLoading: Boolean = false,
    charaters: List<Characters> = emptyList(),
    viewModel: HomeViewModel
) {
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val isFirstElementVisible = lazyListState.layoutInfo.visibleItemsInfo.firstOrNull()?.index == 0
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colors.surface
    ) {

        LazyColumn(
            modifier = Modifier.background(colorResource(id = R.color.dark_green)),
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


        ScrollVisibility(
            visible = lazyListState.isScrollingUp() && !isFirstElementVisible
        ) {
            GoToTop {
                coroutineScope.launch {
                    lazyListState.scrollToItem(0)
                }
            }
        }

        ScrollVisibility(
            visible = lazyListState.isScrollingDown() && !isFirstElementVisible
        ) {
            ShowIndexLetter(
                viewModel.listAlCharacters.get(lazyListState.firstVisibleItemIndex).name.first()
                    .toString()
            )
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
fun GoToTop(goToTop: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        FloatingButton(
            onClick = goToTop,
            modifier = Modifier
                .padding(16.dp)
                .size(40.dp)
                .align(Alignment.TopCenter), // Alineación dentro del Box
            iconPainter = painterResource(id = R.drawable.ic_arrow_black_up_foreground),
            contentDescription = "go to top"
        )
    }
}

@Composable
fun ShowIndexLetter(letter: String) {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .size(40.dp)
                .align(Alignment.TopEnd)
                .background(Color.White, shape = CircleShape)
                .border(1.dp, Color.Black, CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {},
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = letter.uppercase(),
                color = Color.Black,
                style = MaterialTheme.typography.h6 // Puedes ajustar el estilo según lo necesites
            )
        }
    }
}

@Composable
fun ScrollVisibility(
    visible: Boolean,
    onVisible: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        onVisible()
    }
}