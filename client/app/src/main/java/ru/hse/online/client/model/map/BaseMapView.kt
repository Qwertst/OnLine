package ru.hse.online.client.model.map

import androidx.compose.runtime.Composable

abstract class BaseMapView {
    @Composable
    abstract fun DrawMap()
}