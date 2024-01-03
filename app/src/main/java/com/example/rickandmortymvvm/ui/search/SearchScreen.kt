package com.example.rickandmortymvvm.ui.search

import android.content.Context
import android.graphics.drawable.Icon
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.stopScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rickandmortymvvm.R
import com.example.rickandmortymvvm.domain.model.Characters
import com.example.rickandmortymvvm.ui.Screen
import com.example.rickandmortymvvm.ui.home.components.CharacterItem
import com.example.rickandmortymvvm.util.UiText
import kotlinx.coroutines.launch

@Composable
fun SearchScreen (
    onItemClicked: (Int, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel(),
    context: Context
) {
    val state = viewModel.state
    val eventFlow = viewModel.eventFlow
    val scaffoldState = rememberScaffoldState()

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
                input = state.nameInput,
                isLoading = state.isLoading,
                characters = state.characters,
                getCharacters = { input -> viewModel.getSearchCharacters(input) },
                onEvent = { viewModel.onEvent(it) },
                onItemClicked = { id, name -> onItemClicked(id, name) },
                viewModel = viewModel
            )
        }
    )
}

@Composable
private fun SearchContent(
    modifier: Modifier = Modifier,
    input: String,
    isLoading: Boolean = false,
    characters: List<Characters> = emptyList(),
    getCharacters: (String) -> Unit,
    onEvent: (SearchEvent) -> Unit,
    onItemClicked: (Int, String) -> Unit,
    viewModel: SearchViewModel
) {
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Surface(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(start = 5.dp, end = 5.dp, top = 10.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = input,
                    placeholder = { Text(stringResource(R.string.name)) },
                    onValueChange = { onEvent(SearchEvent.EnteredCharacter(it)) },
                    textStyle = MaterialTheme.typography.h6,
                    trailingIcon = {
                        IconButton(onClick = { getCharacters(input) }) {
                            Icon(imageVector = Icons.Default.Search, contentDescription = null)
                        }
                    }
                )
            }
            LazyColumn(
                state = lazyListState,
                contentPadding = PaddingValues(5.dp),
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
        if (isLoading) FullScreenLoading()
    }

    val isScrollAtEnd = lazyListState.isScrollInProgress &&
            lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == lazyListState.layoutInfo.totalItemsCount - 1

    LaunchedEffect(isScrollAtEnd) {
        if (isScrollAtEnd) {
            viewModel.getMoreSearchCharacters(input)
        }

        coroutineScope.launch {
            if(isLoading){
                lazyListState.stopScroll()
            }

        }
    }
}

@Composable
private fun FullScreenLoading() {
    Box(
        modifier = Modifier.fillMaxSize()
            .alpha(0.5f)
            .background(Color.Black)
    )
    Box(
        modifier = Modifier.fillMaxSize()
            .wrapContentSize(Alignment.Center)
    )
    {
        CircularProgressIndicator()
    }
}