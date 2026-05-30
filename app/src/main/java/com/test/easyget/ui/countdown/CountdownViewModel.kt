package com.test.easyget.ui.countdown

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.test.easyget.data.db.AppDatabase
import com.test.easyget.data.model.Countdown
import com.test.easyget.data.repository.CountdownRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CountdownViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CountdownRepository(
        AppDatabase.getInstance(application).countdownDao()
    )

    val allCountdowns: StateFlow<List<Countdown>> = repository.allCountdowns
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 活跃倒计时：截止日期最近且未过期的那个
    private val _activeCountdown = MutableStateFlow<Countdown?>(null)
    val activeCountdown: StateFlow<Countdown?> = _activeCountdown.asStateFlow()

    // 活跃倒计时的剩余天数
    private val _activeRemainingDays = MutableStateFlow(0)
    val activeRemainingDays: StateFlow<Int> = _activeRemainingDays.asStateFlow()

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()

    private val _targetDays = MutableStateFlow("")
    val targetDays: StateFlow<String> = _targetDays.asStateFlow()

    init {
        // 监听数据变化，自动更新活跃倒计时（仅在数据变化时计算，非实时轮询）
        viewModelScope.launch {
            repository.allCountdowns.collect { list ->
                val now = System.currentTimeMillis()
                _activeCountdown.value = list
                    .filter { it.endTime > now }
                    .minByOrNull { it.endTime }
                // 计算剩余天数
                _activeRemainingDays.value = _activeCountdown.value?.let {
                    ((it.endTime - now) / (24 * 60 * 60 * 1000)).toInt().coerceAtLeast(0)
                } ?: 0
            }
        }
    }

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
        }
    }

    fun deleteCountdown(countdown: Countdown) {
        viewModelScope.launch {
            repository.delete(countdown)
        }
    }
}
