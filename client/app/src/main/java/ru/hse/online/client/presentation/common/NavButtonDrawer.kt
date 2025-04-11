package ru.hse.online.client.presentation.common

import android.app.Activity
import android.content.Intent
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

class NavButtonDrawer {
    @Composable
    fun Draw(
        from: Activity,
        to: Class<out Activity>,
        modifier: Modifier = Modifier,
        function: @Composable () -> Unit,
    ) {
        return Button(
            onClick = { from.startActivity(Intent(from, to)) },
            modifier = modifier
        ) {
            function()
        }
    }
}
