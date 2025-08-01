package ru.hse.online.client.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import ru.hse.online.client.repository.StatisticsRepository
import ru.hse.online.client.repository.storage.AppDataStore
import ru.hse.online.client.repository.storage.LocationRepository
import java.time.LocalDateTime
import java.time.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class StepCounterService : Service(), SensorEventListener {
    enum class Stats {
        STEPS,
        KCALS,
        DISTANCE,
        TIME
    }

    private val TAG: String = "APP_STEP_COUNTER_SERVICE"

    private lateinit var sensorManager: SensorManager
    private var stepSensor: Sensor? = null
    private val dataStore: AppDataStore by inject()
    private val statisticsRepository: StatisticsRepository by inject()
    private val locationRepository: LocationRepository by inject()

    private var testJob: Job? = null
    private var autoSaveJob: Job? = null
    private var autoSendJob: Job? = null
    private var dateUpdateJob: Job? = null

    private val _KKAL_PER_STEP = 0.04
    private val _KM_PER_STEP = 0.000762

    private val _LOCAL_SAVE_INTERVAL: Long = 5*1000
    private val _SERVER_SEND_INTERVAL: Long = 10*1000
    private val _DATE_UPDATE_INTERVAL: Long = 10*1000
    private val _INACTIVE_INTERVAL_MILIS = 5*1000;
    private var prevActionTime = LocalDateTime.now();

    private var isOnline = false
    private var isPaused = false

    private var _stats: MutableMap<Stats, MutableStateFlow<Double>> = mutableMapOf()

    val steps: StateFlow<Double>
        get() = getStatFlow(Stats.STEPS)
    val caloriesBurned: StateFlow<Double>
        get() = getStatFlow(Stats.KCALS)
    val distanceTraveled: StateFlow<Double>
        get() = getStatFlow(Stats.DISTANCE)
    val timeElapsed: StateFlow<Double>
        get() = getStatFlow(Stats.TIME)

    private val _stepsOnline = MutableStateFlow(0)
    val stepsOnline: StateFlow<Int> = _stepsOnline.asStateFlow()

    private val _caloriesBurnedOnline = MutableStateFlow(0.0)
    val caloriesBurnedOnline: StateFlow<Double> = _caloriesBurnedOnline.asStateFlow()

    private val _distanceTraveledOnline = MutableStateFlow(0.0)
    val distanceTraveledOnline: StateFlow<Double> = _distanceTraveledOnline.asStateFlow()

    private val _timeElapsedOnline = MutableStateFlow(0L)
    val timeElapsedOnline: StateFlow<Long> = _timeElapsedOnline.asStateFlow()

    private var _prevDate: LocalDate = LocalDate.now()

    private var _pauseAll = MutableStateFlow(false);

    private var userWeight: Int = 0
    private var userHeight: Int = 0

    private fun getStatFlow(stat: Stats): MutableStateFlow<Double> {
        return _stats.getOrPut(stat) {
            MutableStateFlow(0.0)
        }
    }

    private fun increaseStat(value: Double, stat: Stats) {
        val flow = _stats.getOrPut(stat) { MutableStateFlow(0.0) }
        flow.value += value
    }

    fun pauseAll() {
        _pauseAll.value = !_pauseAll.value
    }

    fun goOnline() {
        isOnline = true
    }

    fun pauseOnline() {
        isPaused = true
    }

    fun resumeOnline() {
        isPaused = false
    }

    fun goOffline() {
        isOnline = false
        _stepsOnline.value = 0
        _caloriesBurnedOnline.value = 0.0
        _distanceTraveledOnline.value = 0.0
        _timeElapsedOnline.value = 0

        CoroutineScope(Dispatchers.IO).launch {
            dataStore.saveValue(AppDataStore.USER_ONLINE_STEPS, 0)
            dataStore.saveValue(AppDataStore.USER_ONLINE_CALORIES, 0.0)
            dataStore.saveValue(AppDataStore.USER_ONLINE_DISTANCE, 0.0)
            dataStore.saveValue(AppDataStore.USER_ONLINE_TIME, 0)
        }
    }

    private val binder = LocalBinder()
    private lateinit var notificationManager: NotificationManager

    inner class LocalBinder : Binder() {
        fun getService(): StepCounterService = this@StepCounterService
    }

    override fun onCreate() {
        super.onCreate()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        loadUserData()
        loadSavedData()
        startForeground()
        registerSensor()

        autoSaveJob = CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                delay(_LOCAL_SAVE_INTERVAL)
                saveData()
            }
        }
        autoSendJob = CoroutineScope(Dispatchers.IO).launch {
            var prev = 0.0
            while (true) {
                delay(_SERVER_SEND_INTERVAL)
                if (prev != _stats[Stats.STEPS]?.value) {
                    statisticsRepository.sendStats(_stats)
                    prev = _stats[Stats.STEPS]!!.value
                }
            }
        }
        dateUpdateJob = CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                delay(_DATE_UPDATE_INTERVAL)
                if (!_prevDate.isEqual(LocalDate.now())) {
                    Log.e("TAG", "DayUpdate")
                    onDateUpdate()
                }
            }
        }
    }

    private fun loadUserData() {
        CoroutineScope(Dispatchers.IO).launch {
            userWeight = dataStore.getValueFlow(AppDataStore.USER_WEIGHT, 0).first()
            userHeight = dataStore.getValueFlow(AppDataStore.USER_HEIGHT, 0).first()
        }
    }

    private suspend fun onDateUpdate() {
        statisticsRepository.sendStats(_stats.toMutableMap(), _prevDate)
        Stats.entries.forEach {
            _stats[it]?.value = 0.0
        }
        _prevDate = LocalDate.now()
        dataStore.saveValue(AppDataStore.USER_PREVIOUS_DATE, _prevDate.toString())
    }

    private fun loadSavedData() {
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        CoroutineScope(Dispatchers.IO).launch {
            launch {
                _stepsOnline.value = dataStore.getValueFlow(AppDataStore.USER_ONLINE_STEPS, 0).first()
            }

            launch {
                _caloriesBurnedOnline.value = dataStore.getValueFlow(AppDataStore.USER_ONLINE_CALORIES, 0.0).first()
            }

            launch {
                _distanceTraveledOnline.value = dataStore.getValueFlow(AppDataStore.USER_ONLINE_DISTANCE, 0.0).first()
            }

            launch {
                _timeElapsedOnline.value = dataStore.getValueFlow(AppDataStore.USER_ONLINE_TIME, 0).first()
            }

            launch {
                val value = dataStore.getValueFlow(AppDataStore.USER_PREVIOUS_DATE, "").first()
                _prevDate =
                    if (value == "") LocalDate.now() else LocalDate.parse(value, dateFormatter)
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            statisticsRepository.getTodayStats().forEach { entry ->
                _stats[entry.key]?.value = entry.value
            }
        }
    }

    private fun startForeground() {
        createNotificationChannel()
        val notification = buildNotification()
        startForeground(NOTIFICATION_ID, notification, FOREGROUND_SERVICE_TYPE_HEALTH)
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "OnLine",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "OnLine pedometer service"
        }
        notificationManager.createNotificationChannel(channel)
    }

    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("OnLine")
            .setContentText("Counting your steps...")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun updateNotification(steps: Int, distance: Int) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("OnLine")
            .setContentText("steps: $steps | dist: $distance" )
            .build()
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun registerSensor() {
        stepSensor?.let {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor.type == Sensor.TYPE_STEP_DETECTOR && !_pauseAll.value) {
                Log.e(TAG, "Step detected, ${_stats[Stats.STEPS]?.value}")
                increaseStat(1.0, Stats.STEPS)
                if (isOnline) {
                    _stepsOnline.value++
                }
                updateDerivedMetrics()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onDestroy() {
        saveData()
        super.onDestroy()
        testJob?.cancel()
        testJob = null
        sensorManager.unregisterListener(this)
    }

    private var _prev = LocalDateTime.now()
    private val _DERIVED_METRICS_PENALTY = 5000

    private fun updateDerivedMetrics() {
        val now = LocalDateTime.now()
        if (Duration.between(now, _prev).toMillis() > _DERIVED_METRICS_PENALTY) {
            _prev = LocalDateTime.now()
            return
        }

        _prev = LocalDateTime.now()

        val calories = _KKAL_PER_STEP
        /*
        val calories = if (userWeight > 0 && userHeight > 0) {
            // Формула: 0.035 * вес + (скорость^2 / рост) * 0.029 * вес
            val heightMeters = userHeight / 100.0
            val currentSpeed = locationRepository.currentSpeed.value
            val kkalToMin = ((0.035 * userWeight) + ((currentSpeed * currentSpeed) / heightMeters) * 0.029 * userWeight)
            val mins = Duration.between(prevActionTime, now).toMillis()/60000f
            kkalToMin * mins
        } else {
            _KKAL_PER_STEP
        }*/

        increaseStat(calories, Stats.KCALS)
        increaseStat(_KM_PER_STEP, Stats.DISTANCE)

        val dur = Duration.between(prevActionTime, now).toMillis()
        if (dur in 1.._INACTIVE_INTERVAL_MILIS) {
            increaseStat(dur.toDouble(), Stats.TIME)
        }

        if (isOnline) {
            _caloriesBurnedOnline.value += calories
            _distanceTraveledOnline.value += _KM_PER_STEP
            if (dur in 1.._INACTIVE_INTERVAL_MILIS) {
                _timeElapsedOnline.value += dur
            }
        }
        prevActionTime = now
    }

    private fun saveData() {
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.saveValue(AppDataStore.USER_ONLINE_STEPS, _stepsOnline.value)
            dataStore.saveValue(AppDataStore.USER_ONLINE_CALORIES, _caloriesBurnedOnline.value)
            dataStore.saveValue(AppDataStore.USER_ONLINE_DISTANCE, _distanceTraveledOnline.value)
            dataStore.saveValue(AppDataStore.USER_ONLINE_TIME, _timeElapsedOnline.value)
            dataStore.saveValue(AppDataStore.USER_PREVIOUS_DATE, _prevDate.toString())
        }
    }

    companion object {
        private const val CHANNEL_ID = "online_pedometer_channel"
        private const val NOTIFICATION_ID = 1234

        fun startService(context: Context) {
            val intent = Intent(context, StepCounterService::class.java)
            context.startForegroundService(intent)
        }

        fun stopService(context: Context) {
            val intent = Intent(context, StepCounterService::class.java)
            context.stopService(intent)
        }
    }
}