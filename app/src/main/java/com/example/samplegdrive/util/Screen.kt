package com.example.samplegdrive.util

sealed class Screen(val route: String) {
    object HomeScreen : Screen("main_screen")
    object AuthScreen : Screen("auth_screen")
    object AddNoteScreen : Screen("add_note")
    object RestoreDataScreen : Screen("restore_data")
}
