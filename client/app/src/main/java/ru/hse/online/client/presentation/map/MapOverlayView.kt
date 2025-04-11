package ru.hse.online.client.presentation.map

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SupervisedUserCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import ru.hse.online.client.presentation.MainActivity
import ru.hse.online.client.presentation.PedometerViewModel
import ru.hse.online.client.presentation.common.NavButtonDrawer
import ru.hse.online.client.presentation.friendlist.FriendListView
import ru.hse.online.client.presentation.routelist.RouteListView
import ru.hse.online.client.presentation.settings.SettingsView

class MapOverlayView(private val currentActivity: ComponentActivity) {

    companion object {
        private val paddingTop = 30.dp
        private val paddingStart = 10.dp
        private val paddingEnd = 10.dp
    }

    @Composable
    fun Draw(viewModel: PedometerViewModel = koinViewModel()) {
        val stepCount by viewModel.totalSteps.collectAsStateWithLifecycle(0)

        val navButtonDrawer = NavButtonDrawer()

        Column(
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.padding(top = paddingTop, start = paddingStart, end = paddingEnd)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.background(color = Color(0x77000000))) {
                    Text(
                        text = "$stepCount steps today",
                        fontSize = 16.sp,
                        style = TextStyle(
                            shadow = Shadow(
                                blurRadius = 2.0f,
                                offset = Offset(2.0f, 5.0f)
                            )
                        )
                    )
                }

                navButtonDrawer.Draw(
                    from = currentActivity,
                    to = SettingsView::class.java,
                ) {
                    Icon(Icons.Filled.Settings, contentDescription = "Settings")
                }
            }
        }

        Column(
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier.padding(bottom = 70.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth()
                    .padding(start = 60.dp, end = 60.dp)
            ) {
                navButtonDrawer.Draw(
                    from = currentActivity,
                    to = FriendListView::class.java,
                    function = {
                        Icon(Icons.Filled.SupervisedUserCircle, contentDescription = "Settings")
                    }
                )

                navButtonDrawer.Draw(
                    from = currentActivity,
                    to = MainActivity::class.java,
                    modifier = Modifier
                        .scale(1.3f)
                        .padding(bottom = 40.dp)
                ) {
                    Icon(Icons.Filled.RocketLaunch, contentDescription = "Settings")
                }

                navButtonDrawer.Draw(
                    currentActivity,
                    RouteListView::class.java
                ) {
                    Icon(Icons.Filled.Route, contentDescription = "Settings")
                }
            }
        }
    }
}
