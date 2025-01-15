package com.example.rickandmortymvvm.ui.search

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.stopScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rickandmortymvvm.R
import com.example.rickandmortymvvm.domain.model.Characters
import com.example.rickandmortymvvm.ui.Screen
import com.example.rickandmortymvvm.ui.detail.components.mirroringBackIcon
import com.example.rickandmortymvvm.ui.home.components.CharacterItem
import com.example.rickandmortymvvm.util.commoncomponents.FullScreenLoading
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch

@Composable
fun SearchScreen(
    onItemClicked: (Int, String) -> Unit,
    upPress: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel(),
    context: Context
) {
    val systemUiController = rememberSystemUiController()
    val colorNotificationBar = colorResource(R.color.green_morty)
    val state = viewModel.state
    val eventFlow = viewModel.eventFlow
    val scaffoldState = rememberScaffoldState()

    SideEffect {
        systemUiController.setStatusBarColor(
            color = colorNotificationBar,
            darkIcons = true
        )
    }

    LaunchedEffect(scaffoldState.snackbarHostState) {
        eventFlow.collect { event ->
            when (event) {
                is SearchViewModel.UIEvent.ShowSnackbar -> event.message.let {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = it.asString(context)
                    )
                }
            }
        }
    }
    Scaffold(
        modifier = modifier.fillMaxWidth(),
        scaffoldState = scaffoldState,
        content = { innerPadding ->
            SearchContent(
                modifier = Modifier.padding(innerPadding),
                input = state.collectAsState().value.nameInput,
                isLoading = state.collectAsState().value.isLoading,
                characters = state.collectAsState().value.characters,
                getCharacters = { input -> viewModel.getSearchCharacters(input) },
                onEvent = { viewModel.onEvent(it) },
                upPress = upPress,
                onItemClicked = { id, name -> onItemClicked(id, name) },
                viewModel = viewModel
            )
        }
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun SearchContent(
    modifier: Modifier = Modifier,
    input: String,
    isLoading: Boolean = false,
    characters: List<Characters> = emptyList(),
    getCharacters: (String) -> Unit,
    onEvent: (SearchEvent) -> Unit,
    onItemClicked: (Int, String) -> Unit,
    upPress: () -> Unit,
    viewModel: SearchViewModel
) {
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.dark_green))) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .background(colorResource(R.color.green_morty))
                            .padding(start = 5.dp, end = 5.dp, top = 10.dp)

                    ) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Up(upPress)
                            Text(
                                text = stringResource(id = R.string.search_title),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.h6,
                                color = colorResource(R.color.dark_green),
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 5.dp, end = 5.dp)
                        ) {
                            SearchBox(
                                query = input,
                                onQueryChange = { newQuery ->
                                    onEvent(SearchEvent.EnteredCharacter(newQuery))
                                },
                                onSearch = {
                                    getCharacters(input) // Ejecutar la búsqueda
                                },
                                hint = stringResource(R.string.name),
                                keyboardController = keyboardController,
                                circleColor = colorResource(R.color.dark_green),
                                backgroundColor = colorResource(R.color.soft_green),
                                shadowColor = colorResource(R.color.grey_search_box_shadow),
                                iconColor = colorResource(R.color.soft_green)
                            )
                        }
                    }
                    RoundedRectangleWithBackground()
                    LazyColumn(
                        state = lazyListState,
                        contentPadding = PaddingValues(5.dp),
                        modifier = Modifier
                            .padding(start = 5.dp, end = 5.dp)
                            .fillMaxHeight(),
                        content = {
                            items(characters.size) { index ->
                                CharacterItem(
                                    modifier = Modifier.fillMaxWidth(),
                                    item = characters[index],
                                    screen = Screen.Search.route,
                                    onItemClicked = { id, name -> onItemClicked(id, name) }
                                )
                            }
                        }
                    )
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
            viewModel.getMoreSearchCharacters(input)
        }

        coroutineScope.launch {
            if (isLoading) {
                lazyListState.stopScroll()
            }
        }
    }
}

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
private fun Up(upPress: () -> Unit) {
    IconButton(
        onClick = upPress,
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .size(36.dp)
    ) {
        Icon(
            imageVector = mirroringBackIcon(),
            tint = colorResource(R.color.dark_green),
            contentDescription = null
        )
    }
}

@Composable
fun RoundedRectangleWithBackground() {
    val greenBaseColor = colorResource(id = R.color.green_morty)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(20.dp)
            .clip(RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp))
            .background(greenBaseColor) // Fondo azul
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchBox(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier,
    hint: String,
    textStyle: TextStyle = TextStyle(fontSize = 18.sp),
    backgroundColor: Color = Color.Gray,
    shadowColor: Color = Color.Gray,
    borderColor: Color = Color.Gray,
    cursorColor: Color = Color.Black,
    iconColor: Color = Color.White,
    circleColor: Color = Color.Black,
    keyboardController: SoftwareKeyboardController?
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(30.dp))
            .background(backgroundColor)
            .border(
                width = 2.dp,
                color = colorResource(R.color.dark_green),
                shape = RoundedCornerShape(30.dp)
            )
            .padding(top = 4.dp, end = 4.dp, start = 20.dp, bottom = 4.dp)
            .height(45.dp)
    ) {
        if (query.isEmpty()) {
            Text(
                text = hint,
                style = textStyle,
                color = borderColor,
                modifier = Modifier.align(Alignment.CenterStart),
            )
        }
        BasicTextField(
            value = query,
            onValueChange = { newQuery ->
                onQueryChange(newQuery)
            },
            textStyle = textStyle.copy(color = Color.Black),
            cursorBrush = SolidColor(cursorColor),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Search,
                capitalization = KeyboardCapitalization.Sentences
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearch()
                    keyboardController?.hide()
                }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 32.dp)
                .align(Alignment.CenterStart)// Para dejar espacio para el icono
        )
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .clip(CircleShape)
                .background(circleColor)
                .size(45.dp) // Tamaño del círculo
                .padding(10.dp) // Espacio dentro del círculo para centrar el ícono
        ) {
            IconButton(
                onClick = { onSearch() },
                modifier = Modifier
                    .size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = iconColor
                )
            }
        }
    }
}