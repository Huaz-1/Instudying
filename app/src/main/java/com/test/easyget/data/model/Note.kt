package com.test.easyget.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val content: String = "",
    val fontSize: Int = 16,
    val fontColor: Int = 0xFF000000.toInt(),
    val isBold: Boolean = false,
    val isItalic: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
