package com.example.samplegdrive.ui.screen.auth

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch


class AuthViewModel(private val app: Application) : AndroidViewModel(app) {

    private val _uiState = mutableStateOf(MainScreenState())
    val uiState: State<MainScreenState> = _uiState

    private val _eventFlow = MutableSharedFlow<AuthState>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var mGoogleSignInClient: GoogleSignInClient

    init {
        mGoogleSignInClient =
            GoogleSignIn.getClient(app.applicationContext, getGoogleSignInOptions())
        checkAuthState()
    }

    private fun getGoogleSignInOptions(): GoogleSignInOptions {
        val scopeDriveAppFolder = Scope(Scopes.DRIVE_APPFOLDER)
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(scopeDriveAppFolder)
            .build()
    }

    fun signOut() {
        mGoogleSignInClient.signOut()
            .addOnCompleteListener {
                checkAuthState()
            }
    }

    fun getSignInIntent() = mGoogleSignInClient.signInIntent
    fun getLastSignedUser(): GoogleSignInAccount? {
        return GoogleSignIn.getLastSignedInAccount(app.applicationContext)
    }

    fun checkAuthState() {
        val lastUser = getLastSignedUser()
        _uiState.value =
            _uiState.value.copy(authState = if (lastUser == null) AuthState.LogOut else AuthState.SignIn)
        viewModelScope.launch {
            _eventFlow.emit(if (lastUser==null) AuthState.LogOut else AuthState.SignIn)
        }
    }

    data class MainScreenState(
        var authState: AuthState = AuthState.Loading
    )

    sealed interface AuthState {
        object Loading : AuthState
        object LogOut : AuthState
        object SignIn : AuthState
    }

    companion object {
        private val TAG = AuthViewModel::class.java.simpleName
    }
}