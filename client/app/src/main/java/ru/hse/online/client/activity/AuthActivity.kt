package ru.hse.online.client.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import ru.hse.online.client.ui.theme.ClientTheme
import ru.hse.online.client.usecase.HandleAuthUseCase

class AuthActivity : ComponentActivity() {
    private val auth = HandleAuthUseCase()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ClientTheme(darkTheme = true) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    auth.Execute(this)
                }
            }
        }
    }

}
