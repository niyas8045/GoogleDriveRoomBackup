package com.example.samplegdrive

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.samplegdrive.ui.screen.add_note.AddNoteScreen
import com.example.samplegdrive.ui.screen.auth.AuthScreen
import com.example.samplegdrive.ui.screen.auth.AuthViewModel
import com.example.samplegdrive.ui.screen.notes.HomeScreen
import com.example.samplegdrive.ui.screen.restore.RestoreDataScreen
import com.example.samplegdrive.ui.theme.SampleGDriveTheme
import com.example.samplegdrive.util.Screen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SampleGDriveTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background
                ) {
                    NavContainer()
                }
            }
        }
    }
}

@Composable
fun NavContainer(viewModel: AuthViewModel = viewModel()) {
    val navController = rememberNavController()
    var authState by rememberSaveable {
        mutableStateOf(CheckAuthState.NOT_STARTED.ordinal)
    }
    LaunchedEffect(key1 = true, block = {
        val lastUser = viewModel.getLastSignedUser()
        authState =
            if (lastUser == null) CheckAuthState.LOG_OUT.ordinal else CheckAuthState.SIGN_IN.ordinal
    })
    if (authState != CheckAuthState.NOT_STARTED.ordinal) {
        NavHost(
            navController = navController,
            startDestination = if (authState == CheckAuthState.SIGN_IN.ordinal)
                Screen.HomeScreen.route
            else Screen.AuthScreen.route
        ) {
            composable(route = Screen.AuthScreen.route) {
                AuthScreen(navController = navController, viewModel)
            }
            composable(route = Screen.HomeScreen.route) {
                HomeScreen(navController = navController, viewModel)
            }
            composable(route = Screen.AddNoteScreen.route) {
                AddNoteScreen(navController = navController)
            }
            composable(route = Screen.RestoreDataScreen.route) {
                RestoreDataScreen(navController = navController)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SampleGDriveTheme {}
}

enum class CheckAuthState {
    NOT_STARTED,
    SIGN_IN,
    LOG_OUT
}