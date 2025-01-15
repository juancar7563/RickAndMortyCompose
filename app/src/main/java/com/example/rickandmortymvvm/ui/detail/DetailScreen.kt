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
import androidx.compose.runtime.SideEffect
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
import com.example.rickandmortymvvm.util.commoncomponents.FullScreenLoading
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun DetailScreen(
    viewModel: DetailViewModel = hiltViewModel(),
    upPress: () -> Unit
) {
    val systemUiController = rememberSystemUiController()
    val colorNotificationBar = colorResource(R.color.soft_blue)
    val state = viewModel.state

    SideEffect {
        systemUiController.setStatusBarColor(
            color = colorNotificationBar,
            darkIcons = true
        )
    }

    DetailContent(
        character = state.character,
        upPress = upPress,
        isLoading = state.isLoading
    )
}

@Composable
private fun DetailContent(
    character: Character?,
    upPress: () -> Unit,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    Box(modifier.fillMaxSize()) {
        Column {
            DetailHeader(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                character = character
            )
            DetailBody(character)
        }
        BackButton(upPress)
    }
    if (isLoading) {
        FullScreenLoading()
    }
}

@Composable
private fun DetailHeader(
    character: Character?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.background(colorResource(R.color.soft_blue)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CharacterImage(image = character?.image)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = character?.name.orEmpty(),
            style = MaterialTheme.typography.h5,
            color = colorResource(R.color.dark_green)
        )
    }
}

@Composable
private fun DetailBody(character: Character?) {
    val properties = listOf(
        Triple(stringResource(R.string.specie), character?.species, Icons.Filled.EmojiPeople),
        Triple(stringResource(R.string.status), character?.status, Icons.Outlined.Help),
        Triple(stringResource(R.string.gender), character?.gender, Icons.Outlined.SafetyDivider),
        Triple(stringResource(R.string.first_location), character?.origin?.name, Icons.Outlined.Visibility),
        Triple(stringResource(R.string.last_location), character?.location?.name, Icons.Outlined.LocationOn)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.dark_green))
    ) {
        properties.forEach { (label, value, icon) ->
            DetailProperty(label = label, value = value, imageVector = icon)
        }
    }
}

@Composable
private fun BackButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .size(36.dp)
    ) {
        Icon(
            imageVector = mirroringBackIcon(),
            tint = colorResource(R.color.dark_green),
            contentDescription = stringResource(R.string.back_button)
        )
    }
}