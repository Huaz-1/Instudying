package com.test.easyget.ui.countdown

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.test.easyget.data.db.AppDatabase
import com.test.easyget.data.model.Countdown
import com.test.easyget.data.repository.CountdownRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class CountdownViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CountdownRepository(
        AppDatabase.getInstance(application).countdownDao()
    )

    val countdowns: StateFlow<List<Countdown>> = repository.allCountdowns
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()

    private val _targetDays = MutableStateFlow("")
    val targetDays: StateFlow<String> = _targetDays.asStateFlow()

    // 活跃倒计时剩余时间（毫秒）
    private val _remainingMs = MutableStateFlow(0L)
    val remainingMs: StateFlow<Long> = _remainingMs.asStateFlow()

    private var timerJob: Job? = null

    fun updateTitle(text: String) { _title.value = text }
    fun updateTargetDays(days: String) { _targetDays.value = days }

    fun startCountdown() {
        val days = _targetDays.value.toIntOrNull() ?: return
        if (days <= 0) return

        viewModelScope.launch {
            val countdown = Countdown(
                title = _title.value.ifBlank { "${days}天" },
                targetDays = days,
                startTime = System.currentTimeMillis(),
                endTime = System.currentTimeMillis() + days * 24 * 60 * 60 * 1000L
            )
            repository.insert(countdown)
            _title.value = ""
            _targetDays.value = ""
            startTimer(countdown.endTime)
        }
    }

    fun startTimerForExisting(endTime: Long) {
        timerJob?.cancel()
        startTimer(endTime)
    }

    fun stopTimer() {
        timerJob?.cancel()
        _remainingMs.value = 0L
    }

    private fun startTimer(endTime: Long) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive) {
                val remaining = endTime - System.currentTimeMillis()
                _remainingMs.value = remaining.coerceAtLeast(0)
                if (remaining <= 0) break
                delay(60_000L)
            }
        }
    }

    fun deleteCountdown(countdown: Countdown) {
        viewModelScope.launch {
            // 如果删除的是正在计时的倒计时，停止计时器
            if (_remainingMs.value > 0) {
                stopTimer()
            }
            repository.delete(countdown)
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
