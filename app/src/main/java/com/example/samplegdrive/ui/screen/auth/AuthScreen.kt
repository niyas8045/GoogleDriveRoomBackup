package com.example.samplegdrive.ui.screen.auth

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavOptions
import com.example.samplegdrive.util.Screen
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.flow.collectLatest

private const val TAG = "AuthScreen"

@Composable
fun AuthScreen(
    navController: NavHostController, viewModel: AuthViewModel
) {

    val state = viewModel.uiState.value

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is AuthViewModel.AuthState.SignIn -> {
                    navController.navigate(Screen.HomeScreen.route) {
                        popUpTo(Screen.AuthScreen.route) {
                            inclusive = true
                        }
                    }
                }
                else -> {}
            }
        }
    }

    val context = LocalContext.current

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                viewModel.checkAuthState()
            } else {
                Toast.makeText(context, "Auth Failed", Toast.LENGTH_LONG).show()
            }
        }

    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        when (state.authState) {
            AuthViewModel.AuthState.Loading -> {
                CircularProgressIndicator()
            }
            AuthViewModel.AuthState.LogOut -> {
                SignInScreen { launcher.launch(viewModel.getSignInIntent()) }
            }
            AuthViewModel.AuthState.SignIn -> {
            }
        }
    }
}

@Composable
fun SignInScreen(onClick: () -> Unit) {
    Column {
        Button(onClick = onClick) {
            Text(text = "SignIn with Google")
        }
    }
}
