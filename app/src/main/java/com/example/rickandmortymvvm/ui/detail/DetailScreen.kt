package com.example.rickandmortymvvm.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiPeople
import androidx.compose.material.icons.outlined.Help
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.SafetyDivider
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rickandmortymvvm.R
import com.example.rickandmortymvvm.ui.detail.components.DetailProperty
import com.example.rickandmortymvvm.ui.detail.components.mirroringBackIcon
import com.example.rickandmortymvvm.ui.detail.components.CharacterImage
import com.example.rickandmortymvvm.domain.model.Character

@Composable
fun DetailScreen(
    viewModel: DetailViewModel = hiltViewModel(),
    upPress: () -> Unit
) {
    val state = viewModel.state
    DetailContent(
        character = state.character,
        upPress = upPress,
        isLoading = state.isLoading
    )
}

@Composable
private fun DetailContent(
    modifier: Modifier = Modifier,
    character: Character?,
    upPress: () -> Unit,
    isLoading: Boolean = false
) {
    Box(modifier.fillMaxSize()) {
        Column {
            Header(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                character = character
            )
            Body(character = character)
        }
        Up(upPress)
    }
    if (isLoading) {
        FullScreenLoading()
    }
}

@Composable
private fun Header(
    modifier: Modifier = Modifier,
    character: Character?
) {
    Column(
        modifier = modifier.background(colorResource(R.color.soft_blue)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CharacterImage(image = character?.image)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = character?.name ?: "",
            style = MaterialTheme.typography.h5,
            color = colorResource(R.color.dark_green)
        )
    }
}

@Composable
private fun Body(character: Character?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.dark_green))
    ) {
        DetailProperty(label = stringResource(R.string.specie), value = character?.species, imageVector = Icons.Filled.EmojiPeople)
        DetailProperty(label = stringResource(R.string.status), value = character?.status, imageVector = Icons.Outlined.Help)
        DetailProperty(label = stringResource(R.string.gender), value = character?.gender, imageVector = Icons.Outlined.SafetyDivider)
        DetailProperty(label = stringResource(R.string.first_location), value = character?.origin?.name, imageVector = Icons.Outlined.Visibility)
        DetailProperty(label = stringResource(R.string.last_location), value = character?.location?.name, imageVector = Icons.Outlined.LocationOn)
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