package ru.hse.online.client.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.hse.online.client.repository.StatisticsRepository
import ru.hse.online.client.repository.networking.api_data.StatisticsResult
import ru.hse.online.client.services.ContextProvider
import ru.hse.online.client.services.StepCounterService
import ru.hse.online.client.services.StepCounterService.Stats
import ru.hse.online.client.services.StepServiceConnector
import java.time.DayOfWeek
import java.time.LocalDate

data class StatisticsUiState(
    val selectedStat: Stats = Stats.STEPS,
    val currentWeek: LocalDate = LocalDate.now(),
    val weekData: Map<LocalDate, Double> = emptyMap(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class StatsViewModel(
    private val connector: StepServiceConnector,
    private val contextProvider: ContextProvider,
    private val statisticsRepository: StatisticsRepository
) : ViewModel() {

    val totalSteps: StateFlow<Int> = connector.steps
    val totalCalories: StateFlow<Double> = connector.caloriesBurned
    val totalDistance: StateFlow<Double> = connector.distanceTraveled
    val totalTime: StateFlow<Long> = connector.timeElapsed

    val onlineSteps: StateFlow<Int> = connector.stepsOnline
    val onlineCalories: StateFlow<Double> = connector.caloriesBurnedOnline
    val onlineDistance: StateFlow<Double> = connector.distanceTraveledOnline
    val onlineTime: StateFlow<Long> = connector.timeElapsedOnline

    private val _isOnline = MutableStateFlow(false)
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    private val _isPaused = MutableStateFlow(false)
    val isPaused: StateFlow<Boolean> = _isPaused.asStateFlow()

    private val _isInGroup = MutableStateFlow(false)
    val isInGroup: StateFlow<Boolean> = _isInGroup.asStateFlow()

    private val _prevSixDaysStats = MutableStateFlow<MutableMap<LocalDate, Int>>(mutableMapOf())
    val prevSixDaysStats: StateFlow<MutableMap<LocalDate, Int>> = _prevSixDaysStats.asStateFlow()

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    private val dataCache = mutableMapOf<Pair<Stats, LocalDate>, Double>()

    init {
        connector.bind()
        StepCounterService.startService(contextProvider.getContext())

        viewModelScope.launch {
            when (val res = statisticsRepository.getStatistics(Stats.STEPS, LocalDate.now().minusDays(6), LocalDate.now().minusDays(1))) {
                is StatisticsResult.SuccessGet -> {
                    res.statistics.forEach {
                        _prevSixDaysStats.value[it.timestamp] = it.value.toInt()
                    }
                }
                is StatisticsResult.SuccessPost -> {}
                is StatisticsResult.Failure -> {}
            }
        }
        loadData()
    }

    private fun loadData() {
        val stat = _uiState.value.selectedStat
        val weekStart = _uiState.value.currentWeek.with(DayOfWeek.MONDAY)
        val weekEnd = weekStart.plusDays(6)
        val weekDates = (0..6).map { weekStart.plusDays(it.toLong()) }

        val allCached = weekDates.all { dataCache.containsKey(stat to it) }

        if (allCached) {
            _uiState.update { state ->
                state.copy(
                    weekData = weekDates.associateWith { date ->
                        dataCache[stat to date] ?: 0.0
                    },
                    isLoading = false,
                    errorMessage = null
                )
            }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                when (val result = statisticsRepository.getStatistics(stat, weekStart, weekEnd)) {
                    is StatisticsResult.Failure -> {
                        _uiState.update { state ->
                            state.copy(
                                errorMessage = result.message,
                                isLoading = false
                            )
                        }
                    }
                    is StatisticsResult.SuccessGet -> {
                        result.statistics.forEach { data ->
                            dataCache[stat to data.timestamp] = data.value
                        }

                        _uiState.update { state ->
                            state.copy(
                                weekData = weekDates.associateWith { date ->
                                    dataCache[stat to date] ?: 0.0
                                },
                                isLoading = false
                            )
                        }
                    }
                    is StatisticsResult.SuccessPost -> {}
                }
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(
                        errorMessage = "Network error",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun selectStat(stat: Stats) {
        _uiState.update { it.copy(selectedStat = stat) }
        loadData()
    }

    fun moveWeek(forward: Boolean) {
        _uiState.update { state ->
            val newWeek = if (forward) state.currentWeek.plusWeeks(1)
            else state.currentWeek.minusWeeks(1)
            state.copy(currentWeek = newWeek)
        }
        loadData()
    }

    fun pauseAll() {
        connector.pauseAll()
    }

    override fun onCleared() {
        connector.unbind()
        super.onCleared()
    }

    fun goOnLine() {
        _isOnline.value = true
        connector.goOnline()
    }

    fun pauseOnline() {
        if (_isOnline.value) {
            _isPaused.value = true
            connector.pauseOnline()
        }
    }

    fun resumeOnline() {
        if (_isOnline.value) {
            _isPaused.value = false
            connector.resumeOnline()
        }
    }

    fun goOffLine() {
        _isOnline.value = false
        connector.goOffline()
    }
}
