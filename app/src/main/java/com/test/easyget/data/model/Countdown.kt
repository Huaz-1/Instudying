package com.test.easyget.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "countdowns")
data class Countdown(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String = "",
    val targetDays: Int = 1,
    val startTime: Long = System.currentTimeMillis(),
    val endTime: Long = System.currentTimeMillis() + targetDays * 24 * 60 * 60 * 1000L
)
