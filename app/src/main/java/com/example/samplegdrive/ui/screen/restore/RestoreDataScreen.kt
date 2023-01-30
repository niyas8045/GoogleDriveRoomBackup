package com.example.samplegdrive.ui.screen.restore

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.samplegdrive.util.Screen
import com.example.samplegdrive.util.getViewModelFactory
import kotlinx.coroutines.flow.collectLatest

@Composable
fun RestoreDataScreen(
    navController: NavHostController,
    viewModel: RestoreViewModel = viewModel(factory = getViewModelFactory()),
) {

    var deletingFile by rememberSaveable {
        mutableStateOf(false)
    }
    var restoringDb by rememberSaveable {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = true, block = {
        viewModel.events.collectLatest {
            when (it) {
                RestoreViewModel.RestoreScreenEvents.CreateDbFromFile -> {
                    restoringDb = false
                    navController.navigate(Screen.HomeScreen.route) {
                        popUpTo(Screen.RestoreDataScreen.route) {
                            inclusive = true
                        }
                    }
                }
                RestoreViewModel.RestoreScreenEvents.BackupDeleted -> {
                    deletingFile = false
                }
            }
        }
    })

    Scaffold(
        topBar = {
            TopAppBar(navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }, title = { Text(text = "Restore") })
        },
    ) { padding ->

        val state = viewModel.state.value
        when (state.fetchState) {
            RestoreViewModel.RestoreScreenState.FetchingList -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            RestoreViewModel.RestoreScreenState.ListFetched -> {
                if (state.fileId.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "No file to restore")
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                    ) {
                        Text(text = "Backup found", style = MaterialTheme.typography.h4)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row {
                            Button(onClick = {
                                viewModel.restoreFromDrive(state.fileId)
                                restoringDb = true
                            }) {
                                Text(text = "Restore")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(onClick = {
                                viewModel.delete(state.fileId)
                                deletingFile = true
                            }) {
                                Text(text = "Delete")
                            }
                        }
                    }
                }
            }
        }
    }

    if (deletingFile) {
        AlertDialog(
            onDismissRequest = { },
            confirmButton = {},
            title = { Text(text = "Deleting") },
            text = { CircularProgressIndicator() })
    }

    if (restoringDb) {
        AlertDialog(
            onDismissRequest = { },
            confirmButton = {},
            title = { Text(text = "Restoring") },
            text = { CircularProgressIndicator() })
    }
}