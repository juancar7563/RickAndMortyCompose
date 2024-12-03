package com.example.rickandmortymvvm.ui.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.rickandmortymvvm.R
import com.example.rickandmortymvvm.domain.model.Characters

@Composable
fun CharacterItem(
    modifier: Modifier = Modifier,
    item: Characters,
    screen: String,
    onItemClicked: (Int, String) -> Unit
) {
    Column( // Use Column to wrap everything
        modifier = modifier
            .background(colorResource(R.color.dark_green))
            .clickable { onItemClicked(item.id, screen) }
            .padding(start = 8.dp, top = 12.dp, end = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .wrapContentHeight()
        ) {
            CharacterImageContainer(modifier = Modifier.size(64.dp)) {
                CharacterImage(item = item)
            }
            Spacer(Modifier.width(20.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = item.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = colorResource(R.color.soft_blue)
                )
                Text(
                    text = item.specie,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light,
                    color = colorResource(R.color.soft_blue)
                )
            }
        }
        Divider(
            color = colorResource(R.color.soft_blue),
            thickness = 1.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        )
    }
}

@Composable
fun CharacterImage(
    modifier: Modifier = Modifier,
    item: Characters
) {
    Box(
        modifier = modifier
            .border(
                width = 2.dp,
                color = colorResource(R.color.medium_green),
                shape = RoundedCornerShape(30.dp)
            )
            .background(colorResource(R.color.dark_green))
            .clip(CircleShape)
    ) {
        val painter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(item.image)
                .size(Size.ORIGINAL)
                .build()
        )
        Image(
            painter = painter,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun CharacterImageContainer(
    modifier: Modifier,
    content: @Composable () -> Unit
) {
    Surface(modifier.aspectRatio(1f), RoundedCornerShape(4.dp)) {
        content()
    }
}