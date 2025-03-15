package ru.hse.online.client.usecase

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

class DrawNavButtonUseCase {
    @Composable
    fun Execute(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
        Button(
            onClick = onClick,
            modifier = modifier
        ) {
            Text(text = text)
        }
    }
}