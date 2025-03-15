package ru.hse.online.client.usecase

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ru.hse.online.client.R
import ru.hse.online.client.activity.AuthActivity
import ru.hse.online.client.activity.MainActivity
import ru.hse.online.client.interactor.AuthInteractor

class HandleAuthUseCase {
    private enum class AuthType {
        LogIn, SignUp, None
    }

    private val authFunctions = AuthInteractor()

    @Composable
    fun Execute(currentActivity: AuthActivity) {
        var email: String by rememberSaveable { mutableStateOf("") }
        var password by rememberSaveable { mutableStateOf("") }
        var authType by rememberSaveable { mutableStateOf(AuthType.None) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .paint(
                    painterResource(id = R.drawable.frost),
                    contentScale = ContentScale.Crop
                )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Greetings!",
                    modifier = Modifier.padding(32.dp),
                    style = MaterialTheme.typography.headlineMedium,

                    )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = {
                        authType = AuthType.SignUp
                    }) {
                        Text("Sign Up")
                    }

                    Button(onClick = {
                        authType = AuthType.LogIn
                    }) {
                        Text("Log In")
                    }
                }

                if (authType != AuthType.None) {
                    OutlinedTextField(
                        value = email,
                        label = { Text("Email") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        onValueChange = { email = it }
                    )

                    OutlinedTextField(
                        value = password,
                        label = { Text("Password") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        onValueChange = { password = it }
                    )

                    val context = LocalContext.current

                    Button(
                        onClick = {
                            when (authType) {
                                AuthType.LogIn -> {
                                    authFunctions.handleLogIn()
                                    context.startActivity(Intent(currentActivity, MainActivity::class.java))
                                }

                                AuthType.SignUp -> {
                                    authFunctions.handleSignUp()
                                    context.startActivity(Intent(currentActivity, MainActivity::class.java))
                                }

                                AuthType.None -> assert(false)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (authType == AuthType.LogIn) "Log In" else "Sign Up")
                    }
                }
            }
        }
    }

}