package ru.hse.online.client.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import ru.hse.online.client.viewModels.GroupViewModel

@Composable
fun TestScreen(groupViewModel: GroupViewModel) {
    Column {
        Text("бу испугался не бойся я хуесос")

        Groups(groupViewModel)
    }

}


@Composable
fun Groups(viewModel: GroupViewModel) {
    val connectionStatus by viewModel.connectionStatus.collectAsState()
    val groupId by viewModel.groupId.collectAsState()

    var inviteUser by remember { mutableStateOf("") }
    var joinUser by remember { mutableStateOf("") }
    var lat by remember { mutableStateOf("55.751244") }
    var lng by remember { mutableStateOf("37.618423") }

    LaunchedEffect(Unit) {
        viewModel.connect()
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Connection status
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Status: ${if (connectionStatus) "Connected" else "Disconnected"}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Group: ${if (groupId != -1L) groupId else "None"}",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    if (!connectionStatus) {
                        Spacer(modifier = Modifier.height(8.dp))
                        CircularProgressIndicator()
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Register
            Button(
                onClick = { viewModel.register() },
                enabled = connectionStatus,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Register")
            }

            Button(
                onClick = { viewModel.unregister() },
                enabled = connectionStatus,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Unregister")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Group management
            OutlinedTextField(
                value = inviteUser,
                onValueChange = { inviteUser = it },
                label = { Text("Invite user") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    viewModel.sendInvite(inviteUser)
                    inviteUser = ""
                },
                enabled = connectionStatus && inviteUser.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Send Invite")
            }

            OutlinedTextField(
                value = joinUser,
                onValueChange = { joinUser = it },
                label = { Text("Join user's group") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    viewModel.joinGroup(joinUser)
                    joinUser = ""
                },
                enabled = connectionStatus && joinUser.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Join Group")
            }

            Button(
                onClick = { viewModel.quitGroup() },
                enabled = connectionStatus && groupId != -1L,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Quit Group")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Location
            OutlinedTextField(
                value = lat,
                onValueChange = { lat = it },
                label = { Text("Latitude") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = lng,
                onValueChange = { lng = it },
                label = { Text("Longitude") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    viewModel.sendLocation(lat.toDouble(), lng.toDouble())
                },
                enabled = connectionStatus,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Update Location")
            }
        }
    }
}