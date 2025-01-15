package com.example.rickandmortymvvm.util.commoncomponents

import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.painter.Painter

@Composable
fun FloatingButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = White,
    contentColor: Color = Black,
    iconPainter: Painter,
    contentDescription: String
) {
    FloatingActionButton(
        onClick = onClick,
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        modifier = modifier
    ) {
        Icon(
            painter = iconPainter,
            contentDescription = contentDescription
        )
    }
}