package ru.hse.online.client.model.map

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ru.hse.online.client.R

class StaticMapView : BaseMapView() {
    @Composable
    override fun DrawMap() {
        Column(verticalArrangement = Arrangement.Top, modifier = Modifier.padding(top = 40.dp)) {
            Image(
                painter = painterResource(id = R.drawable.meme),
                contentDescription = "Map",
                modifier = Modifier,
                contentScale = ContentScale.FillBounds
            )
        }
    }
}