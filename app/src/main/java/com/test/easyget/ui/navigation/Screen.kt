package com.test.easyget.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Timer
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    data object Note : Screen("note", "写便签", Icons.Default.Edit)
    data object Archive : Screen("archive", "存档", Icons.Default.History)
    data object Countdown : Screen("countdown", "倒计时", Icons.Default.Timer)

    companion object {
        val items = listOf(Note, Archive, Countdown)
    }
}
