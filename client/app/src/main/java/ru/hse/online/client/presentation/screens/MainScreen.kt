package ru.hse.online.client.presentation.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Route
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import ru.hse.online.client.viewModels.GroupViewModel
import ru.hse.online.client.viewModels.SettingsViewModel
import ru.hse.online.client.viewModels.StatsViewModel
import java.time.LocalDate
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    statsViewModel: StatsViewModel,
    settingsViewModel: SettingsViewModel,
    groupViewModel: GroupViewModel
) {
    val stepCount by statsViewModel.totalSteps.collectAsStateWithLifecycle(0)
    val calories by statsViewModel.totalCalories.collectAsStateWithLifecycle(0.0)
    val distance by statsViewModel.totalDistance.collectAsStateWithLifecycle(0.0)
    val time by statsViewModel.totalTime.collectAsStateWithLifecycle(0L)
    val dailyStepGoal by settingsViewModel.dailyStepGoal.collectAsState(initial = 6000)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "OnLine",
                            modifier = Modifier.align(Alignment.TopCenter)
                        )
                    }
                },
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                MetricsGrid(
                    stepCount = stepCount,
                    stepGoal = dailyStepGoal,
                    calories = calories,
                    distance = distance,
                    time = time,
                    onStop = {
                        statsViewModel.pauseAll()
                    }
                )
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            StepsProgress(
                statsViewModel,
                dailyStepGoal = dailyStepGoal
            )

            InvitesList(groupViewModel = groupViewModel)
        }
    }
}

@Composable
fun AdditionalMetricCard(
    icon: ImageVector,
    title: String,
    value: String,
) {
    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        modifier = Modifier
            .padding(8.dp)
            .wrapContentSize()
            .defaultMinSize(minWidth = 100.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}


@Composable
fun StepsMetricCard(
    stepCount: Int,
    stepGoal: Int,
    onStop: () -> Unit
) {
    var isPaused by remember { mutableStateOf(false) }
    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .height(200.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "%d".format(stepCount),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    fontSize = 40.sp
                )
                IconButton(
                    onClick = {
                        onStop()
                        isPaused = !isPaused
                    },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Icon(
                        imageVector = if (isPaused) Icons.Filled.PlayArrow else Icons.Filled.Pause,
                        contentDescription = "pause/resume",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            Text(
                text = "/%d Steps".format(stepGoal),
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.bodyMedium
            )
            if (isPaused) {
                Card(
                    modifier = Modifier
                        .wrapContentSize()
                        .align(Alignment.CenterHorizontally),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White, contentColor = Color.Black)
                ) {
                    Text(
                        text = "Paused",
                        color = Color.Black,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                DailyStepProgress(
                    stepCount = stepCount,
                    stepGoal = stepGoal
                )
            }
        }
    }
}


@Composable
fun MetricsGrid(
    stepCount: Int,
    stepGoal: Int,
    calories: Double,
    distance: Double,
    time: Long,
    onStop: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        StepsMetricCard(
            stepCount = stepCount,
            stepGoal = stepGoal,
            onStop = onStop
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            AdditionalMetricCard(
                icon = Icons.Default.Route,
                title = "km",
                value = "%.1f".format(distance),
            )
            AdditionalMetricCard(
                icon = Icons.Default.LocalFireDepartment,
                title = "kcal",
                value = "%.1f".format(calories),
            )
            AdditionalMetricCard(
                icon = Icons.Default.AccessTime,
                title = "time",
                value = formatTime(time),
            )
        }
    }
}

@Composable
fun DailyStepProgress(stepCount: Int, stepGoal: Int) {
    val progress = remember(stepCount, stepGoal) {
        if (stepGoal > 0) min(stepCount.toFloat() / stepGoal, 1f) else 0f
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp)
        ) {
            LinearProgressIndicator(
                progress = { 1f },
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.surfaceVariant,
            )

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxSize(),
                color = if (progress >= 1f) Color.Green else MaterialTheme.colorScheme.primary,
            )
        }

        if (progress >= 1f) {
            Text(
                text = "Daily goal achieved!",
                color = Color.Green,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun StepsProgress(statsViewModel: StatsViewModel, dailyStepGoal: Int) {
    val stepsMap by statsViewModel.prevSixDaysStats.collectAsStateWithLifecycle()
    val todaySteps by statsViewModel.totalSteps.collectAsStateWithLifecycle()
    val prevDays = remember {
        List(6) { index ->
           LocalDate.now().minusDays(index.toLong() + 1)
        }.reversed()
    }

    var avg = todaySteps;
    prevDays.forEach { date ->
        avg += stepsMap[date] ?: 0
    }
    avg /= 7;

    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        prevDays.forEach { date ->
            val steps = stepsMap[date] ?: 0
            val progress = if (dailyStepGoal > 0) min(steps.toFloat() / dailyStepGoal, 1f) else 0f
            ProgressCircle(date, progress, steps)
        }
        ProgressCircle(LocalDate.now(), if (dailyStepGoal > 0) min(todaySteps.toFloat() / dailyStepGoal, 1f) else 0f , todaySteps)
    }

    Text(
        text = "You walked $avg steps on average!",
        fontSize = 16.sp,
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
fun ProgressCircle(date: LocalDate, progress: Float, steps: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Text(
            text = date.dayOfWeek.toString().take(3),
            fontSize = 12.sp,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = { 1f },
                modifier = Modifier.size(36.dp),
                color = Color.LightGray,
                strokeWidth = 3.dp,
                trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
            )
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.size(36.dp),
                color = Color.Green,
                strokeWidth = 3.dp,
                trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
            )
        }

        Text(
            text = steps.toString(),
            fontSize = 10.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun InvitesList(groupViewModel: GroupViewModel) {
    val invites by groupViewModel.receivedInvites.collectAsStateWithLifecycle()
    Log.e("TAG1", invites.size.toString())
    if (invites.isEmpty()) {
        return
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(invites.toList()) {
            InviteCard(from = it, groupViewModel = groupViewModel)
        }
    }
}

@Composable
private fun InviteCard(from: String, groupViewModel: GroupViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = from,
                style = MaterialTheme.typography.titleMedium
            )
            IconButton(
                onClick = {
                    groupViewModel.rejectInvite(from)
                },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Reject invite"
                )
            }

            IconButton(
                onClick = {
                    groupViewModel.joinGroup(from)
                },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Join group by invite"
                )
            }
        }
    }
}

fun formatTime(millis: Long): String {
    val minutes = (millis / (1000 * 60)) % 60
    val hours = (millis / (1000 * 60 * 60))
    return "%dh %dm".format(hours, minutes)
}