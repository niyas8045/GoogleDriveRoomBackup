package com.example.samplegdrive.util

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import com.example.samplegdrive.MyApp
import com.example.samplegdrive.di.ViewModelFactory

@Composable
fun getViewModelFactory(defaultArgs: Bundle? = null): ViewModelFactory {
    val repository = (LocalContext.current.applicationContext as MyApp).noteRepository
    return ViewModelFactory(repository, LocalSavedStateRegistryOwner.current, defaultArgs)
}
