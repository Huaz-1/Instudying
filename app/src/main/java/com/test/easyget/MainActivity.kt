package com.test.easyget

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.test.easyget.ui.archive.ArchiveScreen
import com.test.easyget.ui.countdown.CountdownScreen
import com.test.easyget.ui.navigation.BottomNavBar
import com.test.easyget.ui.navigation.Screen
import com.test.easyget.ui.note.NoteScreen
import com.test.easyget.ui.theme.EasyGetTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EasyGetTheme{
                EasyGetApp()
            }
        }
    }
}

@Composable
fun EasyGetApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavBar(
                currentRoute = currentRoute,
                onItemClick = { screen ->
                    navController.navigate(screen.route) {
                        popUpTo(Screen.Note.route) { saveState = false }
                        launchSingleTop = true
                        restoreState = false
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Note.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // 便签编辑页（支持 ?noteId=xxx 归档编辑）
            composable(
                route = "note?noteId={noteId}",
                arguments = listOf(
                    navArgument("noteId") {
                        type = NavType.LongType
                        defaultValue = -1L
                    }
                )
            ) { backStackEntry ->
                val noteId = backStackEntry.arguments?.getLong("noteId") ?: -1L
                NoteScreen(
                    noteId = if (noteId > 0) noteId else null,
                    onSaved = { navController.navigate(Screen.Archive.route) }
                )
            }

            // 历史存档页
            composable(Screen.Archive.route) {
                ArchiveScreen(
                    onNoteClick = { noteId ->
                        navController.navigate("note?noteId=$noteId")
                    }
                )
            }

            // 倒计时页
            composable(Screen.Countdown.route) {
                CountdownScreen()
            }
        }
    }
}
